package com.gradeassessment.data.db.tables

import org.jetbrains.exposed.sql.Table

object SubjectScoresTable : Table("subject_scores") {
    val id = long("id").autoIncrement()
    val sessionId = long("session_id").references(SessionsTable.id)
    val subject = varchar("subject", 50)
    val scaledScore = integer("scaled_score")
    val gradeEquivalent = double("grade_equivalent")
    val riskTier = varchar("risk_tier", 50)
    val theta = double("theta")
    val standardError = double("standard_error")
    val correctCount = integer("correct_count")
    val totalCount = integer("total_count")
    val scoringMode = varchar("scoring_mode", 30).nullable()
    val iqScore = integer("iq_score").nullable()
    val iqRangeLow = integer("iq_range_low").nullable()
    val iqRangeHigh = integer("iq_range_high").nullable()
    val ctPercentile = integer("ct_percentile").nullable()

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(sessionId, subject)
    }
}
