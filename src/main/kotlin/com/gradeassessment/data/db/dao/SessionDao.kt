package com.gradeassessment.data.db.dao

import com.gradeassessment.data.db.tables.SessionsTable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SessionDao {

    fun createSession(firstName: String, age: Int): Long = transaction {
        SessionsTable.insert {
            it[studentFirstName] = firstName
            it[studentAge] = age
            it[startTime] = Clock.System.now()
            it[status] = "IN_PROGRESS"
        }[SessionsTable.id]
    }

    fun completeSession(sessionId: Long, endTime: Instant, durationMs: Long) = transaction {
        SessionsTable.update({ SessionsTable.id eq sessionId }) {
            it[SessionsTable.endTime] = endTime
            it[status] = "COMPLETED"
            it[SessionsTable.durationMs] = durationMs
        }
    }

    fun getSession(sessionId: Long) = transaction {
        SessionsTable.selectAll().where { SessionsTable.id eq sessionId }.firstOrNull()
    }
}
