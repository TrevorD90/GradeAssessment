package com.gradeassessment.data.db.dao

import com.gradeassessment.data.db.tables.ResultsTable
import com.gradeassessment.data.db.tables.SubjectScoresTable
import com.gradeassessment.domain.model.*
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ResultDao {

    fun saveResult(result: AssessmentResult) = transaction {
        // Insert session-level row (write zeros to old denormalized columns for backward compat)
        ResultsTable.insert {
            it[sessionId] = result.sessionId
            it[studentFirstName] = result.student.firstName
            it[studentAge] = result.student.age
            it[startTime] = result.startTime
            it[endTime] = result.endTime
            it[sessionDurationMs] = result.durationMs

            val math = result.subjectResults[Subject.MATH]?.scoreBundle
            it[mathScaledScore] = math?.scaledScore ?: 0
            it[mathGradeEquivalent] = math?.gradeEquivalent ?: 0.0
            it[mathRiskTier] = math?.riskTier?.name ?: RiskTier.WELL_BELOW_BENCHMARK.name
            it[mathTheta] = math?.theta ?: 0.0
            it[mathConfidence] = math?.standardError ?: 1.0
            it[mathCorrectCount] = math?.correctCount ?: 0
            it[mathTotalCount] = math?.totalCount ?: 0

            val reading = result.subjectResults[Subject.READING]?.scoreBundle
            it[readingScaledScore] = reading?.scaledScore ?: 0
            it[readingGradeEquivalent] = reading?.gradeEquivalent ?: 0.0
            it[readingRiskTier] = reading?.riskTier?.name ?: RiskTier.WELL_BELOW_BENCHMARK.name
            it[readingTheta] = reading?.theta ?: 0.0
            it[readingConfidence] = reading?.standardError ?: 1.0
            it[readingCorrectCount] = reading?.correctCount ?: 0
            it[readingTotalCount] = reading?.totalCount ?: 0

            val writing = result.subjectResults[Subject.WRITING]?.scoreBundle
            it[writingScaledScore] = writing?.scaledScore ?: 0
            it[writingGradeEquivalent] = writing?.gradeEquivalent ?: 0.0
            it[writingRiskTier] = writing?.riskTier?.name ?: RiskTier.WELL_BELOW_BENCHMARK.name
            it[writingTheta] = writing?.theta ?: 0.0
            it[writingConfidence] = writing?.standardError ?: 1.0
            it[writingCorrectCount] = writing?.correctCount ?: 0
            it[writingTotalCount] = writing?.totalCount ?: 0
        }

        // Insert one row per subject into normalized table
        for ((subject, subjectResult) in result.subjectResults) {
            val sb = subjectResult.scoreBundle
            SubjectScoresTable.insert {
                it[SubjectScoresTable.sessionId] = result.sessionId
                it[SubjectScoresTable.subject] = subject.name
                it[scaledScore] = sb.scaledScore
                it[gradeEquivalent] = sb.gradeEquivalent
                it[riskTier] = sb.riskTier.name
                it[theta] = sb.theta
                it[standardError] = sb.standardError
                it[correctCount] = sb.correctCount
                it[totalCount] = sb.totalCount
                it[scoringMode] = sb.scoringMode.name
                it[iqScore] = sb.iqScore
                it[iqRangeLow] = sb.iqRangeLow
                it[iqRangeHigh] = sb.iqRangeHigh
                it[ctPercentile] = sb.ctPercentile
            }
        }
    }

    fun getAllResults(): List<AssessmentResult> = transaction {
        val resultRows = ResultsTable.selectAll()
            .orderBy(ResultsTable.startTime)
            .toList()

        // Batch load all subject scores in one query to avoid N+1
        val allScoreRows = SubjectScoresTable.selectAll().toList()
        val scoresBySession = allScoreRows.groupBy { it[SubjectScoresTable.sessionId] }

        resultRows.map { row ->
            buildAssessmentResult(row, scoresBySession[row[ResultsTable.sessionId]] ?: emptyList())
        }
    }

    /**
     * All saved results for one student (case-insensitive first-name match), filtered in SQL.
     * O(matching rows) instead of loading and scanning the entire results history in memory.
     */
    fun getResultsForStudent(firstName: String): List<AssessmentResult> = transaction {
        val resultRows = ResultsTable.selectAll()
            .where { ResultsTable.studentFirstName.lowerCase() eq firstName.trim().lowercase() }
            .orderBy(ResultsTable.startTime)
            .toList()
        if (resultRows.isEmpty()) return@transaction emptyList()

        val sessionIds = resultRows.map { it[ResultsTable.sessionId] }
        val scoresBySession = SubjectScoresTable.selectAll()
            .where { SubjectScoresTable.sessionId inList sessionIds }
            .groupBy { it[SubjectScoresTable.sessionId] }

        resultRows.map { row ->
            buildAssessmentResult(row, scoresBySession[row[ResultsTable.sessionId]] ?: emptyList())
        }
    }

    fun getResultBySessionId(sessionId: Long): AssessmentResult? = transaction {
        val row = ResultsTable.selectAll()
            .where { ResultsTable.sessionId eq sessionId }
            .firstOrNull() ?: return@transaction null
        val scoreRows = SubjectScoresTable.selectAll()
            .where { SubjectScoresTable.sessionId eq sessionId }
            .toList()
        buildAssessmentResult(row, scoreRows)
    }

    private fun buildAssessmentResult(row: ResultRow, scoreRows: List<ResultRow>): AssessmentResult {
        val sessionId = row[ResultsTable.sessionId]
        val student = Student(
            firstName = row[ResultsTable.studentFirstName],
            age = row[ResultsTable.studentAge]
        )

        val subjectResults = scoreRows.mapNotNull { scoreRow ->
            val subjectName = scoreRow[SubjectScoresTable.subject]
            val subject = try {
                Subject.valueOf(subjectName)
            } catch (_: Exception) {
                System.err.println("ResultDao: unknown Subject value '$subjectName' for session $sessionId, skipping")
                return@mapNotNull null
            }
            val riskTierName = scoreRow[SubjectScoresTable.riskTier]
            val scoringModeName = scoreRow[SubjectScoresTable.scoringMode]
            val scoringMode = if (scoringModeName != null) {
                try { ScoringMode.valueOf(scoringModeName) } catch (_: Exception) { ScoringMode.GRADE_EQUIVALENT }
            } else {
                ScoringMode.GRADE_EQUIVALENT
            }
            val scoreBundle = ScoreBundle(
                scaledScore = scoreRow[SubjectScoresTable.scaledScore],
                gradeEquivalent = scoreRow[SubjectScoresTable.gradeEquivalent],
                riskTier = try {
                    RiskTier.valueOf(riskTierName)
                } catch (_: Exception) {
                    System.err.println("ResultDao: unknown RiskTier value '$riskTierName' for session $sessionId subject $subjectName, defaulting to WELL_BELOW_BENCHMARK")
                    RiskTier.WELL_BELOW_BENCHMARK
                },
                theta = scoreRow[SubjectScoresTable.theta],
                standardError = scoreRow[SubjectScoresTable.standardError],
                correctCount = scoreRow[SubjectScoresTable.correctCount],
                totalCount = scoreRow[SubjectScoresTable.totalCount],
                scoringMode = scoringMode,
                iqScore = scoreRow[SubjectScoresTable.iqScore],
                iqRangeLow = scoreRow[SubjectScoresTable.iqRangeLow],
                iqRangeHigh = scoreRow[SubjectScoresTable.iqRangeHigh],
                ctPercentile = scoreRow[SubjectScoresTable.ctPercentile]
            )
            subject to SubjectResult(
                subject = subject,
                scoreBundle = scoreBundle,
                responses = emptyList()
            )
        }.toMap()

        return AssessmentResult(
            id = row[ResultsTable.id],
            sessionId = sessionId,
            student = student,
            startTime = row[ResultsTable.startTime],
            endTime = row[ResultsTable.endTime],
            durationMs = row[ResultsTable.sessionDurationMs],
            subjectResults = subjectResults
        )
    }
}
