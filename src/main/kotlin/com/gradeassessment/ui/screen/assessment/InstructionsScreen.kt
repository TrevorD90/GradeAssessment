package com.gradeassessment.ui.screen.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.Subject

@Composable
fun InstructionsScreen(
    studentName: String,
    selectedSubjects: List<Subject>,
    onBeginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ready, $studentName?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        Card(modifier = Modifier.widthIn(max = 500.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                InstructionItem("1", "You will answer questions in ${formatSubjectList(selectedSubjects)}.")
                InstructionItem("2", "Questions adapt to your level - they may get harder or easier.")
                InstructionItem("3", "You have up to 4 minutes per question.")
                InstructionItem("4", "Different question types will appear: multiple choice, fill-in-the-blank, ordering, and more.")
                InstructionItem("5", "Do your best! There is no penalty for wrong answers.")
                InstructionItem("6", "If you don't know an answer, make your best guess.")
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onBeginClick,
            modifier = Modifier.widthIn(min = 200.dp)
        ) {
            Text("Start Assessment", style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun formatSubjectList(subjects: List<Subject>): String = when (subjects.size) {
    0 -> ""
    1 -> subjects.first().displayName
    2 -> "${subjects[0].displayName} and ${subjects[1].displayName}"
    else -> subjects.dropLast(1).joinToString(", ") { it.displayName } +
            ", and ${subjects.last().displayName}"
}

@Composable
private fun InstructionItem(number: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$number. ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
