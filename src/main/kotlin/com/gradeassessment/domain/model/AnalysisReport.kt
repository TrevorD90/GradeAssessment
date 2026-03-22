package com.gradeassessment.domain.model

data class AnalysisReport(
    val overallGradeEquivalent: Double,
    val overallRiskTier: RiskTier,
    val strongestSubject: Subject?,
    val weakestSubject: Subject?,
    val subjectRankings: List<SubjectRanking>,
    val standardsMastery: List<StandardMastery>,
    val recommendations: List<String>,
    val performanceTrend: PerformanceTrend?
)

data class SubjectRanking(
    val subject: Subject,
    val scaledScore: Int,
    val gradeEquivalent: Double,
    val rank: Int
)

data class StandardMastery(
    val standardCode: String,
    val subject: Subject,
    val mastered: Boolean,
    val correctCount: Int,
    val totalCount: Int
)

data class PerformanceTrend(
    val previousOverallGe: Double,
    val currentOverallGe: Double,
    val subjectChanges: Map<Subject, Double>
)
