package com.gradeassessment.domain.model

enum class RiskTier(val display: String) {
    AT_ABOVE_BENCHMARK("At/Above Benchmark"),
    BELOW_BENCHMARK("Below Benchmark"),
    WELL_BELOW_BENCHMARK("Well Below Benchmark"),
    NOT_APPLICABLE("N/A");

    companion object {
        fun fromGradeEquivalentAndAge(gradeEquivalent: Double, age: Int): RiskTier {
            val expectedGrade = (age - 5).coerceAtLeast(0).toDouble()
            val gap = expectedGrade - gradeEquivalent
            return when {
                gradeEquivalent < 0 -> WELL_BELOW_BENCHMARK // negative GE is always critical
                gap < 0.5 -> AT_ABOVE_BENCHMARK   // within half a grade level
                gap < 1.5 -> BELOW_BENCHMARK       // 0.5–1.5 grade levels behind
                else -> WELL_BELOW_BENCHMARK        // more than 1.5 grade levels behind
            }
        }
    }
}
