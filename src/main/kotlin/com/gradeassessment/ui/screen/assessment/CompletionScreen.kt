package com.gradeassessment.ui.screen.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.AssessmentResult
import com.gradeassessment.domain.model.Subject
import com.gradeassessment.ui.components.ScoreCard

@Composable
fun CompletionScreen(
    result: AssessmentResult?,
    onExportPdf: (() -> Unit)? = null,
    onNewAssessment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Assessment Complete!",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        if (result != null) {
            Spacer(Modifier.height(8.dp))

            val durationMinutes = result.durationMs / 60000
            val durationSeconds = (result.durationMs % 60000) / 1000
            Text(
                text = "Student: ${result.student.firstName} (Age ${result.student.age}) | Duration: ${durationMinutes}m ${durationSeconds}s",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Dynamic score cards in rows of 3-4
            val subjects = result.subjectResults.entries
                .filter { it.value.scoreBundle.totalCount > 0 }
                .toList()
            val chunked = subjects.chunked(4)
            for (row in chunked) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    for ((subject, subjectResult) in row) {
                        ScoreCard(
                            subject = subject,
                            scoreBundle = subjectResult.scoreBundle,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if row has fewer than 4
                    repeat(4 - row.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (onExportPdf != null) {
                OutlinedButton(
                    onClick = onExportPdf,
                    modifier = Modifier.widthIn(min = 200.dp)
                ) {
                    Text("Export PDF Report")
                }
            }

            Button(
                onClick = onNewAssessment,
                modifier = Modifier.widthIn(min = 200.dp)
            ) {
                Text("Start New Assessment")
            }
        }
    }
}
