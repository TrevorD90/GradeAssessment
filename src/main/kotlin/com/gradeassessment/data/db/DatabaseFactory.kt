package com.gradeassessment.data.db

import com.gradeassessment.data.db.tables.ResponsesTable
import com.gradeassessment.data.db.tables.ResultsTable
import com.gradeassessment.data.db.tables.SessionsTable
import com.gradeassessment.data.db.tables.SubjectScoresTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private var database: Database? = null

    fun init(dbPath: String = "") {
        val resolvedPath = if (dbPath.isNotEmpty()) {
            dbPath
        } else {
            // Store DB in user's home directory so it works from any working directory
            val appDir = java.io.File(System.getProperty("user.home"), ".grade-assessment")
            if (!appDir.exists()) appDir.mkdirs()
            java.io.File(appDir, "grade_assessment.db").absolutePath
        }
        database = Database.connect(
            url = "jdbc:sqlite:$resolvedPath",
            driver = "org.sqlite.JDBC"
        )
        transaction {
            SchemaUtils.create(SessionsTable, ResponsesTable, ResultsTable, SubjectScoresTable)
            migrateOldData()
            migrateAddScoringColumns()
        }
    }

    private fun migrateOldData() {
        val hasSubjectScores = SubjectScoresTable.selectAll().count() > 0
        if (hasSubjectScores) return

        val oldResults = ResultsTable.selectAll().toList()
        if (oldResults.isEmpty()) return

        for (row in oldResults) {
            val sessionId = row[ResultsTable.sessionId]
            val mathTotal = row[ResultsTable.mathTotalCount]
            val readingTotal = row[ResultsTable.readingTotalCount]
            val writingTotal = row[ResultsTable.writingTotalCount]

            if (mathTotal > 0) {
                SubjectScoresTable.insertIgnore {
                    it[SubjectScoresTable.sessionId] = sessionId
                    it[subject] = "MATH"
                    it[scaledScore] = row[ResultsTable.mathScaledScore]
                    it[gradeEquivalent] = row[ResultsTable.mathGradeEquivalent]
                    it[riskTier] = row[ResultsTable.mathRiskTier]
                    it[theta] = row[ResultsTable.mathTheta]
                    it[standardError] = row[ResultsTable.mathConfidence]
                    it[correctCount] = row[ResultsTable.mathCorrectCount]
                    it[totalCount] = row[ResultsTable.mathTotalCount]
                }
            }

            if (readingTotal > 0) {
                SubjectScoresTable.insertIgnore {
                    it[SubjectScoresTable.sessionId] = sessionId
                    it[subject] = "READING"
                    it[scaledScore] = row[ResultsTable.readingScaledScore]
                    it[gradeEquivalent] = row[ResultsTable.readingGradeEquivalent]
                    it[riskTier] = row[ResultsTable.readingRiskTier]
                    it[theta] = row[ResultsTable.readingTheta]
                    it[standardError] = row[ResultsTable.readingConfidence]
                    it[correctCount] = row[ResultsTable.readingCorrectCount]
                    it[totalCount] = row[ResultsTable.readingTotalCount]
                }
            }

            if (writingTotal > 0) {
                SubjectScoresTable.insertIgnore {
                    it[SubjectScoresTable.sessionId] = sessionId
                    it[subject] = "WRITING"
                    it[scaledScore] = row[ResultsTable.writingScaledScore]
                    it[gradeEquivalent] = row[ResultsTable.writingGradeEquivalent]
                    it[riskTier] = row[ResultsTable.writingRiskTier]
                    it[theta] = row[ResultsTable.writingTheta]
                    it[standardError] = row[ResultsTable.writingConfidence]
                    it[correctCount] = row[ResultsTable.writingCorrectCount]
                    it[totalCount] = row[ResultsTable.writingTotalCount]
                }
            }
        }
    }

    private fun migrateAddScoringColumns() {
        // Add new nullable columns for CT/IQ scoring if they don't exist yet
        val columnsToAdd = listOf(
            "scoring_mode" to "VARCHAR(30)",
            "iq_score" to "INTEGER",
            "iq_range_low" to "INTEGER",
            "iq_range_high" to "INTEGER",
            "ct_percentile" to "INTEGER"
        )
        for ((column, type) in columnsToAdd) {
            try {
                org.jetbrains.exposed.sql.transactions.TransactionManager.current()
                    .exec("ALTER TABLE subject_scores ADD COLUMN $column $type")
            } catch (_: Exception) {
                // Column already exists — safe to ignore
            }
        }
    }

    fun getDatabase(): Database {
        return database ?: throw IllegalStateException("Database not initialized. Call init() first.")
    }
}
