package com.gradeassessment.ui.screen.assessment

import com.gradeassessment.domain.agent.OrchestratorAgent
import com.gradeassessment.domain.agent.AssessmentEngineAgent
import com.gradeassessment.domain.agent.ReportGeneratorAgent
import com.gradeassessment.domain.agent.ResultsAnalysisAgent
import com.gradeassessment.domain.model.*
import com.gradeassessment.ui.navigation.AppScreen
import com.gradeassessment.ui.screen.results.ExportStatus
import com.gradeassessment.util.PdfExportHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// [DECISION: canonicalOrder expanded to 9 subjects. AppSettings consulted only in startFullAssessment.]

class AssessmentViewModel(
    private val orchestrator: OrchestratorAgent,
    private val engine: AssessmentEngineAgent,
    private val analysisAgent: ResultsAnalysisAgent,
    private val reportGenerator: ReportGeneratorAgent
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _screen = MutableStateFlow<AppScreen>(AppScreen.Welcome)
    val screen: StateFlow<AppScreen> = _screen.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()

    private val _currentSubject = MutableStateFlow(Subject.MATH)
    val currentSubject: StateFlow<Subject> = _currentSubject.asStateFlow()

    private val _questionIndex = MutableStateFlow(0)
    val questionIndex: StateFlow<Int> = _questionIndex.asStateFlow()

    private val _timeRemaining = MutableStateFlow(240)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _assessmentResult = MutableStateFlow<AssessmentResult?>(null)
    val assessmentResult: StateFlow<AssessmentResult?> = _assessmentResult.asStateFlow()

    private val _subjectScores = MutableStateFlow<Map<Subject, ScoreBundle>>(emptyMap())
    val subjectScores: StateFlow<Map<Subject, ScoreBundle>> = _subjectScores.asStateFlow()

    private val _exportStatus = MutableStateFlow<ExportStatus>(ExportStatus.Idle)
    val exportStatus: StateFlow<ExportStatus> = _exportStatus.asStateFlow()

    private val _selectedSubjects = MutableStateFlow(Subject.entries.toList())
    val selectedSubjects: StateFlow<List<Subject>> = _selectedSubjects.asStateFlow()

    private var isFullAssessment = true
    private var studentName = ""
    private var studentAge = 8
    private var questionStartTime = System.currentTimeMillis()
    private var timerJob: Job? = null

    // Canonical order used to filter selections into the correct sequence (all 9 subjects)
    private val canonicalOrder = listOf(
        Subject.MATH, Subject.READING, Subject.WRITING,
        Subject.SCIENCE, Subject.ARTS, Subject.WORLD_HISTORY, Subject.SOCIAL_STUDIES,
        Subject.CRITICAL_THINKING, Subject.IQ_ASSESSMENT
    )

    fun startFullAssessment() {
        isFullAssessment = true
        // Consult AppSettings for which optional subjects to include
        _selectedSubjects.value = AppSettings.fullAssessmentSubjects()
        _screen.value = AppScreen.StudentProfile
    }

    fun navigateToSettings() {
        _screen.value = AppScreen.Settings
    }

    fun startSingleSubjectAssessment() {
        isFullAssessment = false
        _screen.value = AppScreen.StudentProfile
    }

    fun startAssessment(name: String, age: Int) {
        studentName = name
        studentAge = age
        orchestrator.startSession(name, age)
        if (isFullAssessment) {
            val subjects = AppSettings.fullAssessmentSubjects()
            orchestrator.setSubjectOrder(subjects)
            _selectedSubjects.value = subjects
            _screen.value = AppScreen.Instructions
        } else {
            _screen.value = AppScreen.SubjectSelection
        }
    }

    fun confirmSubjectSelection(subjects: List<Subject>) {
        // Filter to canonical order so subjects run in the standard sequence
        val ordered = canonicalOrder.filter { it in subjects }
        _selectedSubjects.value = ordered
        orchestrator.setSubjectOrder(ordered)
        _screen.value = AppScreen.Instructions
    }

    fun beginAssessment() {
        val subject = orchestrator.subjectOrder.first()
        _currentSubject.value = subject
        orchestrator.startSubject(subject)
        _currentQuestion.value = engine.currentQuestion.value
        _questionIndex.value = 1
        startQuestionTimer()
        _screen.value = AppScreen.ActiveAssessment
    }

    fun submitAnswer(
        question: Question,
        answer: String,
        answers: List<String> = emptyList(),
        orderedItems: List<String> = emptyList(),
        fluencySelections: Set<String> = emptySet()
    ) {
        timerJob?.cancel()

        val timeMs = System.currentTimeMillis() - questionStartTime
        val correct = evaluateAnswer(question, answer, answers, orderedItems, fluencySelections)
        val actualAnswer = when (question.type) {
            QuestionType.MULTI_SELECT -> answers.joinToString(", ")
            QuestionType.CLOZE -> answers.joinToString(", ")
            QuestionType.ORDERING -> orderedItems.joinToString(" -> ")
            QuestionType.TIMED_FLUENCY -> fluencySelections.joinToString(", ")
            else -> answer
        }

        engine.recordAnswer(question, actualAnswer, correct, timeMs)

        val hasNext = engine.deliverNextQuestion(_currentSubject.value, studentAge)
        if (hasNext) {
            _currentQuestion.value = engine.currentQuestion.value
            _questionIndex.value = _questionIndex.value + 1
            questionStartTime = System.currentTimeMillis()
            startQuestionTimer()
        } else {
            completeCurrentSubject()
        }
    }

    fun timeExpired() {
        val question = _currentQuestion.value ?: return
        submitAnswer(question, "", emptyList(), emptyList(), emptySet())
    }

    private fun completeCurrentSubject() {
        val subject = _currentSubject.value
        val score = orchestrator.completeSubject(subject)
        _subjectScores.value = _subjectScores.value + (subject to score)

        val nextSubject = orchestrator.getNextSubject()
        if (nextSubject != null) {
            _screen.value = AppScreen.SubjectTransition(subject, nextSubject)
        } else {
            val result = orchestrator.completeAssessment()
            _assessmentResult.value = result
            _screen.value = AppScreen.Completion
        }
    }

    fun startNextSubject() {
        val nextSubject = orchestrator.getNextSubject() ?: return
        _currentSubject.value = nextSubject
        _questionIndex.value = 1
        orchestrator.startSubject(nextSubject)
        _currentQuestion.value = engine.currentQuestion.value
        questionStartTime = System.currentTimeMillis()
        startQuestionTimer()
        _screen.value = AppScreen.ActiveAssessment
    }

    fun exportPdf(result: AssessmentResult) {
        scope.launch(Dispatchers.IO) {
            val report = analysisAgent.analyze(result)
            val path = withContext(Dispatchers.Main) {
                PdfExportHelper.showSaveDialog(result.student.firstName)
            } ?: return@launch
            try {
                reportGenerator.generatePdf(report, result, path)
                _exportStatus.value = ExportStatus.Success(path)
            } catch (e: Exception) {
                _exportStatus.value = ExportStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetToWelcome() {
        timerJob?.cancel()
        orchestrator.reset()
        _screen.value = AppScreen.Welcome
        _currentQuestion.value = null
        _assessmentResult.value = null
        _subjectScores.value = emptyMap()
        _selectedSubjects.value = AppSettings.fullAssessmentSubjects()
        _questionIndex.value = 0
        isFullAssessment = true
    }

    private fun startQuestionTimer() {
        timerJob?.cancel()
        _timeRemaining.value = 240
        timerJob = scope.launch {
            for (i in 240 downTo 0) {
                _timeRemaining.value = i
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                timeExpired()
            }
        }
    }

    private fun evaluateAnswer(
        question: Question,
        answer: String,
        answers: List<String>,
        orderedItems: List<String>,
        fluencySelections: Set<String>
    ): Boolean = when (question.type) {
        QuestionType.MULTIPLE_CHOICE -> answer.equals(question.correctAnswer, ignoreCase = true)
        QuestionType.MULTI_SELECT -> {
            val expected = question.correctAnswers.toSet()
            answers.toSet() == expected
        }
        QuestionType.FILL_IN_BLANK -> {
            val trimmed = answer.trim()
            trimmed.equals(question.correctAnswer.trim(), ignoreCase = true) ||
                question.correctAnswers.any { trimmed.equals(it.trim(), ignoreCase = true) }
        }
        QuestionType.CLOZE -> {
            val expected = question.blanks
            answers.size == expected.size && answers.zip(expected).all { (a, e) ->
                a.trim().equals(e.trim(), ignoreCase = true)
            }
        }
        QuestionType.ORDERING -> orderedItems == question.orderedItems
        QuestionType.PASSAGE_BASED -> answer.equals(question.correctAnswer, ignoreCase = true)
        QuestionType.TIMED_FLUENCY -> {
            val correctCount = fluencySelections.count { it in question.fluencyCorrectItems }
            val incorrectCount = fluencySelections.count { it !in question.fluencyCorrectItems }
            correctCount > incorrectCount
        }
    }
}
