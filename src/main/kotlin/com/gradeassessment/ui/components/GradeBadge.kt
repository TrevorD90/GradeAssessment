package com.gradeassessment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gradeassessment.domain.model.RiskTier
import com.gradeassessment.ui.theme.riskTierColor

@Composable
fun GradeBadge(
    riskTier: RiskTier,
    modifier: Modifier = Modifier
) {
    // Don't display a badge for NOT_APPLICABLE (CT/IQ subjects)
    if (riskTier == RiskTier.NOT_APPLICABLE) return

    val bgColor = riskTierColor(riskTier)
    val textColor = if (riskTier == RiskTier.BELOW_BENCHMARK) Color.Black else Color.White

    val description = "Risk level: ${riskTier.display}"
    Text(
        text = riskTier.display,
        style = MaterialTheme.typography.labelMedium,
        color = textColor,
        modifier = modifier
            .semantics { contentDescription = description }
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
