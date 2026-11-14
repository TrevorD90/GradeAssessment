package com.gradeassessment.domain.model

/**
 * Immutable snapshot of one subject's adaptive test.
 *
 * Big-O notes:
 *  - [posteriorLogLikelihood] caches the running log-likelihood on the quadrature grid, so
 *    absorbing a new answer costs O(G) (G = grid size, constant) instead of re-scoring every
 *    previous response (previously O(k * N): k responses, each found by a linear scan of N questions).
 *  - [askedIds] is a hash set, so "was this question already asked?" is O(1) instead of O(k).
 */
data class AdaptiveState(
    val currentTheta: Double,
    val standardError: Double = 1.0,
    val responses: List<StudentResponse> = emptyList(),
    val questionsAsked: List<String> = emptyList(),
    val lastBloomsLevel: Int? = null,
    val lastQuestionType: QuestionType? = null,
    /** Centre of the Bayesian prior (ability expected for the student's age). */
    val priorMean: Double = currentTheta,
    /** Running log-likelihood per quadrature point; null until the first answer. */
    val posteriorLogLikelihood: DoubleArray? = null,
    val askedIds: Set<String> = emptySet()
) {
    val questionCount: Int get() = responses.size
    val correctCount: Int get() = responses.count { it.correct }

    companion object {
        /**
         * How far below the age-expected level the first question is pitched, in grade levels.
         * [DECISION: start one grade below expectation. Operational adaptive tests (Star, i-Ready)
         *  start at or below the enrolled grade so the first items build confidence, then move up
         *  or down after every answer. Starting at the very easiest item for everyone would waste
         *  questions and bore older students.]
         */
        const val START_OFFSET_GRADES = 1.0

        fun fromAge(age: Int): AdaptiveState {
            val expectedGe = GradeScale.expectedGeForAge(age)
            val priorMean = GradeScale.geToTheta(expectedGe)
            val startTheta = GradeScale.geToTheta(expectedGe - START_OFFSET_GRADES)
                .coerceIn(GradeScale.THETA_MIN, GradeScale.THETA_MAX)
            return AdaptiveState(currentTheta = startTheta, priorMean = priorMean)
        }
    }
}
