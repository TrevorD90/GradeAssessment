package com.gradeassessment.domain.model

import kotlin.math.roundToInt

/**
 * Single source of truth for converting between the IRT ability scale (theta),
 * grade equivalents (GE) and the reported scaled score.
 *
 * [DECISION: one linear vertical scale. Previously three unrelated mappings were in use
 *  (starting theta from age, question-bank b-values per band, and a piecewise scaled-score
 *  table), so a child who answered exactly at grade level was reported ~1 grade too high and
 *  "9-10" questions were calibrated as grade 7-9. Everything now goes through this object.]
 *
 * Convention (mirrored in tools/validate_question_bank.py):
 *  - GE g.x means "working like a typical student x tenths of the way through grade g" (K = 0).
 *  - theta = 0.6 * (GE - 5): one grade level is 0.6 theta, grade 5.0 sits at theta 0.
 *  - A question tagged gradeLevel g and difficulty EASY / MEDIUM / HARD has
 *    b = theta(g + 0.2) / theta(g + 0.5) / theta(g + 0.8).
 *
 * [REVIEW NEEDED: item parameters are expert-assigned from grade-level standards, not
 *  empirically calibrated on student response data. Scores are estimates, not normed results.]
 */
object GradeScale {
    const val THETA_PER_GRADE = 0.6
    const val CENTER_GRADE = 5.0

    /** Lowest / highest reportable grade equivalents ("below K" .. "above 10th"). */
    const val MIN_GE = -1.0
    const val MAX_GE = 11.0

    val THETA_MIN: Double = geToTheta(MIN_GE) // -3.6
    val THETA_MAX: Double = geToTheta(MAX_GE) //  3.6

    const val MAX_SCALED_SCORE = 1200

    fun geToTheta(gradeEquivalent: Double): Double = THETA_PER_GRADE * (gradeEquivalent - CENTER_GRADE)

    fun thetaToGe(theta: Double): Double =
        (theta / THETA_PER_GRADE + CENTER_GRADE).coerceIn(MIN_GE, MAX_GE)

    /** Scaled score 0..1200: 100 points per grade level, kindergarten entry (GE 0.0) = 100. */
    fun thetaToScaledScore(theta: Double): Int =
        ((thetaToGe(theta) - MIN_GE) * 100.0).roundToInt().coerceIn(0, MAX_SCALED_SCORE)

    fun scaledScoreToGe(scaledScore: Int): Double =
        (scaledScore / 100.0 + MIN_GE).coerceIn(MIN_GE, MAX_GE)

    /** Grade a child of this age is normally enrolled in (K = 0), e.g. age 8 -> grade 3. */
    fun expectedGradeForAge(age: Int): Int = (age - 5).coerceIn(0, 10)

    /** Mid-year grade equivalent expected for a child of this age, e.g. age 8 -> 3.5. */
    fun expectedGeForAge(age: Int): Double = expectedGradeForAge(age) + 0.5
}
