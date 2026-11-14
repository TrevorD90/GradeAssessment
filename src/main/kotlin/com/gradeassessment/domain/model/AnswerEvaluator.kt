package com.gradeassessment.domain.model

import kotlin.math.ceil

/**
 * Decides whether a submitted answer is correct. Lives in the domain layer (it used to be a
 * private function of the UI ViewModel) so it can be unit-tested and reused.
 */
object AnswerEvaluator {

    /** Share of fluency items the student must say correctly for the subtest to count as passed. */
    const val FLUENCY_PASS_FRACTION = 0.6

    fun isCorrect(
        question: Question,
        answer: String,
        answers: List<String> = emptyList(),
        orderedItems: List<String> = emptyList(),
        fluencySelections: Set<String> = emptySet()
    ): Boolean = when (question.type) {
        QuestionType.MULTIPLE_CHOICE, QuestionType.PASSAGE_BASED ->
            answer.isNotBlank() && answer == question.correctAnswer

        QuestionType.MULTI_SELECT ->
            // An empty selection is never correct (timeouts submit an empty selection).
            answers.isNotEmpty() && answers.toSet() == question.correctAnswers.toSet()

        QuestionType.FILL_IN_BLANK ->
            (listOf(question.correctAnswer) + question.correctAnswers)
                .filter { it.isNotBlank() }
                .any { typedMatches(answer, it, ignoreArticles(question)) }

        QuestionType.CLOZE -> {
            val expected = question.blanks
            expected.isNotEmpty() && answers.size == expected.size &&
                answers.zip(expected).all { (given, wanted) -> typedMatches(given, wanted, ignoreArticles(question)) }
        }

        QuestionType.ORDERING ->
            question.orderedItems.isNotEmpty() && orderedItems == question.orderedItems

        QuestionType.TIMED_FLUENCY ->
            // An adult marks the items the student said correctly.
            // [DECISION: pass = at least 60% of the items marked correct. Previously the marks were
            //  compared with fluencyCorrectItems, which for Phoneme Segmentation holds phoneme
            //  spellings ("/k/ /a/ /t/") rather than the displayed words, so that subtest could
            //  never be passed. REVIEW NEEDED: 60% is a placeholder, not a published benchmark.]
            fluencySelections.count { it in question.fluencyItems } >= fluencyItemsNeeded(question)
    }

    fun fluencyItemsNeeded(question: Question): Int =
        ceil(question.fluencyItems.size * FLUENCY_PASS_FRACTION).toInt().coerceAtLeast(1)

    /**
     * Compares a typed answer with an accepted answer, forgiving differences a child should not
     * be penalised for: case, surrounding/double spaces, a trailing full stop, thousands
     * separators, and number formatting ("32", "32.0", " 32 " and "0.50" vs ".5" all match).
     */
    /**
     * A leading "a"/"an"/"the" is not what is being assessed in content subjects ("the Nile" vs
     * "Nile"), but in Writing the article itself can be the skill under test ("an" vs "a").
     */
    private fun ignoreArticles(question: Question): Boolean = question.subject != Subject.WRITING

    private val LEADING_ARTICLE = Regex("^(a|an|the) (?=\\S)")

    fun typedMatches(given: String, expected: String, ignoreLeadingArticle: Boolean = false): Boolean {
        var g = normalizeText(given)
        var e = normalizeText(expected)
        if (ignoreLeadingArticle) {
            g = g.replaceFirst(LEADING_ARTICLE, "")
            e = e.replaceFirst(LEADING_ARTICLE, "")
        }
        if (g.isEmpty()) return false
        if (g == e) return true
        val gn = toNumber(g)
        val en = toNumber(e)
        return gn != null && en != null && gn.compareTo(en) == 0
    }

    private fun normalizeText(value: String): String =
        value.trim()
            .trimEnd('.', '!', '?')
            .replace(Regex("\\s+"), " ")
            .replace('’', '\'') // typographic apostrophe
            // Typographic minus / dashes (pasted text, some keyboard layouts) count as a hyphen-minus.
            .replace('−', '-')
            .replace('–', '-')
            .replace('—', '-')
            .lowercase()

    /** Number words a young child might type instead of digits ("four" for 4). */
    private val NUMBER_WORDS: Map<String, Int> = listOf(
        "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
        "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
        "nineteen", "twenty"
    ).withIndex().associate { (i, word) -> word to i }

    private fun toNumber(value: String): java.math.BigDecimal? {
        NUMBER_WORDS[value]?.let { return java.math.BigDecimal(it) }
        val cleaned = value.replace(",", "").replace("$", "").replace("%", "").trim()
        if (cleaned.isEmpty() || !cleaned.any { it.isDigit() }) return null
        return cleaned.toBigDecimalOrNull()
    }
}
