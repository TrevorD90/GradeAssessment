package com.gradeassessment.domain.model

data class ScoreBundle(
    val scaledScore: Int,
    val gradeEquivalent: Double,
    val riskTier: RiskTier,
    val theta: Double,
    val standardError: Double,
    val correctCount: Int,
    val totalCount: Int,
    val scoringMode: ScoringMode = ScoringMode.GRADE_EQUIVALENT,
    val iqScore: Int? = null,
    val iqRangeLow: Int? = null,
    val iqRangeHigh: Int? = null,
    val ctPercentile: Int? = null,
    val disclaimer: String? = null,
    /** GradeScale version the score was computed on; see [GradeScale.VERSION]. */
    val scaleVersion: Int = GradeScale.VERSION
)
