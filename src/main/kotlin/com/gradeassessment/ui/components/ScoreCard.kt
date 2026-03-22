package com.gradeassessment.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.GradeLevel
import com.gradeassessment.domain.model.ScoreBundle
import com.gradeassessment.domain.model.ScoringMode
import com.gradeassessment.domain.model.Subject
import com.gradeassessment.ui.theme.subjectColor

@Composable
fun ScoreCard(
    subject: Subject,
    scoreBundle: ScoreBundle,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = subjectColor(subject).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = subject.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = subjectColor(subject)
            )

            Spacer(Modifier.height(12.dp))

            when (scoreBundle.scoringMode) {
                ScoringMode.CRITICAL_THINKING -> {
                    // Show percentile as the primary metric
                    Text(
                        text = "${scoreBundle.ctPercentile ?: 0}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Percentile",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Scaled: ${scoreBundle.scaledScore}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ScoringMode.IQ_ASSESSMENT -> {
                    // Show IQ score + range as the primary metric
                    Text(
                        text = "${scoreBundle.iqScore ?: 0}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "IQ Score",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Range: ${scoreBundle.iqRangeLow ?: 0} - ${scoreBundle.iqRangeHigh ?: 0}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    // Mandatory IQ disclaimer
                    Text(
                        text = "Informational only. Not a diagnosis.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
                ScoringMode.GRADE_EQUIVALENT -> {
                    // Standard grade-equivalent display
                    val gradeLevel = GradeLevel.fromGradeEquivalent(scoreBundle.gradeEquivalent)
                    Text(
                        text = "${scoreBundle.scaledScore}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scaled Score",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = gradeLevel.display,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Suggested Level",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "GE: %.1f".format(scoreBundle.gradeEquivalent),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    GradeBadge(riskTier = scoreBundle.riskTier)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${scoreBundle.correctCount} / ${scoreBundle.totalCount} correct",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
