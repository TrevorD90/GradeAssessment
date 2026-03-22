package com.gradeassessment.domain.agent

import com.gradeassessment.data.repository.ResultRepository
import com.gradeassessment.domain.model.*

class ResultsAnalysisAgent(
    private val resultRepository: ResultRepository
) {
    fun analyze(result: AssessmentResult): AnalysisReport {
        val activeResults = result.subjectResults.filter { it.value.scoreBundle.totalCount > 0 }
        // Exclude CT/IQ from core analysis (rankings, overall GE, risk tier)
        val coreResults = activeResults.filter { it.key in Subject.CORE_SUBJECTS }

        val rankings = computeRankings(coreResults)
        val overallGe = computeOverallGe(coreResults)
        val overallRisk = RiskTier.fromGradeEquivalentAndAge(overallGe, result.student.age)
        val strongest = rankings.firstOrNull()?.subject
        val weakest = rankings.lastOrNull()?.subject
        val mastery = computeStandardsMastery(result)
        val trend = computeTrend(result, overallGe)
        val recommendations = generateRecommendations(coreResults, result.student.age, overallRisk, mastery).toMutableList()

        // Add CT/IQ-specific recommendations if those subjects were assessed
        val ctResult = activeResults[Subject.CRITICAL_THINKING]
        if (ctResult != null) {
            val percentile = ctResult.scoreBundle.ctPercentile
            if (percentile != null) {
                val ctRec = when {
                    percentile >= 75 -> "Critical Thinking: Strong reasoning skills (${percentile}th percentile). Encourage advanced problem-solving activities."
                    percentile >= 40 -> "Critical Thinking: Average reasoning skills (${percentile}th percentile). Practice inference and argument analysis exercises."
                    else -> "Critical Thinking: Below-average reasoning skills (${percentile}th percentile). Recommend structured reasoning exercises and guided logic practice."
                }
                recommendations.add(ctRec)
            }
        }

        val iqResult = activeResults[Subject.IQ_ASSESSMENT]
        if (iqResult != null) {
            val iqScore = iqResult.scoreBundle.iqScore
            if (iqScore != null) {
                val iqRec = "IQ Assessment: Estimated IQ score of $iqScore (range: ${iqResult.scoreBundle.iqRangeLow}-${iqResult.scoreBundle.iqRangeHigh}). " +
                    "Note: This is a screening estimate only and does not replace comprehensive psychological evaluation."
                recommendations.add(iqRec)
            }
        }

        return AnalysisReport(
            overallGradeEquivalent = overallGe,
            overallRiskTier = overallRisk,
            strongestSubject = strongest,
            weakestSubject = weakest,
            subjectRankings = rankings,
            standardsMastery = mastery,
            recommendations = recommendations,
            performanceTrend = trend
        )
    }

    private fun computeRankings(activeResults: Map<Subject, SubjectResult>): List<SubjectRanking> {
        return activeResults.entries
            .sortedByDescending { it.value.scoreBundle.scaledScore }
            .mapIndexed { index, (subject, subjectResult) ->
                SubjectRanking(
                    subject = subject,
                    scaledScore = subjectResult.scoreBundle.scaledScore,
                    gradeEquivalent = subjectResult.scoreBundle.gradeEquivalent,
                    rank = index + 1
                )
            }
    }

    private fun computeOverallGe(activeResults: Map<Subject, SubjectResult>): Double {
        if (activeResults.isEmpty()) return 0.0
        // Only core subjects contribute to overall GE
        return activeResults.values.map { it.scoreBundle.gradeEquivalent }.average()
    }

    private fun computeStandardsMastery(result: AssessmentResult): List<StandardMastery> {
        // We can only compute mastery from missed questions data — responses aren't hydrated from DB.
        // Standards that appear in missed questions had errors, so mastered = false for those.
        val standardGroups = mutableMapOf<String, MutableList<Subject>>()

        for ((subject, subjectResult) in result.subjectResults) {
            for (missed in subjectResult.missedQuestions) {
                if (missed.standardCode.isNotBlank()) {
                    standardGroups.getOrPut(missed.standardCode) { mutableListOf() }
                        .add(subject)
                }
            }
        }

        return standardGroups.map { (code, subjects) ->
            val subject = subjects.first()
            val total = subjects.size
            StandardMastery(
                standardCode = code,
                subject = subject,
                mastered = false, // standard had missed questions — needs review
                correctCount = 0,
                totalCount = total
            )
        }
    }

    private fun computeTrend(result: AssessmentResult, currentOverallGe: Double): PerformanceTrend? {
        val allResults = resultRepository.getAllResults()
        val priorResults = allResults.filter {
            it.student.firstName.equals(result.student.firstName, ignoreCase = true) &&
                it.sessionId != result.sessionId
        }.sortedByDescending { it.startTime }

        val previous = priorResults.firstOrNull() ?: return null
        val previousActive = previous.subjectResults.filter { it.value.scoreBundle.totalCount > 0 }
        if (previousActive.isEmpty()) return null

        val previousOverallGe = previousActive.values.map { it.scoreBundle.gradeEquivalent }.average()

        val subjectChanges = mutableMapOf<Subject, Double>()
        for (subject in Subject.entries) {
            val prevScore = previous.subjectResults[subject]?.scoreBundle?.gradeEquivalent
            val currScore = result.subjectResults[subject]?.scoreBundle?.gradeEquivalent
            if (prevScore != null && currScore != null) {
                subjectChanges[subject] = currScore - prevScore
            }
        }

        return PerformanceTrend(
            previousOverallGe = previousOverallGe,
            currentOverallGe = currentOverallGe,
            subjectChanges = subjectChanges
        )
    }

    private fun generateRecommendations(
        activeResults: Map<Subject, SubjectResult>,
        age: Int,
        overallRisk: RiskTier,
        mastery: List<StandardMastery>
    ): List<String> {
        val recommendations = mutableListOf<String>()

        // Overall risk recommendation
        when (overallRisk) {
            RiskTier.WELL_BELOW_BENCHMARK -> recommendations.add(
                "Overall performance is well below grade level. Consider comprehensive intervention across multiple subjects."
            )
            RiskTier.BELOW_BENCHMARK -> recommendations.add(
                "Overall performance is below grade level. Targeted support in weaker areas is recommended."
            )
            RiskTier.AT_ABOVE_BENCHMARK -> recommendations.add(
                "Overall performance is at or above grade level. Continue current instructional strategies."
            )
            RiskTier.NOT_APPLICABLE -> {} // no overall risk recommendation for N/A
        }

        // Subject-specific recommendations
        val weakSubjects = activeResults.filter {
            it.value.scoreBundle.riskTier == RiskTier.WELL_BELOW_BENCHMARK
        }
        for ((subject, _) in weakSubjects) {
            recommendations.add(
                "${subject.displayName}: Performance indicates significant gaps. Recommend intensive, targeted intervention."
            )
        }

        val belowSubjects = activeResults.filter {
            it.value.scoreBundle.riskTier == RiskTier.BELOW_BENCHMARK
        }
        for ((subject, _) in belowSubjects) {
            recommendations.add(
                "${subject.displayName}: Performance is below expectations. Recommend additional practice and monitoring."
            )
        }

        // Standards mastery recommendation
        val unmasteredStandards = mastery.filter { !it.mastered }
        if (unmasteredStandards.isNotEmpty()) {
            val standardsList = unmasteredStandards.take(5).joinToString(", ") { it.standardCode }
            recommendations.add(
                "Focus on these unmastered standards: $standardsList"
            )
        }

        return recommendations.take(5)
    }
}
