package com.gradeassessment.ui.screen.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.Subject
import com.gradeassessment.ui.theme.subjectColor

@Composable
fun SubjectSelectionScreen(
    onConfirm: (List<Subject>) -> Unit,
    modifier: Modifier = Modifier
) {
    val allSubjects = Subject.entries
    var selected by remember { mutableStateOf(setOf<Subject>()) }

    Row(
        modifier = modifier.fillMaxSize().padding(48.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side — subject checkboxes
        Card(modifier = Modifier.widthIn(max = 500.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Select Subjects",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Choose one or more subjects to assess.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // Select All / Clear All row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { selected = allSubjects.toSet() }) {
                        Text("Select All")
                    }
                    TextButton(onClick = { selected = emptySet() }) {
                        Text("Clear All")
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Core subjects section
                Text(
                    text = "Core Subjects",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))

                Subject.CORE_SUBJECTS.forEach { subject ->
                    SubjectCheckboxRow(
                        subject = subject,
                        checked = subject in selected,
                        onCheckedChange = { checked ->
                            selected = if (checked) selected + subject else selected - subject
                        }
                    )
                }

                // Separator
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Optional subjects section
                Text(
                    text = "Optional Assessments",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(4.dp))

                Subject.OPTIONAL_SUBJECTS.forEach { subject ->
                    SubjectCheckboxRow(
                        subject = subject,
                        checked = subject in selected,
                        onCheckedChange = { checked ->
                            selected = if (checked) selected + subject else selected - subject
                        },
                        isOptional = true
                    )
                }
            }
        }

        // Right side — continue button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${selected.size} selected",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onConfirm(selected.toList()) },
                enabled = selected.isNotEmpty(),
                modifier = Modifier.widthIn(min = 200.dp)
            ) {
                Text("Continue", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun SubjectCheckboxRow(
    subject: Subject,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isOptional: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = subject.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = subjectColor(subject)
        )
        if (isOptional) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = "(Optional)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
