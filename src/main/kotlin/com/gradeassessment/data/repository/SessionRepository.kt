package com.gradeassessment.data.repository

import com.gradeassessment.data.db.dao.ResponseDao
import com.gradeassessment.data.db.dao.SessionDao
import com.gradeassessment.domain.model.StudentResponse

class SessionRepository(
    private val sessionDao: SessionDao,
    private val responseDao: ResponseDao
) {
    fun createSession(firstName: String, age: Int): Long {
        return sessionDao.createSession(firstName, age)
    }

    fun completeSession(sessionId: Long, endTime: kotlinx.datetime.Instant, durationMs: Long) {
        sessionDao.completeSession(sessionId, endTime, durationMs)
    }

    fun saveResponse(sessionId: Long, response: StudentResponse) {
        responseDao.saveResponse(sessionId, response)
    }
}
