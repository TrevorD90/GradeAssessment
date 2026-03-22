package com.gradeassessment.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String,
    val subject: Subject,
    val type: QuestionType,
    val gradeRange: String,
    val bloomsLevel: Int = 1,
    val standardCode: String = "",
    val piParameters: IrtParameters = IrtParameters(),
    val prompt: String,
    val passage: String? = null,
    val choices: List<String> = emptyList(),
    val correctAnswer: String = "",
    val correctAnswers: List<String> = emptyList(),
    val blanks: List<String> = emptyList(),
    val orderedItems: List<String> = emptyList(),
    val fluencyItems: List<String> = emptyList(),
    val fluencyCorrectItems: Set<String> = emptySet(),
    val fluencyTimeLimitSeconds: Int = 60,
    val readingSubtest: ReadingSubtest? = null,
    val explanation: String? = null
) {
    val irtParameters: IrtParameters get() = piParameters
}
