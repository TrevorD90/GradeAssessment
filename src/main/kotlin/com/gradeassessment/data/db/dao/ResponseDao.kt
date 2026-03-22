package com.gradeassessment.data.db.dao

import com.gradeassessment.data.db.tables.ResponsesTable
import com.gradeassessment.domain.model.StudentResponse
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ResponseDao {

    fun saveResponse(sessionId: Long, response: StudentResponse) = transaction {
        ResponsesTable.insert {
            it[ResponsesTable.sessionId] = sessionId
            it[subject] = response.subject.name
            it[subtest] = response.subtest?.name
            it[questionId] = response.questionId
            it[studentAnswer] = response.studentAnswer
            it[correct] = response.correct
            it[timeToAnswerMs] = response.timeToAnswerMs
            it[hintUsed] = response.hintUsed
            it[thetaAtTime] = response.thetaAtTime
            it[questionIndex] = response.questionIndex
        }
    }

    fun getResponsesForSession(sessionId: Long) = transaction {
        ResponsesTable.selectAll()
            .where { ResponsesTable.sessionId eq sessionId }
            .orderBy(ResponsesTable.questionIndex)
            .toList()
    }
}
