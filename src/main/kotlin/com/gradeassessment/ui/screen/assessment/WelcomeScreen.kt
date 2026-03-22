package com.gradeassessment.ui.screen.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    onFullAssessmentClick: () -> Unit,
    onSingleSubjectClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Left column — title + subject descriptions
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight()
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grade Assessment",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Assessment Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Adaptive Academic Assessment",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "This assessment evaluates skills in seven core areas, plus optional cognitive assessments:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(12.dp))

                AssessmentFeature("Math", "Number sense, arithmetic, algebra, geometry")
                AssessmentFeature("Reading", "Comprehension, vocabulary, fluency")
                AssessmentFeature("Writing", "Grammar, punctuation, sentence structure")
                AssessmentFeature("Science", "Scientific reasoning, observation, analysis")
                AssessmentFeature("Arts", "Visual arts, music, creative expression")
                AssessmentFeature("World History", "Civilizations, events, historical thinking")
                AssessmentFeature("Social Studies", "Geography, civics, economics")

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "The test adapts to the student's level. Questions get harder or easier based on performance. " +
                        "Use the gear icon above to enable optional Critical Thinking and IQ assessments.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Right column — two cards: full assessment (top), custom assessment (bottom)
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Top card — Full Assessment
            Card(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Full Assessment",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Complete all 7 subjects in sequence.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onFullAssessmentClick,
                        modifier = Modifier.widthIn(min = 220.dp)
                    ) {
                        Text("Start Full Assessment", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            // Bottom card — Custom Assessment
            Card(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Custom Assessment",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Choose which subjects to assess.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = onSingleSubjectClick,
                        modifier = Modifier.widthIn(min = 220.dp)
                    ) {
                        Text("Choose Subjects", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun AssessmentFeature(title: String, description: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$title: ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
