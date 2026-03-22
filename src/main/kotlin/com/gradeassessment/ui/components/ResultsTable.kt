package com.gradeassessment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.AssessmentResult
import com.gradeassessment.domain.model.ScoreBundle
import com.gradeassessment.domain.model.Subject
import com.gradeassessment.ui.theme.riskTierColor
import com.gradeassessment.ui.theme.subjectColor
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private fun subjectAbbrev(subject: Subject): String = when (subject) {
    Subject.MATH -> "Math"
    Subject.READING -> "Read"
    Subject.WRITING -> "Write"
    Subject.SCIENCE -> "Sci"
    Subject.ARTS -> "Arts"
    Subject.WORLD_HISTORY -> "WHist"
    Subject.SOCIAL_STUDIES -> "SStud"
    Subject.CRITICAL_THINKING -> "CT"
    Subject.IQ_ASSESSMENT -> "IQ"
}

@Composable
fun ResultsTable(
    results: List<AssessmentResult>,
    onResultClick: (AssessmentResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize().horizontalScroll(scrollState)) {
        // Header
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableHeader("Name", Modifier.width(120.dp))
            TableHeader("Age", Modifier.width(48.dp))
            TableHeader("Date", Modifier.width(90.dp))
            for (subject in Subject.entries) {
                val abbrev = subjectAbbrev(subject)
                TableHeader("$abbrev SS", Modifier.width(64.dp))
                TableHeader("$abbrev GE", Modifier.width(64.dp))
            }
        }

        HorizontalDivider()

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(results) { result ->
                ResultRow(result, onResultClick)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun TableHeader(text: String, modifier: Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ResultRow(
    result: AssessmentResult,
    onClick: (AssessmentResult) -> Unit
) {
    val rowDesc = "Result for ${result.student.firstName}, age ${result.student.age}, click to view details"
    Row(
        modifier = Modifier
            .semantics { contentDescription = rowDesc }
            .clickable { onClick(result) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(result.student.firstName, modifier = Modifier.width(120.dp))
        Text("${result.student.age}", modifier = Modifier.width(48.dp), textAlign = TextAlign.Center)

        val localDate = result.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
        Text(
            "${localDate.monthNumber}/${localDate.dayOfMonth}/${localDate.year}",
            modifier = Modifier.width(90.dp),
            textAlign = TextAlign.Center
        )

        for (subject in Subject.entries) {
            val subjectResult = result.subjectResults[subject]
            if (subjectResult != null && subjectResult.scoreBundle.totalCount > 0) {
                ScoreCell(subjectResult.scoreBundle, subject, Modifier.width(64.dp))
                GeCell(subjectResult.scoreBundle, Modifier.width(64.dp))
            } else {
                Text("--", modifier = Modifier.width(64.dp), textAlign = TextAlign.Center)
                Text("--", modifier = Modifier.width(64.dp), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun ScoreCell(scoreBundle: ScoreBundle, subject: Subject, modifier: Modifier) {
    Text(
        text = "${scoreBundle.scaledScore}",
        modifier = modifier,
        textAlign = TextAlign.Center,
        color = subjectColor(subject),
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun GeCell(scoreBundle: ScoreBundle, modifier: Modifier) {
    Text(
        text = "%.1f".format(scoreBundle.gradeEquivalent),
        modifier = modifier,
        textAlign = TextAlign.Center,
        color = riskTierColor(scoreBundle.riskTier),
        fontWeight = FontWeight.Bold
    )
}
