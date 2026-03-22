package com.gradeassessment.domain.agent

import com.gradeassessment.data.repository.ResultRepository
import com.gradeassessment.data.repository.SessionRepository
import com.gradeassessment.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

// [DECISION: defaultSubjectOrder stays as 7 core subjects. AppSettings consulted at ViewModel level for full assessment.]

class OrchestratorAgent(
    private val engineAgent: AssessmentEngineAgent,
    private val sessionRepository: SessionRepository,
    private val resultRepository: ResultRepository
) {
    private val _currentSubject = MutableStateFlow(Subject.MATH)
    val currentSubject: StateFlow<Subject> = _currentSubject.asStateFlow()

    private val _event = MutableStateFlow<AgentEvent?>(null)
    val event: StateFlow<AgentEvent?> = _event.asStateFlow()

    private var sessionId: Long = 0
    private var student: Student? = null
    private var startTime: Instant = Clock.System.now()
    private val subjectResults = mutableMapOf<Subject, SubjectResult>()

    private val defaultSubjectOrder = listOf(
        Subject.MATH, Subject.READING, Subject.WRITING,
        Subject.SCIENCE, Subject.ARTS, Subject.WORLD_HISTORY, Subject.SOCIAL_STUDIES
    )

    private var _subjectOrder: List<Subject> = defaultSubjectOrder
    val subjectOrder: List<Subject> get() = _subjectOrder

    fun setSubjectOrder(subjects: List<Subject>) {
        _subjectOrder = subjects
    }

    fun reset() {
        _subjectOrder = defaultSubjectOrder
        _currentSubject.value = Subject.MATH
        _event.value = null
        sessionId = 0
        student = null
        subjectResults.clear()
    }

    fun startSession(firstName: String, age: Int): Long {
        student = Student(firstName, age)
        startTime = Clock.System.now()
        sessionId = sessionRepository.createSession(firstName, age)
        _event.value = AgentEvent.SessionStarted(sessionId, student!!)
        return sessionId
    }

    fun startSubject(subject: Subject) {
        _currentSubject.value = subject
        val age = student?.age ?: 8
        engineAgent.startSubject(subject, age, sessionId)
        _event.value = AgentEvent.SubjectStarted(subject)
    }

    fun completeSubject(subject: Subject): ScoreBundle {
        val age = student?.age ?: 8
        val scoreBundle = engineAgent.computeSubjectScore(subject, age)
        val responses = engineAgent.getSubjectResponses(subject)
        val missed = engineAgent.getMissedQuestions(subject)

        subjectResults[subject] = SubjectResult(
            subject = subject,
            scoreBundle = scoreBundle,
            responses = responses,
            missedQuestions = missed
        )

        _event.value = AgentEvent.SubjectCompleted(subject, scoreBundle)
        return scoreBundle
    }

    fun getNextSubject(): Subject? {
        val currentIndex = subjectOrder.indexOf(_currentSubject.value)
        return if (currentIndex < subjectOrder.lastIndex) {
            subjectOrder[currentIndex + 1]
        } else null
    }

    fun completeAssessment(): AssessmentResult {
        val endTime = Clock.System.now()
        val durationMs = (endTime - startTime).inWholeMilliseconds

        sessionRepository.completeSession(sessionId, endTime, durationMs)

        val result = AssessmentResult(
            sessionId = sessionId,
            student = student!!,
            startTime = startTime,
            endTime = endTime,
            durationMs = durationMs,
            subjectResults = subjectResults.toMap() // only completed subjects
        )

        resultRepository.saveResult(result)
        _event.value = AgentEvent.AssessmentCompleted(result)
        return result
    }
}
