package com.gradeassessment.data.questionbank

import com.gradeassessment.domain.model.Question
import com.gradeassessment.domain.model.QuestionType
import com.gradeassessment.domain.model.ReadingSubtest
import com.gradeassessment.domain.model.Subject

class QuestionRepository {

    private var questions: List<Question> = emptyList()
    @Volatile
    private var loaded = false

    @Synchronized
    fun loadAll() {
        if (loaded) return
        questions = QuestionBankLoader.loadQuestionsFromResources()
        loaded = true
    }

    fun getQuestionById(id: String): Question? {
        if (!loaded) loadAll()
        return questions.find { it.id == id }
    }

    fun getQuestionsBySubject(subject: Subject): List<Question> {
        if (!loaded) loadAll()
        return questions.filter { it.subject == subject }
    }

    fun getQuestionsBySubjectAndGradeRange(subject: Subject, gradeRange: String): List<Question> =
        questions.filter { it.subject == subject && it.gradeRange == gradeRange }

    fun getReadingFluencyQuestions(subtest: ReadingSubtest): List<Question> =
        questions.filter { it.subject == Subject.READING && it.readingSubtest == subtest }

    fun getReadingComprehensionQuestions(): List<Question> =
        questions.filter { it.subject == Subject.READING && it.readingSubtest == ReadingSubtest.COMPREHENSION }

    fun getQuestionsByType(type: QuestionType): List<Question> =
        questions.filter { it.type == type }

    fun getFilteredQuestions(
        subject: Subject,
        excludeIds: List<String> = emptyList(),
        excludeBloomsLevel: Int? = null,
        excludeType: QuestionType? = null
    ): List<Question> =
        questions.filter { q ->
            q.subject == subject &&
                q.id !in excludeIds &&
                (excludeBloomsLevel == null || q.bloomsLevel != excludeBloomsLevel) &&
                (excludeType == null || q.type != excludeType)
        }

    fun getAllQuestions(): List<Question> = questions
}
