package com.gradeassessment.data.questionbank

import com.gradeassessment.domain.model.Question
import com.gradeassessment.domain.model.QuestionType
import com.gradeassessment.domain.model.ReadingSubtest
import com.gradeassessment.domain.model.Subject

/**
 * In-memory question bank with indexes built once at load time.
 *
 * Big-O (N = all questions):
 *  - loadAll:                       O(N log N) once (hash index + per-subject sort by difficulty)
 *  - getQuestionById:               O(1)   (was an O(N) linear scan, run for every past answer on every update)
 *  - getAdaptivePool / by subject:  O(1)   (was an O(N) filter on every question delivered)
 *
 * Every accessor goes through [ensureLoaded]; previously only two of them did, so a subject
 * started before the background load finished (e.g. Reading comprehension) saw an empty bank
 * and ended immediately with zero questions.
 */
class QuestionRepository {

    private class Index(questions: List<Question>) {
        val all: List<Question> = questions
        val byId: Map<String, Question> = questions.associateBy { it.id }
        val bySubject: Map<Subject, List<Question>> = questions.groupBy { it.subject }

        /** Adaptive (non-fluency) items per subject, sorted ascending by IRT difficulty b. */
        val adaptivePool: Map<Subject, List<Question>> = questions
            .filter { it.type != QuestionType.TIMED_FLUENCY }
            .filter { it.subject != Subject.READING || it.readingSubtest == ReadingSubtest.COMPREHENSION }
            .groupBy { it.subject }
            .mapValues { (_, list) -> list.sortedBy { it.irtParameters.b } }

        val fluencyBySubtest: Map<ReadingSubtest, List<Question>> = questions
            .filter {
                it.subject == Subject.READING && it.type == QuestionType.TIMED_FLUENCY && it.readingSubtest != null
            }
            .groupBy { it.readingSubtest!! }
    }

    @Volatile
    private var index: Index? = null

    private fun ensureLoaded(): Index {
        index?.let { return it }
        return synchronized(this) {
            index ?: Index(QuestionBankLoader.loadQuestionsFromResources()).also { index = it }
        }
    }

    fun loadAll() {
        ensureLoaded()
    }

    fun getQuestionById(id: String): Question? = ensureLoaded().byId[id]

    fun getQuestionsBySubject(subject: Subject): List<Question> = ensureLoaded().bySubject[subject] ?: emptyList()

    /** Questions eligible for adaptive selection in [subject], sorted ascending by difficulty. */
    fun getAdaptivePool(subject: Subject): List<Question> = ensureLoaded().adaptivePool[subject] ?: emptyList()

    fun getQuestionsBySubjectAndGradeRange(subject: Subject, gradeRange: String): List<Question> =
        getQuestionsBySubject(subject).filter { it.gradeRange == gradeRange }

    fun getReadingFluencyQuestions(subtest: ReadingSubtest): List<Question> =
        ensureLoaded().fluencyBySubtest[subtest] ?: emptyList()

    fun getReadingComprehensionQuestions(): List<Question> = getAdaptivePool(Subject.READING)

    fun getQuestionsByType(type: QuestionType): List<Question> =
        ensureLoaded().all.filter { it.type == type }

    fun getAllQuestions(): List<Question> = ensureLoaded().all
}
