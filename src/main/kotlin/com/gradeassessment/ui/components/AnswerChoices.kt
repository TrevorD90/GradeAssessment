package com.gradeassessment.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AnswerChoices(
    choices: List<String>,
    selectedAnswer: String,
    onAnswerSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        choices.forEachIndexed { index, choice ->
            val label = ('A' + index).toString()
            val isSelected = selectedAnswer == choice

            val selectionState = if (isSelected) "selected" else "not selected"
            OutlinedCard(
                onClick = { onAnswerSelected(choice) },
                modifier = Modifier.fillMaxWidth().semantics {
                    contentDescription = "Answer $label: $choice, $selectionState"
                },
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onAnswerSelected(choice) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$label. $choice",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun MultiSelectChoices(
    choices: List<String>,
    selectedAnswers: List<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        choices.forEachIndexed { index, choice ->
            val label = ('A' + index).toString()
            val isSelected = choice in selectedAnswers

            val selectionState = if (isSelected) "selected" else "not selected"
            OutlinedCard(
                onClick = { onToggle(choice) },
                modifier = Modifier.fillMaxWidth().semantics {
                    contentDescription = "Select answer $label: $choice, $selectionState"
                },
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggle(choice) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$label. $choice",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun FillInBlankField(
    answer: String,
    onAnswerChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = answer,
        onValueChange = onAnswerChanged,
        label = { Text("Your Answer") },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}
