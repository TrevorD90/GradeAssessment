package com.gradeassessment.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.Question

@Composable
fun ClozeQuestion(
    question: Question,
    answers: List<String>,
    onAnswerChanged: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val parts = question.prompt.split("___")
        val blankCount = parts.size - 1

        Text(
            text = "Fill in the blanks:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            parts.forEachIndexed { index, part ->
                if (part.isNotBlank()) {
                    Text(
                        text = part.trim(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (index < blankCount) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Blank ${index + 1}: ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        OutlinedTextField(
                            value = answers.getOrElse(index) { "" },
                            onValueChange = { onAnswerChanged(index, it) },
                            modifier = Modifier.width(200.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
