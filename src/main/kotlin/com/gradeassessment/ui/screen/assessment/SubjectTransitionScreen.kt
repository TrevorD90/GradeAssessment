package com.gradeassessment.ui.screen.assessment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.ScoreBundle
import com.gradeassessment.domain.model.Subject
import com.gradeassessment.ui.components.ScoreCard

@Composable
fun SubjectTransitionScreen(
    completedSubject: Subject,
    scoreBundle: ScoreBundle?,
    nextSubject: Subject?,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${completedSubject.displayName} Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(24.dp))

        if (scoreBundle != null) {
            ScoreCard(
                subject = completedSubject,
                scoreBundle = scoreBundle,
                modifier = Modifier.widthIn(max = 300.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        if (nextSubject != null) {
            Text(
                text = "Next up: ${nextSubject.displayName}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onContinueClick,
                modifier = Modifier.widthIn(min = 200.dp)
            ) {
                Text("Continue to ${nextSubject.displayName}")
            }
        }
    }
}
