package com.gradeassessment.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.AppSettings
import com.gradeassessment.ui.theme.CriticalThinkingIndigo
import com.gradeassessment.ui.theme.IqAssessmentGold

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctEnabled by AppSettings.includeCriticalThinkingInFull.collectAsState()
    val iqEnabled by AppSettings.includeIqAssessmentInFull.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Welcome"
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Assessment Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Configure which optional assessments are included in a full assessment run.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        Card(modifier = Modifier.widthIn(max = 600.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Optional Assessments for Full Assessment",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                // Critical Thinking toggle
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Critical Thinking",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = CriticalThinkingIndigo
                        )
                        Text(
                            text = "Measures inference, deduction, argument evaluation, and pattern logic. " +
                                "Results displayed as age-relative percentile (no grade equivalent).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Switch(
                        checked = ctEnabled,
                        onCheckedChange = { AppSettings.setIncludeCriticalThinking(it) }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // IQ Assessment toggle
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "IQ Assessment",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = IqAssessmentGold
                        )
                        Text(
                            text = "Measures pattern recognition, analogies, logical deduction, and spatial reasoning. " +
                                "Results displayed as IQ score with 95% confidence interval.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Note: IQ results include a mandatory disclaimer and are for informational purposes only.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Switch(
                        checked = iqEnabled,
                        onCheckedChange = { AppSettings.setIncludeIqAssessment(it) }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "These settings only affect the Full Assessment mode. " +
                "In Custom Assessment, all 9 subjects are always available for selection.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
