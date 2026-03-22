package com.gradeassessment.data.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ResultsTable : Table("results") {
    val id = long("id").autoIncrement()
    val sessionId = long("session_id").references(SessionsTable.id).uniqueIndex()
    val studentFirstName = varchar("student_first_name", 255)
    val studentAge = integer("student_age")
    val startTime = timestamp("start_time")
    val endTime = timestamp("end_time")
    val sessionDurationMs = long("session_duration_ms")

    // Math scores
    val mathScaledScore = integer("math_scaled_score")
    val mathGradeEquivalent = double("math_grade_equivalent")
    val mathRiskTier = varchar("math_risk_tier", 50)
    val mathTheta = double("math_theta")
    val mathConfidence = double("math_confidence")
    val mathCorrectCount = integer("math_correct_count")
    val mathTotalCount = integer("math_total_count")

    // Reading scores
    val readingScaledScore = integer("reading_scaled_score")
    val readingGradeEquivalent = double("reading_grade_equivalent")
    val readingRiskTier = varchar("reading_risk_tier", 50)
    val readingTheta = double("reading_theta")
    val readingConfidence = double("reading_confidence")
    val readingCorrectCount = integer("reading_correct_count")
    val readingTotalCount = integer("reading_total_count")

    // Writing scores
    val writingScaledScore = integer("writing_scaled_score")
    val writingGradeEquivalent = double("writing_grade_equivalent")
    val writingRiskTier = varchar("writing_risk_tier", 50)
    val writingTheta = double("writing_theta")
    val writingConfidence = double("writing_confidence")
    val writingCorrectCount = integer("writing_correct_count")
    val writingTotalCount = integer("writing_total_count")

    override val primaryKey = PrimaryKey(id)
}
