package com.gradeassessment.data.questionbank

import com.gradeassessment.domain.model.Question
import kotlinx.serialization.json.Json

object QuestionBankLoader {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
    }

    fun loadQuestionsFromResources(): List<Question> {
        val allQuestions = mutableListOf<Question>()

        val resourcePaths = listOf(
            // Math
            "questions/math/math_k_2.json",
            "questions/math/math_3_5.json",
            "questions/math/math_6_8.json",
            "questions/math/math_9_10.json",
            // Reading
            "questions/reading/fluency/letter_naming.json",
            "questions/reading/fluency/phoneme_seg.json",
            "questions/reading/fluency/nonsense_words.json",
            "questions/reading/comprehension/reading_k_2.json",
            "questions/reading/comprehension/reading_3_5.json",
            "questions/reading/comprehension/reading_6_8.json",
            "questions/reading/comprehension/reading_9_10.json",
            // Writing
            "questions/writing/writing_k_2.json",
            "questions/writing/writing_3_5.json",
            "questions/writing/writing_6_8.json",
            "questions/writing/writing_9_10.json",
            // Science
            "questions/science/science_k_2.json",
            "questions/science/science_3_5.json",
            "questions/science/science_6_8.json",
            "questions/science/science_9_10.json",
            // Arts
            "questions/arts/arts_k_2.json",
            "questions/arts/arts_3_5.json",
            "questions/arts/arts_6_8.json",
            "questions/arts/arts_9_10.json",
            // World History
            "questions/world_history/world_history_k_2.json",
            "questions/world_history/world_history_3_5.json",
            "questions/world_history/world_history_6_8.json",
            "questions/world_history/world_history_9_10.json",
            // Social Studies
            "questions/social_studies/social_studies_k_2.json",
            "questions/social_studies/social_studies_3_5.json",
            "questions/social_studies/social_studies_6_8.json",
            "questions/social_studies/social_studies_9_10.json",
            // Critical Thinking
            "questions/critical_thinking/critical_thinking_k_2.json",
            "questions/critical_thinking/critical_thinking_3_5.json",
            "questions/critical_thinking/critical_thinking_6_8.json",
            "questions/critical_thinking/critical_thinking_9_10.json",
            // IQ Assessment
            "questions/iq_assessment/iq_assessment_k_2.json",
            "questions/iq_assessment/iq_assessment_3_5.json",
            "questions/iq_assessment/iq_assessment_6_8.json",
            "questions/iq_assessment/iq_assessment_9_10.json"
        )

        for (path in resourcePaths) {
            try {
                val stream = this::class.java.classLoader.getResourceAsStream(path)
                if (stream != null) {
                    val content = stream.bufferedReader().readText()
                    val questions = json.decodeFromString<List<Question>>(content)
                    allQuestions.addAll(questions)
                }
            } catch (e: Exception) {
                System.err.println("Warning: Could not load $path: ${e.message}")
            }
        }

        return allQuestions
    }
}
