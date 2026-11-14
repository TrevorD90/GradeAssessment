package com.gradeassessment

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.use
import com.gradeassessment.domain.model.*
import com.gradeassessment.ui.components.GrowthChartSection
import kotlinx.datetime.Instant
import org.jetbrains.skia.EncodedImageFormat
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GrowthHistoryTest {

    private fun result(session: Long, isoDate: String, age: Int, scores: Map<Subject, Double>, scaleVersion: Int = GradeScale.VERSION) =
        AssessmentResult(
            sessionId = session,
            student = Student("Ava", age),
            startTime = Instant.parse(isoDate),
            endTime = Instant.parse(isoDate),
            durationMs = 0,
            subjectResults = scores.mapValues { (subject, ge) ->
                SubjectResult(
                    subject = subject,
                    scoreBundle = ScoreBundle(
                        scaledScore = 0, gradeEquivalent = ge, riskTier = RiskTier.AT_ABOVE_BENCHMARK,
                        theta = 0.0, standardError = 0.3, correctCount = 10, totalCount = 20,
                        scaleVersion = scaleVersion
                    ),
                    responses = emptyList()
                )
            }
        )

    private val sample = listOf(
        result(1, "2025-01-10T15:00:00Z", 8, mapOf(Subject.MATH to 2.0), scaleVersion = 1), // old scale
        result(2, "2026-01-12T15:00:00Z", 8, mapOf(Subject.MATH to 2.6, Subject.READING to 3.1, Subject.WRITING to 2.4)),
        result(3, "2026-03-20T15:00:00Z", 8, mapOf(Subject.MATH to 3.0, Subject.READING to 3.3, Subject.WRITING to 2.9, Subject.SCIENCE to 3.0)),
        result(4, "2026-06-05T15:00:00Z", 9, mapOf(Subject.MATH to 3.6, Subject.READING to 3.7, Subject.WRITING to 3.0, Subject.SCIENCE to 3.4)),
        result(5, "2026-09-15T15:00:00Z", 9, mapOf(Subject.CRITICAL_THINKING to 0.0)), // CT only: nothing to plot
        result(6, "2026-11-13T15:00:00Z", 9, mapOf(Subject.MATH to 4.4, Subject.READING to 4.1, Subject.WRITING to 3.5, Subject.SCIENCE to 3.9))
    )

    @Test
    fun `history keeps comparable core-subject sessions in date order`() {
        val history = GrowthHistory.from(sample.shuffled(), upTo = Instant.parse("2026-12-31T00:00:00Z"))
        assertEquals(listOf(2L, 3L, 4L, 6L), history.points.map { it.sessionId })
        assertEquals(1, history.excludedSessions) // the old-scale session
        assertEquals(listOf(Subject.MATH, Subject.READING, Subject.WRITING, Subject.SCIENCE), history.subjects)
        assertEquals(4.4 - 2.6, history.change(Subject.MATH)!!, 1e-9)
        assertEquals(3.9 - 3.0, history.change(Subject.SCIENCE)!!, 1e-9) // first session it was taken
        assertTrue(history.hasGrowth)
        assertEquals(3.5, history.points.first().expectedGe, 1e-9) // age 8 -> grade 3, mid-year
    }

    @Test
    fun `history stops at the result being viewed`() {
        val history = GrowthHistory.from(sample, upTo = Instant.parse("2026-03-20T15:00:00Z"))
        assertEquals(listOf(2L, 3L), history.points.map { it.sessionId })
        val single = GrowthHistory.from(sample, upTo = Instant.parse("2026-01-12T15:00:00Z"))
        assertFalse(single.hasGrowth)
        assertNull(single.change(null))
    }

    /** Renders the chart off-screen so it can be inspected: build/growth-chart-preview.png. */
    @Test
    fun `growth chart renders`() {
        val history = GrowthHistory.from(sample, upTo = Instant.parse("2026-12-31T00:00:00Z"))
        val png = ImageComposeScene(width = 1100, height = 900).use { scene ->
            scene.setContent {
                MaterialTheme {
                    Surface { GrowthChartSection(history, Modifier.width(1060.dp).padding(20.dp)) }
                }
            }
            scene.render().encodeToData(EncodedImageFormat.PNG)!!.bytes
        }
        val out = File("build/growth-chart-preview.png")
        out.writeBytes(png)
        assertTrue(out.length() > 10_000)
    }
}
