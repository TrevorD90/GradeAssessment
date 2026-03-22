package com.gradeassessment.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gradeassessment.domain.model.Subject

val AtBenchmarkGreen = Color(0xFF4CAF50)
val BelowBenchmarkYellow = Color(0xFFFFC107)
val WellBelowBenchmarkRed = Color(0xFFF44336)

val MathBlue = Color(0xFF2196F3)
val ReadingPurple = Color(0xFF9C27B0)
val WritingOrange = Color(0xFFFF9800)
val ScienceGreen = Color(0xFF4CAF50)
val ArtsRose = Color(0xFFE91E63)
val WorldHistoryBrown = Color(0xFF795548)
val SocialStudiesTeal = Color(0xFF009688)
val CriticalThinkingIndigo = Color(0xFF3F51B5)
val IqAssessmentGold = Color(0xFFFFB300)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF7B1FA2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1BEE7),
    onSecondaryContainer = Color(0xFF4A148C),
    tertiary = Color(0xFFE65100),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFCC80),
    onTertiaryContainer = Color(0xFFBF360C),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF616161),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF212121),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

// Scaled-up typography for readability, especially for young students
private val AppTypography = Typography(
    displayLarge = TextStyle(fontSize = 57.sp, lineHeight = 64.sp, fontWeight = FontWeight.Normal),
    displayMedium = TextStyle(fontSize = 45.sp, lineHeight = 52.sp, fontWeight = FontWeight.Normal),
    displaySmall = TextStyle(fontSize = 36.sp, lineHeight = 44.sp, fontWeight = FontWeight.Normal),
    headlineLarge = TextStyle(fontSize = 34.sp, lineHeight = 42.sp, fontWeight = FontWeight.Normal),
    headlineMedium = TextStyle(fontSize = 30.sp, lineHeight = 38.sp, fontWeight = FontWeight.Normal),
    headlineSmall = TextStyle(fontSize = 26.sp, lineHeight = 34.sp, fontWeight = FontWeight.Normal),
    titleLarge = TextStyle(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Medium),
    titleMedium = TextStyle(fontSize = 20.sp, lineHeight = 28.sp, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 19.sp, lineHeight = 28.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 17.sp, lineHeight = 26.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = 15.sp, lineHeight = 22.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium)
)

@Composable
fun GradeAssessmentTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}

fun subjectColor(subject: Subject): Color = when (subject) {
    Subject.MATH -> MathBlue
    Subject.READING -> ReadingPurple
    Subject.WRITING -> WritingOrange
    Subject.SCIENCE -> ScienceGreen
    Subject.ARTS -> ArtsRose
    Subject.WORLD_HISTORY -> WorldHistoryBrown
    Subject.SOCIAL_STUDIES -> SocialStudiesTeal
    Subject.CRITICAL_THINKING -> CriticalThinkingIndigo
    Subject.IQ_ASSESSMENT -> IqAssessmentGold
}

fun subjectIcon(subject: Subject): String = when (subject) {
    Subject.MATH -> "calculate"
    Subject.READING -> "menu_book"
    Subject.WRITING -> "edit_note"
    Subject.SCIENCE -> "science"
    Subject.ARTS -> "palette"
    Subject.WORLD_HISTORY -> "public"
    Subject.SOCIAL_STUDIES -> "groups"
    Subject.CRITICAL_THINKING -> "psychology"
    Subject.IQ_ASSESSMENT -> "lightbulb"
}

fun riskTierColor(riskTier: com.gradeassessment.domain.model.RiskTier): Color = when (riskTier) {
    com.gradeassessment.domain.model.RiskTier.AT_ABOVE_BENCHMARK -> AtBenchmarkGreen
    com.gradeassessment.domain.model.RiskTier.BELOW_BENCHMARK -> BelowBenchmarkYellow
    com.gradeassessment.domain.model.RiskTier.WELL_BELOW_BENCHMARK -> WellBelowBenchmarkRed
    com.gradeassessment.domain.model.RiskTier.NOT_APPLICABLE -> Color.Gray
}
