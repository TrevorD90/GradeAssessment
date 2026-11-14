package com.gradeassessment.domain.agent

import com.gradeassessment.domain.model.*
import com.gradeassessment.domain.scoring.ScoringHelper
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * 3PL IRT maths for the adaptive test.
 *
 * Complexity summary (N = questions in the subject, k = answers so far, G = grid points, W = window):
 *  - absorbing an answer:      O(G)             (was O(k * N) per answer, O(k^2 * N) per subject)
 *  - choosing the next item:   O(log N + W + k) (was O(N * k): full scan with list membership tests)
 *  - looking up a question:    O(1)             (was O(N)), see QuestionRepository
 */
object IrtEngine {

    private val THETA_MIN = GradeScale.THETA_MIN
    private val THETA_MAX = GradeScale.THETA_MAX

    // [DECISION: stop once the posterior SD is below 0.36 theta = 0.6 of a grade level, i.e. the
    //  reported grade is known to roughly +/- half a grade, after at least 15 and at most 30 items.]
    const val SE_THRESHOLD = 0.36
    const val MIN_QUESTIONS = 15
    const val MAX_QUESTIONS = 30

    /** Prior SD of ability around the age-expected level: 1.2 theta = 2 grade levels (weak prior). */
    const val PRIOR_SD = 1.2

    /** Quadrature grid for EAP estimation: 121 points, 0.06 theta (= 0.1 grade) apart. */
    private const val GRID_SIZE = 121
    private val GRID: DoubleArray = DoubleArray(GRID_SIZE) { i ->
        THETA_MIN + i * (THETA_MAX - THETA_MIN) / (GRID_SIZE - 1)
    }

    /** How many unasked questions nearest the current ability are scored for information. */
    private const val SELECTION_WINDOW = 24

