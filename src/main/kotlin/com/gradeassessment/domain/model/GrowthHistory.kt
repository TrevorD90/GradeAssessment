package com.gradeassessment.domain.model

import kotlinx.datetime.Instant

/** One assessment session as a point on a student's growth chart. */
data class GrowthPoint(
    val sessionId: Long,
    val date: Instant,
    val age: Int,
    /** Grade equivalent per core subject taken in this session. */
    val subjectGe: Map<Subject, Double>
) {
    /** Grade equivalent expected for the student's age at this session (mid-year of the age's grade). */
    val expectedGe: Double get() = GradeScale.expectedGeForAge(age)

    /**
     * Mean grade equivalent across the subjects taken in this session, or null if none.
     * Sessions that covered different subjects are not strictly comparable on this figure;
     * the per-subject lines are the precise view.
     */
    val overallGe: Double? get() = subjectGe.values.takeIf { it.isNotEmpty() }?.average()

    /** The value for [series]: one subject, or the overall mean when [series] is null. */
    fun valueFor(series: Subject?): Double? = if (series == null) overallGe else subjectGe[series]
}

/**
 * A student's grade-equivalent history, oldest first, for growth charts and reports.
 *
 * @param excludedSessions sessions left out because their scores use an older, non-comparable
 *        version of the grade scale (see [GradeScale.VERSION]).
 */
data class GrowthHistory(
    val points: List<GrowthPoint>,
    val excludedSessions: Int = 0
) {
    /** Core subjects that appear in at least one session, in the standard subject order. */
    val subjects: List<Subject> get() = Subject.CORE_SUBJECTS.filter { s -> points.any { s in it.subjectGe } }

    /** True when there are at least two points to compare: growth needs a before and an after. */
    val hasGrowth: Boolean get() = points.size >= 2

    /** Change in grade levels from the first to the last session that has a value for [series]. */
    fun change(series: Subject?): Double? {
        val values = points.mapNotNull { it.valueFor(series) }
        return if (values.size >= 2) values.last() - values.first() else null
    }

    companion object {
        /**
         * Builds the history from a student's saved results, up to and including [upTo].
         * Only core subjects with answered questions on the current scale are used.
         * O(sessions x subjects).
         */
        fun from(results: List<AssessmentResult>, upTo: Instant): GrowthHistory {
            var excluded = 0
            val points = results
                .filter { it.startTime <= upTo }
                .sortedBy { it.startTime }
                .mapNotNull { result ->
                    val scored = result.subjectResults.filter { (subject, sr) ->
                        subject in Subject.CORE_SUBJECTS && sr.scoreBundle.totalCount > 0
                    }
                    val current = scored.filter { it.value.scoreBundle.scaleVersion == GradeScale.VERSION }
                    when {
                        current.isNotEmpty() -> GrowthPoint(
                            sessionId = result.sessionId,
                            date = result.startTime,
                            age = result.student.age,
                            subjectGe = current.mapValues { it.value.scoreBundle.gradeEquivalent }
                        )
                        scored.isNotEmpty() -> { excluded++; null } // older scale only
                        else -> null // CT/IQ-only or empty session: nothing to plot
                    }
                }
            return GrowthHistory(points, excluded)
        }
    }
}
