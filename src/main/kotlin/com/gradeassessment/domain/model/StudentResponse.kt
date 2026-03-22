package com.gradeassessment.domain.model

data class StudentResponse(
    val questionId: String,
    val subject: Subject,
    val subtest: ReadingSubtest? = null,
    val studentAnswer: String,
    val correct: Boolean,
    val timeToAnswerMs: Long,
    val hintUsed: Boolean = false,
    val thetaAtTime: Double,
    val questionIndex: Int
)