    /** Pick at random among this many most-informative candidates ("randomesque" exposure control). */
    private const val RANDOMESQUE_TOP = 4

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
        return params.a * params.a * pStar * pStar * q / p
    }

    /**
     * Adds one scored response to the running log-likelihood grid. O(G).
     * Returns a new array (state objects are immutable snapshots).
     */
    fun absorbResponse(logLikelihood: DoubleArray?, params: IrtParameters, correct: Boolean): DoubleArray {
        val updated = logLikelihood?.copyOf() ?: DoubleArray(GRID_SIZE)
        for (i in 0 until GRID_SIZE) {
            // Clamp so a single surprising answer can never zero-out a region of the scale.
            val p = probability3PL(GRID[i], params).coerceIn(1e-6, 1.0 - 1e-6)
            updated[i] += ln(if (correct) p else 1.0 - p)
        }
        return updated
    }

    /**
     * Expected-a-posteriori ability estimate and its posterior standard deviation. O(G).
     *
     * [DECISION: EAP with a normal prior instead of Newton-Raphson maximum likelihood. The MLE
     *  does not exist while every answer so far is right (or every answer is wrong), so the old
     *  code jumped to the top (or bottom) of the scale after the first answer: a kindergartner who
     *  got question 1 right was next shown 10th-grade material. EAP is always finite and moves in
     *  sensible steps.]
     */
    fun estimate(logLikelihood: DoubleArray?, priorMean: Double): Pair<Double, Double> {
        val logPost = DoubleArray(GRID_SIZE)
        var maxLog = Double.NEGATIVE_INFINITY
        for (i in 0 until GRID_SIZE) {
            val z = (GRID[i] - priorMean) / PRIOR_SD
            logPost[i] = -0.5 * z * z + (logLikelihood?.get(i) ?: 0.0)
            if (logPost[i] > maxLog) maxLog = logPost[i]
        }
        var sumW = 0.0
        var sumWT = 0.0
        var sumWTT = 0.0
        for (i in 0 until GRID_SIZE) {
            val w = exp(logPost[i] - maxLog) // subtract the max for numerical stability
            sumW += w
            sumWT += w * GRID[i]
            sumWTT += w * GRID[i] * GRID[i]
        }
        val mean = sumWT / sumW
        val variance = (sumWTT / sumW - mean * mean).coerceAtLeast(0.0)
        return mean.coerceIn(THETA_MIN, THETA_MAX) to sqrt(variance)
    }

    /**
     * Chooses the next question: the most informative unasked items near the current ability,
     * preferring a different question type / Bloom's level than the previous item, then a random
     * pick among the best few so repeat test-takers do not always see the same sequence.
     *
     * @param sortedByDifficulty the subject's questions sorted ascending by b (see QuestionRepository).
     *
     * O(log N) binary search + O(W + k) outward walk, instead of scoring all N questions.
     * Information peaks where b is close to theta, so only the W nearest unasked items are scored.
     */
    fun selectNextQuestion(
        theta: Double,
        sortedByDifficulty: List<Question>,
        askedIds: Set<String>,
        lastBloomsLevel: Int? = null,
        lastQuestionType: QuestionType? = null,
        random: Random = Random.Default
    ): Question? {
        val n = sortedByDifficulty.size
        if (n == 0) return null

        // Binary search: first index whose b >= theta.
        var lo = 0
        var hi = n
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (sortedByDifficulty[mid].irtParameters.b < theta) lo = mid + 1 else hi = mid
        }

        // Walk outward from the insertion point, always taking the closer side next.
        val window = ArrayList<Question>(SELECTION_WINDOW)
        var left = lo - 1
        var right = lo
        while (window.size < SELECTION_WINDOW && (left >= 0 || right < n)) {
            val takeLeft = when {
                left < 0 -> false
                right >= n -> true
                else -> theta - sortedByDifficulty[left].irtParameters.b <=
                    sortedByDifficulty[right].irtParameters.b - theta
            }
            val candidate = if (takeLeft) sortedByDifficulty[left--] else sortedByDifficulty[right++]
            if (candidate.id !in askedIds) window.add(candidate)
        }
        if (window.isEmpty()) return null

        // Content balancing: avoid the same format and the same thinking level twice in a row
        // when an alternative exists; relax step by step rather than leaving the ability region.
        val varied = window.filter {
            (lastQuestionType == null || it.type != lastQuestionType) &&
                (lastBloomsLevel == null || it.bloomsLevel != lastBloomsLevel)
        }.ifEmpty {
            window.filter { lastQuestionType == null || it.type != lastQuestionType }
        }.ifEmpty { window }

        val best = varied
            .sortedByDescending { fisherInformation(theta, it.irtParameters) }
            .take(RANDOMESQUE_TOP)
        return best[random.nextInt(best.size)]
    }

    fun shouldTerminate(state: AdaptiveState): Boolean {
        if (state.questionCount >= MAX_QUESTIONS) return true
        if (state.questionCount < MIN_QUESTIONS) return false
        return state.standardError < SE_THRESHOLD
    }

    fun thetaToScaledScore(theta: Double): Int = GradeScale.thetaToScaledScore(theta)

    fun scaledScoreToGradeEquivalent(scaledScore: Int): Double = GradeScale.scaledScoreToGe(scaledScore)

    fun computeScoreBundle(theta: Double, se: Double, responses: List<StudentResponse>, age: Int): ScoreBundle {
        val scaledScore = thetaToScaledScore(theta)
        // One decimal place: GE 3.4 = "like a typical student four tenths of the way through grade 3".
        val gradeEquivalent = round(GradeScale.thetaToGe(theta) * 10.0) / 10.0
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

    /**
     * Critical Thinking and IQ are reported relative to same-age peers, so the ability is first
     * re-centred on the level expected for the student's age and expressed in SD units.
     * (Previously the raw vertical-scale theta was used, so every young child scored far below
     * average and every teenager far above, regardless of how they actually reasoned.)
     *
     * [ASSUMPTION: within one age group, reasoning ability has an SD of about 2 grade levels
     *  (PRIOR_SD). REVIEW NEEDED: this is not an empirical norm.]
     */
    fun ageRelativeZ(theta: Double, age: Int): Double =
        (theta - GradeScale.geToTheta(GradeScale.expectedGeForAge(age))) / PRIOR_SD

    fun computeCtScoreBundle(theta: Double, se: Double, responses: List<StudentResponse>, age: Int): ScoreBundle {
        val scaledScore = thetaToScaledScore(theta)
        val percentile = ScoringHelper.thetaToCtPercentile(ageRelativeZ(theta, age))
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

    fun computeIqScoreBundle(theta: Double, se: Double, responses: List<StudentResponse>, age: Int): ScoreBundle {
        val scaledScore = thetaToScaledScore(theta)
        val z = ageRelativeZ(theta, age)
        val zSe = se / PRIOR_SD
        val iqScore = ScoringHelper.thetaToIqScore(z)
        val (rangeLow, rangeHigh) = ScoringHelper.thetaToIqRange(z, zSe)
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
