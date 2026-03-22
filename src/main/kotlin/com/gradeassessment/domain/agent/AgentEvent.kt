package com.gradeassessment.domain.agent

import com.gradeassessment.domain.model.*

sealed interface AgentEvent {
    data class SessionStarted(val sessionId: Long, val student: Student) : AgentEvent
    data class SubjectStarted(val subject: Subject) : AgentEvent
    data class QuestionReady(val question: Question, val index: Int, val total: Int) : AgentEvent
    data class AnswerRecorded(val response: StudentResponse) : AgentEvent
    data class SubjectCompleted(val subject: Subject, val scoreBundle: ScoreBundle) : AgentEvent
    data class AssessmentCompleted(val result: AssessmentResult) : AgentEvent
    data class TimerTick(val remainingSeconds: Int) : AgentEvent
    data class TimerExpired(val questionId: String) : AgentEvent
    data object ReadingFluencyStarted : AgentEvent
}
