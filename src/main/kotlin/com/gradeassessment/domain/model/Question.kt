package com.gradeassessment.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String,
    val subject: Subject,
    val type: QuestionType,
    val gradeRange: String,
    /** Exact grade the item's standard belongs to (0 = kindergarten .. 10); null for legacy fluency items. */
    val gradeLevel: Int? = null,
    /** Difficulty within the grade: EASY, MEDIUM or HARD. */
    val difficulty: String? = null,
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

    /** Human-readable correct answer for reports, whatever the question type. */
    fun correctAnswerText(): String = when (type) {
        QuestionType.CLOZE -> blanks.joinToString(", ")
        QuestionType.ORDERING -> orderedItems.joinToString(" -> ")
        QuestionType.MULTI_SELECT -> correctAnswers.joinToString(", ")
        QuestionType.TIMED_FLUENCY ->
            "At least ${AnswerEvaluator.fluencyItemsNeeded(this)} of ${fluencyItems.size} items said correctly"
        else -> correctAnswer.ifBlank { correctAnswers.firstOrNull().orEmpty() }
    }

    /**
     * Returns why this question cannot be scored fairly, or null if it is usable.
     * The loader skips defective questions instead of serving items no answer can satisfy
     * (previously a MULTI_SELECT with no correctAnswers could only be "right" by selecting nothing,
     * and an ORDERING item with no orderedItems was always marked wrong).
     */
    fun structuralDefect(): String? = when (type) {
        QuestionType.MULTIPLE_CHOICE, QuestionType.PASSAGE_BASED -> when {
            choices.size < 2 -> "needs at least 2 choices"
            correctAnswer !in choices -> "correctAnswer is not one of the choices"
            type == QuestionType.PASSAGE_BASED && passage.isNullOrBlank() -> "passage is missing"
            else -> null
        }
        QuestionType.MULTI_SELECT -> when {
            correctAnswers.isEmpty() -> "correctAnswers is empty"
            !choices.containsAll(correctAnswers) -> "correctAnswers contains values that are not choices"
            else -> null
        }
        QuestionType.FILL_IN_BLANK ->
            if (correctAnswer.isBlank() && correctAnswers.none { it.isNotBlank() }) "no accepted answer" else null
        QuestionType.CLOZE -> when {
            blanks.isEmpty() -> "blanks is empty"
            prompt.split("___").size - 1 != blanks.size -> "number of ___ gaps does not match blanks"
            else -> null
        }
        QuestionType.ORDERING -> if (orderedItems.size < 2) "orderedItems is missing" else null
        QuestionType.TIMED_FLUENCY -> if (fluencyItems.isEmpty()) "fluencyItems is empty" else null
    }
}
