package com.gradeassessment.domain.agent

import com.gradeassessment.domain.model.*
import com.gradeassessment.domain.scoring.ScoringHelper
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt

object IrtEngine {

    private const val THETA_MIN = -3.0
    private const val THETA_MAX = 3.0
    private const val MAX_SCALED_SCORE = 1400
    private const val SE_THRESHOLD = 0.40
    private const val MIN_QUESTIONS = 15
    private const val MAX_QUESTIONS = 30

    fun probability3PL(theta: Double, params: IrtParameters): Double {
        val exponent = params.a * (theta - params.b)
        val logistic = 1.0 / (1.0 + exp(-exponent))
        return params.c + (1.0 - params.c) * logistic
    }

    fun fisherInformation(theta: Double, params: IrtParameters): Double {
        val p = probability3PL(theta, params)
        val q = 1.0 - p
        if (p <= params.c || p >= 1.0 || q <= 0.0) return 0.0

        val pStar = (p - params.c) / (1.0 - params.c)
        val numerator = params.a * params.a * pStar * pStar * q
        val denominator = p
        return if (denominator > 0.0) numerator / denominator else 0.0
    }

    fun estimateTheta(
        responses: List<Pair<IrtParameters, Boolean>>,
        priorTheta: Double = 0.0,
        maxIterations: Int = 25
    ): Double {
        if (responses.isEmpty()) return priorTheta

        var theta = priorTheta

        for (iteration in 0 until maxIterations) {
            var firstDerivative = 0.0
            var secondDerivative = 0.0

            for ((params, correct) in responses) {
                val p = probability3PL(theta, params)
                val q = 1.0 - p
                val pStar = (p - params.c) / (1.0 - params.c)

                if (p <= params.c || p >= 1.0 || q <= 0.0) continue

                val u = if (correct) 1.0 else 0.0
                val w = params.a * pStar / p

                firstDerivative += w * (u - p)
                secondDerivative -= w * w * p * q
            }

            if (secondDerivative >= 0.0) break

            val delta = firstDerivative / secondDerivative
            theta -= delta
            theta = theta.coerceIn(THETA_MIN, THETA_MAX)

            if (kotlin.math.abs(delta) < 0.001) break
        }

        return theta
    }

    fun standardError(theta: Double, responses: List<Pair<IrtParameters, Boolean>>): Double {
        var totalInfo = 0.0
        for ((params, _) in responses) {
            totalInfo += fisherInformation(theta, params)
        }
        return if (totalInfo > 0.0) 1.0 / sqrt(totalInfo) else 1.0
    }

    fun selectNextQuestion(
        theta: Double,
        availableQuestions: List<Question>,
        askedIds: List<String>,
        lastBloomsLevel: Int? = null,
        lastQuestionType: QuestionType? = null
    ): Question? {
        val candidates = availableQuestions.filter { q ->
            q.id !in askedIds &&
                (lastBloomsLevel == null || q.bloomsLevel != lastBloomsLevel) &&
                (lastQuestionType == null || q.type != lastQuestionType)
        }

        if (candidates.isEmpty()) {
            return availableQuestions.filter { it.id !in askedIds }.maxByOrNull {
                fisherInformation(theta, it.irtParameters)
            }
        }

        return candidates.maxByOrNull { fisherInformation(theta, it.irtParameters) }
    }

    fun shouldTerminate(state: AdaptiveState): Boolean {
        if (state.questionCount >= MAX_QUESTIONS) return true
        if (state.questionCount < MIN_QUESTIONS) return false
        return state.standardError < SE_THRESHOLD
    }

    fun thetaToScaledScore(theta: Double): Int {
        val normalized = (theta - THETA_MIN) / (THETA_MAX - THETA_MIN)
        return (normalized * MAX_SCALED_SCORE).toInt().coerceIn(0, MAX_SCALED_SCORE)
    }

    fun scaledScoreToGradeEquivalent(scaledScore: Int): Double = when {
        scaledScore <= 100 -> -0.5
        scaledScore <= 200 -> 0.0 + (scaledScore - 101) * 0.9 / 99.0
        scaledScore <= 300 -> 1.0 + (scaledScore - 201) * 0.9 / 99.0
        scaledScore <= 400 -> 2.0 + (scaledScore - 301) * 0.9 / 99.0
        scaledScore <= 500 -> 3.0 + (scaledScore - 401) * 0.9 / 99.0
        scaledScore <= 600 -> 4.0 + (scaledScore - 501) * 0.9 / 99.0
        scaledScore <= 700 -> 5.0 + (scaledScore - 601) * 0.9 / 99.0
        scaledScore <= 800 -> 6.0 + (scaledScore - 701) * 0.9 / 99.0
        scaledScore <= 900 -> 7.0 + (scaledScore - 801) * 0.9 / 99.0
        scaledScore <= 1000 -> 8.0 + (scaledScore - 901) * 0.9 / 99.0
        scaledScore <= 1100 -> 9.0 + (scaledScore - 1001) * 0.9 / 99.0
        scaledScore <= 1200 -> 10.0
        else -> 11.0
    }

    fun computeScoreBundle(theta: Double, se: Double, responses: List<StudentResponse>, age: Int): ScoreBundle {
        val scaledScore = thetaToScaledScore(theta)
        val gradeEquivalent = scaledScoreToGradeEquivalent(scaledScore)
        val riskTier = RiskTier.fromGradeEquivalentAndAge(gradeEquivalent, age)
        val correctCount = responses.count { it.correct }

        return ScoreBundle(
            scaledScore = scaledScore,
            gradeEquivalent = gradeEquivalent,
            riskTier = riskTier,
            theta = theta,
            standardError = se,
            correctCount = correctCount,
            totalCount = responses.size
        )
    }

    fun computeCtScoreBundle(theta: Double, se: Double, responses: List<StudentResponse>): ScoreBundle {
        val scaledScore = thetaToScaledScore(theta)
        val percentile = ScoringHelper.thetaToCtPercentile(theta)
        val correctCount = responses.count { it.correct }

        return ScoreBundle(
            scaledScore = scaledScore,
            gradeEquivalent = 0.0, // CT doesn't produce a grade equivalent
            riskTier = RiskTier.NOT_APPLICABLE,
            theta = theta,
            standardError = se,
            correctCount = correctCount,
            totalCount = responses.size,
            scoringMode = ScoringMode.CRITICAL_THINKING,
            ctPercentile = percentile
        )
    }

    fun computeIqScoreBundle(theta: Double, se: Double, responses: List<StudentResponse>): ScoreBundle {
        val scaledScore = thetaToScaledScore(theta)
        val iqScore = ScoringHelper.thetaToIqScore(theta)
        val (rangeLow, rangeHigh) = ScoringHelper.thetaToIqRange(theta, se)
        val correctCount = responses.count { it.correct }

        return ScoreBundle(
            scaledScore = scaledScore,
            gradeEquivalent = 0.0, // IQ doesn't produce a grade equivalent
            riskTier = RiskTier.NOT_APPLICABLE,
            theta = theta,
            standardError = se,
            correctCount = correctCount,
            totalCount = responses.size,
            scoringMode = ScoringMode.IQ_ASSESSMENT,
            iqScore = iqScore,
            iqRangeLow = rangeLow,
            iqRangeHigh = rangeHigh,
            disclaimer = ScoringHelper.IQ_DISCLAIMER
        )
    }
}
