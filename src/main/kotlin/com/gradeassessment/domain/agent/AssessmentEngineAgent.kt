package com.gradeassessment.domain.agent

import com.gradeassessment.data.questionbank.QuestionRepository
import com.gradeassessment.data.repository.SessionRepository
import com.gradeassessment.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AssessmentEngineAgent(
    private val adaptiveAgent: AdaptiveQuestionAgent,
    private val sessionRepository: SessionRepository,
    private val questionRepository: QuestionRepository
) {
    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()

    private val _adaptiveState = MutableStateFlow(AdaptiveState(currentTheta = 0.0))
    val adaptiveState: StateFlow<AdaptiveState> = _adaptiveState.asStateFlow()

    private var currentSubject: Subject = Subject.MATH
    private var questionIndex = 0
    private var sessionId: Long = 0
    private val subjectResponses = mutableMapOf<Subject, MutableList<StudentResponse>>()

    /** Clears all per-session data. Must be called between students. */
    fun reset() {
        // Previously this map survived into the next session, so a new student's report listed
        // the previous student's missed questions for any subject both had taken.
        subjectResponses.clear()
        _currentQuestion.value = null
        _adaptiveState.value = AdaptiveState(currentTheta = 0.0)
        questionIndex = 0
        sessionId = 0
    }

    fun startSubject(subject: Subject, age: Int, sessionId: Long) {
        this.currentSubject = subject
        this.sessionId = sessionId
        this.questionIndex = 0
        _adaptiveState.value = AdaptiveState.fromAge(age)
        // A subject restarted within a session starts from a clean slate.
        subjectResponses[subject] = mutableListOf()
        deliverNextQuestion(subject, age)
    }

    fun deliverNextQuestion(subject: Subject, age: Int): Boolean {
        val state = _adaptiveState.value

        if (adaptiveAgent.shouldTerminate(state) && questionIndex > 0) {
            _currentQuestion.value = null
            return false
        }

        val question = when {
            subject == Subject.READING && age <= FLUENCY_MAX_AGE ->
                // Early readers first take each foundational-skills fluency subtest once,
                // then continue with adaptive comprehension questions.
                getNextFluencySubtest(state)?.let { adaptiveAgent.selectFluencyQuestion(it, state) }
                    ?: adaptiveAgent.selectReadingComprehensionQuestion(state)
            else -> adaptiveAgent.selectNextQuestion(state, subject)
        }

        if (question != null) {
            _currentQuestion.value = question
            return true
        }

        _currentQuestion.value = null
        return false
    }

    fun recordAnswer(
        question: Question,
        studentAnswer: String,
        correct: Boolean,
        timeMs: Long
    ): AdaptiveState {
        val response = StudentResponse(
            questionId = question.id,
            subject = question.subject,
            subtest = question.readingSubtest,
            studentAnswer = studentAnswer,
            correct = correct,
            timeToAnswerMs = timeMs,
            thetaAtTime = _adaptiveState.value.currentTheta,
            questionIndex = questionIndex
        )

        sessionRepository.saveResponse(sessionId, response)
        subjectResponses.getOrPut(currentSubject) { mutableListOf() }.add(response)

        val newState = adaptiveAgent.updateState(_adaptiveState.value, question, response)
        _adaptiveState.value = newState
        questionIndex++

        return newState
    }

    fun computeSubjectScore(subject: Subject, age: Int): ScoreBundle {
        val state = _adaptiveState.value
        return when (subject) {
            Subject.CRITICAL_THINKING -> IrtEngine.computeCtScoreBundle(
                theta = state.currentTheta,
                se = state.standardError,
                responses = state.responses,
                age = age
            )
            Subject.IQ_ASSESSMENT -> IrtEngine.computeIqScoreBundle(
                theta = state.currentTheta,
                se = state.standardError,
                responses = state.responses,
                age = age
            )
            else -> IrtEngine.computeScoreBundle(
                theta = state.currentTheta,
                se = state.standardError,
                responses = state.responses,
                age = age
            )
        }
    }

    fun getSubjectResponses(subject: Subject): List<StudentResponse> {
        return subjectResponses[subject] ?: emptyList()
    }

    fun getMissedQuestions(subject: Subject): List<MissedQuestion> {
        val responses = subjectResponses[subject] ?: return emptyList()
        return responses.filter { !it.correct }.mapNotNull { r ->
            val question = questionRepository.getQuestionById(r.questionId)
            question?.let {
                MissedQuestion(
                    questionId = it.id,
                    prompt = it.prompt,
                    studentAnswer = r.studentAnswer,
                    correctAnswer = it.correctAnswerText(),
                    standardCode = it.standardCode
                )
            }
        }
    }

    // Previously a single "fluency done" flag flipped after the FIRST subtest, so Phoneme
    // Segmentation and Nonsense Word fluency were never delivered. Each subtest is now
    // tracked individually by getNextFluencySubtest.
    private fun getNextFluencySubtest(state: AdaptiveState): ReadingSubtest? {
        val completedSubtests = state.responses.mapNotNull { it.subtest }.toSet()
        val fluencySubtests = listOf(
            ReadingSubtest.LETTER_NAMING,
            ReadingSubtest.PHONEME_SEGMENTATION,
            ReadingSubtest.NONSENSE_WORD
        )
        return fluencySubtests.firstOrNull { it !in completedSubtests }
    }

    companion object {
        /** Oldest age (grade 2) that receives the K-2 foundational fluency subtests. */
        const val FLUENCY_MAX_AGE = 7
    }
}
