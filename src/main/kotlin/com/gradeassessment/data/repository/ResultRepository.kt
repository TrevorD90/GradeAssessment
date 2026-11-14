package com.gradeassessment.data.repository

import com.gradeassessment.data.db.dao.ResponseDao
import com.gradeassessment.data.db.dao.ResultDao
import com.gradeassessment.data.db.tables.ResponsesTable
import com.gradeassessment.data.questionbank.QuestionRepository
import com.gradeassessment.domain.model.AssessmentResult
import com.gradeassessment.domain.model.MissedQuestion
import com.gradeassessment.domain.model.Subject

class ResultRepository(
    private val resultDao: ResultDao,
    private val responseDao: ResponseDao,
    private val questionRepository: QuestionRepository
) {
    fun saveResult(result: AssessmentResult) {
        resultDao.saveResult(result)
    }

    fun getAllResults(): List<AssessmentResult> {
        return resultDao.getAllResults()
    }

    fun getResultsForStudent(firstName: String): List<AssessmentResult> {
        return resultDao.getResultsForStudent(firstName)
    }

    fun getResultBySessionId(sessionId: Long): AssessmentResult? {
        return resultDao.getResultBySessionId(sessionId)
    }

    /**
     * Rebuilds each subject's missed-question list from the stored responses.
     * Results read back from the database carry only scores, so without this the Detail View
     * and exported PDF of a saved result never showed any missed questions.
     * Results that already carry missed questions (a just-finished session) are returned as is.
     * O(responses in the session); each question lookup is O(1).
     */
    fun withMissedQuestions(result: AssessmentResult): AssessmentResult {
        if (result.subjectResults.values.any { it.missedQuestions.isNotEmpty() }) return result

        val missedBySubject = HashMap<Subject, MutableList<MissedQuestion>>()
        for (row in responseDao.getResponsesForSession(result.sessionId)) {
            if (row[ResponsesTable.correct]) continue
            val subject = runCatching { Subject.valueOf(row[ResponsesTable.subject]) }.getOrNull() ?: continue
            // Questions retired from the bank since the session was taken cannot be shown.
            val question = questionRepository.getQuestionById(row[ResponsesTable.questionId]) ?: continue
            missedBySubject.getOrPut(subject) { mutableListOf() }.add(
                MissedQuestion(
                    questionId = question.id,
                    prompt = question.prompt,
                    studentAnswer = row[ResponsesTable.studentAnswer],
                    correctAnswer = question.correctAnswerText(),
                    standardCode = question.standardCode
                )
            )
        }
        if (missedBySubject.isEmpty()) return result

        return result.copy(
            subjectResults = result.subjectResults.mapValues { (subject, subjectResult) ->
                subjectResult.copy(missedQuestions = missedBySubject[subject] ?: emptyList())
            }
        )
    }
}
