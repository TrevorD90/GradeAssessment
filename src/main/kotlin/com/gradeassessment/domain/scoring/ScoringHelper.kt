package com.gradeassessment.domain.scoring

import kotlin.math.abs
import kotlin.math.exp

object ScoringHelper {

    const val IQ_DISCLAIMER =
        "These observations are informational only and do not constitute a medical or psychological diagnosis. " +
        "This IQ estimate is based on a brief adaptive assessment and should not be used as a substitute for " +
        "a comprehensive psychological evaluation administered by a qualified professional."

    /**
     * Convert IRT theta to IQ scale score: IQ = 100 + 15 * theta, clamped to [40, 160].
     */
    fun thetaToIqScore(theta: Double): Int {
        return (100 + 15 * theta).toInt().coerceIn(40, 160)
    }

    /**
     * Compute 95% confidence interval for IQ score: IQ ± 1.96 * 15 * SE.
     */
    fun thetaToIqRange(theta: Double, se: Double): Pair<Int, Int> {
        val iqScore = 100.0 + 15.0 * theta
        val margin = 1.96 * 15.0 * se
        val low = (iqScore - margin).toInt().coerceIn(40, 160)
        val high = (iqScore + margin).toInt().coerceIn(40, 160)
        return Pair(low, high)
    }

    /**
     * Convert IRT theta to age-relative percentile via standard normal CDF, clamped to [1, 99].
     * Uses Horner's method approximation of the cumulative normal distribution.
     */
    fun thetaToCtPercentile(theta: Double): Int {
        val z = theta
        val t = 1.0 / (1.0 + 0.2316419 * abs(z))
        val d = 0.3989422804014327 // 1 / sqrt(2 * pi)
        val poly = t * (0.319381530 +
            t * (-0.356563782 +
                t * (1.781477937 +
                    t * (-1.821255978 +
                        t * 1.330274429))))
        val p = d * exp(-z * z / 2.0) * poly
        val cdf = if (z >= 0) 1.0 - p else p
        return (cdf * 100).toInt().coerceIn(1, 99)
    }
}
