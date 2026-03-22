package com.gradeassessment.domain.model

data class AdaptiveState(
    val currentTheta: Double,
    val standardError: Double = 1.0,
    val responses: List<StudentResponse> = emptyList(),
    val questionsAsked: List<String> = emptyList(),
    val lastBloomsLevel: Int? = null,
    val lastQuestionType: QuestionType? = null
) {
    val questionCount: Int get() = responses.size
    val correctCount: Int get() = responses.count { it.correct }

    companion object {
        fun fromAge(age: Int): AdaptiveState {
            val expectedGrade = (age - 5).coerceAtLeast(0).toDouble()
            val startingTheta = (expectedGrade - 5.0) / 5.0 * 3.0
            return AdaptiveState(currentTheta = startingTheta.coerceIn(-3.0, 3.0))
        }
    }
}
