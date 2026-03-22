package com.gradeassessment.domain.model

import kotlinx.datetime.Instant

data class AssessmentResult(
    val id: Long = 0,
    val sessionId: Long,
    val student: Student,
    val startTime: Instant,
    val endTime: Instant,
    val durationMs: Long,
    val subjectResults: Map<Subject, SubjectResult>
) {
    // Backward-compatible accessors for common subjects
    val mathResult: SubjectResult get() = subjectResults[Subject.MATH] ?: emptySubjectResult(Subject.MATH)
    val readingResult: SubjectResult get() = subjectResults[Subject.READING] ?: emptySubjectResult(Subject.READING)
    val writingResult: SubjectResult get() = subjectResults[Subject.WRITING] ?: emptySubjectResult(Subject.WRITING)

    companion object {
        fun emptySubjectResult(subject: Subject) = SubjectResult(
            subject = subject,
            scoreBundle = ScoreBundle(0, 0.0, RiskTier.WELL_BELOW_BENCHMARK, 0.0, 0.0, 0, 0),
            responses = emptyList()
        )
    }
}
