package com.gradeassessment.data.repository

import com.gradeassessment.data.db.dao.ResultDao
import com.gradeassessment.domain.model.AssessmentResult

class ResultRepository(
    private val resultDao: ResultDao
) {
    fun saveResult(result: AssessmentResult) {
        resultDao.saveResult(result)
    }

    fun getAllResults(): List<AssessmentResult> {
        return resultDao.getAllResults()
    }

    fun getResultBySessionId(sessionId: Long): AssessmentResult? {
        return resultDao.getResultBySessionId(sessionId)
    }
}
