package com.gradeassessment.ui.screen.results

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gradeassessment.domain.model.*
import com.gradeassessment.domain.scoring.ScoringHelper
import com.gradeassessment.ui.components.GradeBadge
import com.gradeassessment.ui.components.GrowthChartSection
import com.gradeassessment.ui.components.ScoreCard
import com.gradeassessment.ui.theme.subjectColor
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ResultDetailDialog(
    result: AssessmentResult,
    analysisReport: AnalysisReport? = null,
    growthHistory: GrowthHistory? = null,
    onExportPdf: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = result.student.firstName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val localDate = result.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                        val durationMin = result.durationMs / 60000
                        Text(
                            text = "Age: ${result.student.age} | Date: ${localDate.monthNumber}/${localDate.dayOfMonth}/${localDate.year} | Duration: ${durationMin}m",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (onExportPdf != null) {
                            OutlinedButton(onClick = onExportPdf) {
                                Text("Export PDF")
                            }
                        }
                        TextButton(onClick = onDismiss) {
                            Text("Close")
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Analysis summary (if available)
                if (analysisReport != null) {
                    AnalysisSummarySection(analysisReport)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                }

                // Growth across all of this student's assessments up to this one
                if (growthHistory != null) {
                    GrowthChartSection(growthHistory)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                }

                // Score summary
                Text(
                    text = "Score Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                val activeSubjects = result.subjectResults.entries
                    .filter { it.value.scoreBundle.totalCount > 0 }
                    .toList()

                val chunked = activeSubjects.chunked(4)
                for (row in chunked) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for ((subject, subjectResult) in row) {
                            ScoreCard(
                                subject = subject,
                                scoreBundle = subjectResult.scoreBundle,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(4 - row.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Per-subject drill-down (dynamic)
                for ((subject, subjectResult) in activeSubjects) {
                    SubjectDrillDown(subject.displayName, subjectResult, subject)
                }

                // Recommendations (if available)
                if (analysisReport != null && analysisReport.recommendations.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    RecommendationsSection(analysisReport.recommendations)
                }
            }
        }
    }
}

@Composable
private fun AnalysisSummarySection(report: AnalysisReport) {
    Text(
        text = "Analysis Summary",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(8.dp))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Overall Grade Equivalent", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "%.1f".format(report.overallGradeEquivalent),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Suggested Grade Level", style = MaterialTheme.typography.labelMedium)
                    Text(
                        GradeLevel.fromGradeEquivalent(report.overallGradeEquivalent).display,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Overall Risk", style = MaterialTheme.typography.labelMedium)
                    GradeBadge(riskTier = report.overallRiskTier)
                }
            }

            Spacer(Modifier.height(12.dp))

            if (report.strongestSubject != null) {
                Text(
                    "Strongest: ${report.strongestSubject.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (report.weakestSubject != null) {
                Text(
                    "Needs Improvement: ${report.weakestSubject.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (report.performanceTrend != null) {
                Spacer(Modifier.height(8.dp))
                val trend = report.performanceTrend
                val diff = trend.currentOverallGe - trend.previousOverallGe
                val arrow = if (diff >= 0) "+" else ""
                Text(
                    "Trend: $arrow%.1f GE from previous assessment".format(diff),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecommendationsSection(recommendations: List<String>) {
    Text(
        text = "Recommendations",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(8.dp))

    for (recommendation in recommendations) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = recommendation,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun SubjectDrillDown(
    title: String,
    subjectResult: SubjectResult,
    subject: Subject
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = subjectColor(subject)
        )
        Spacer(Modifier.height(8.dp))

        val sb = subjectResult.scoreBundle

        // Scoring-mode-specific stats display
        when (sb.scoringMode) {
            ScoringMode.CRITICAL_THINKING -> {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Correct: ${sb.correctCount}/${sb.totalCount}")
                    Text("Percentile: ${sb.ctPercentile ?: "N/A"}", fontWeight = FontWeight.Bold)
                    Text("Theta: %.2f".format(sb.theta))
                    Text("SE: %.2f".format(sb.standardError))
                }
            }
            ScoringMode.IQ_ASSESSMENT -> {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Correct: ${sb.correctCount}/${sb.totalCount}")
                    Text("IQ: ${sb.iqScore ?: "N/A"} (${sb.iqRangeLow}-${sb.iqRangeHigh})", fontWeight = FontWeight.Bold)
                    Text("Theta: %.2f".format(sb.theta))
                    Text("SE: %.2f".format(sb.standardError))
                }
                Spacer(Modifier.height(4.dp))
                // Mandatory IQ disclaimer
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = ScoringHelper.IQ_DISCLAIMER,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            ScoringMode.GRADE_EQUIVALENT -> {
                val suggestedGrade = GradeLevel.fromGradeEquivalent(sb.gradeEquivalent)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Correct: ${sb.correctCount}/${sb.totalCount}")
                    Text("Suggested: ${suggestedGrade.display}", fontWeight = FontWeight.Bold)
                    Text("Theta: %.2f".format(sb.theta))
                    Text("SE: %.2f".format(sb.standardError))
                }
            }
        }

        if (subjectResult.missedQuestions.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Missed Questions:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            subjectResult.missedQuestions.forEach { missed ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(missed.prompt, style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Student: ${missed.studentAnswer} | Correct: ${missed.correctAnswer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        if (missed.standardCode.isNotBlank()) {
                            Text(
                                "Standard: ${missed.standardCode}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}
