package com.gradeassessment.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.Subject
import com.gradeassessment.ui.theme.subjectColor

@Composable
fun AssessmentProgressBar(
    currentQuestion: Int,
    totalQuestions: Int,
    subject: Subject,
    timeRemainingSeconds: Int?,
    modifier: Modifier = Modifier
) {
    val timeDesc = if (timeRemainingSeconds != null) {
        val m = timeRemainingSeconds / 60
        val s = timeRemainingSeconds % 60
        ", $m minutes $s seconds remaining"
    } else ""
    val progressDesc = "${subject.displayName}, question $currentQuestion of $totalQuestions$timeDesc"

    Column(modifier = modifier.fillMaxWidth().semantics { contentDescription = progressDesc }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${subject.name} - Question $currentQuestion / $totalQuestions",
                style = MaterialTheme.typography.titleSmall,
                color = subjectColor(subject)
            )
            if (timeRemainingSeconds != null) {
                val minutes = timeRemainingSeconds / 60
                val seconds = timeRemainingSeconds % 60
                Text(
                    text = "%d:%02d".format(minutes, seconds),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (timeRemainingSeconds < 30) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { if (totalQuestions > 0) currentQuestion.toFloat() / totalQuestions else 0f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = subjectColor(subject),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
