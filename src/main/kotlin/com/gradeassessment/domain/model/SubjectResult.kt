package com.gradeassessment.domain.model

data class SubjectResult(
    val subject: Subject,
    val scoreBundle: ScoreBundle,
    val responses: List<StudentResponse>,
    val missedQuestions: List<MissedQuestion> = emptyList()
)

data class MissedQuestion(
    val questionId: String,
    val prompt: String,
    val studentAnswer: String,
    val correctAnswer: String,
    val standardCode: String = ""
)
