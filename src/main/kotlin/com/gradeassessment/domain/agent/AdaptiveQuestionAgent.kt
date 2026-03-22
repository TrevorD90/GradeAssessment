package com.gradeassessment.domain.agent

import com.gradeassessment.data.questionbank.QuestionRepository
import com.gradeassessment.domain.model.*

class AdaptiveQuestionAgent(
    private val questionRepository: QuestionRepository
) {

    fun selectNextQuestion(state: AdaptiveState, subject: Subject): Question? {
        val available = questionRepository.getQuestionsBySubject(subject)
        return IrtEngine.selectNextQuestion(
            theta = state.currentTheta,
            availableQuestions = available,
            askedIds = state.questionsAsked,
            lastBloomsLevel = state.lastBloomsLevel,
            lastQuestionType = state.lastQuestionType
        )
    }

    fun selectFluencyQuestion(subtest: ReadingSubtest): Question? {
        return questionRepository.getReadingFluencyQuestions(subtest).firstOrNull()
    }

    fun selectReadingComprehensionQuestion(state: AdaptiveState): Question? {
        val available = questionRepository.getReadingComprehensionQuestions()
        return IrtEngine.selectNextQuestion(
            theta = state.currentTheta,
            availableQuestions = available,
            askedIds = state.questionsAsked,
            lastBloomsLevel = state.lastBloomsLevel,
            lastQuestionType = state.lastQuestionType
        )
    }

    fun updateState(
        state: AdaptiveState,
        question: Question,
        correct: Boolean,
        timeMs: Long,
        index: Int
    ): AdaptiveState {
        val response = StudentResponse(
            questionId = question.id,
            subject = question.subject,
            subtest = question.readingSubtest,
            studentAnswer = "",
            correct = correct,
            timeToAnswerMs = timeMs,
            thetaAtTime = state.currentTheta,
            questionIndex = index
        )

        val allResponses = state.responses + response
        val irtPairs = allResponses.map { r ->
            val q = questionRepository.getQuestionById(r.questionId)
            val params = q?.irtParameters ?: IrtParameters()
            params to r.correct
        }

        val newTheta = IrtEngine.estimateTheta(irtPairs, state.currentTheta)
        val se = IrtEngine.standardError(newTheta, irtPairs)

        return state.copy(
            currentTheta = newTheta,
            standardError = se,
            responses = allResponses,
            questionsAsked = state.questionsAsked + question.id,
            lastBloomsLevel = question.bloomsLevel,
            lastQuestionType = question.type
        )
    }

    fun shouldTerminate(state: AdaptiveState): Boolean {
        return IrtEngine.shouldTerminate(state)
    }
}
