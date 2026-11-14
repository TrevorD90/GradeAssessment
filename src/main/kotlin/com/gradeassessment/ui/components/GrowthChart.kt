package com.gradeassessment.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gradeassessment.domain.model.GradeLevel
import com.gradeassessment.domain.model.GrowthHistory
import com.gradeassessment.domain.model.GrowthPoint
import com.gradeassessment.domain.model.Subject
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/*
 * Growth chart: grade equivalent over time for one student.
 *
 * Design (emphasis form): the chosen series - one subject, or the overall mean - is drawn in the
 * accent blue; the other subjects stay in light gray as context; a dashed line shows the grade
 * expected for the student's age. One accent hue instead of seven competing colors keeps it
 * readable and color-blind safe, and identity never rests on color alone (legend + selector chip
 * + direct label). The table beneath gives the same numbers in text.
 */

// Chart palette (light surface; the app has no dark theme).
private val ChartSurface = Color(0xFFFCFCFB)
private val SeriesBlue = Color(0xFF2A78D6)
private val ContextGray = Color(0xFFC3C2B7)
private val Gridline = Color(0xFFE1E0D9)
private val MutedInk = Color(0xFF898781)
private val SecondaryInk = Color(0xFF52514E)
private val PrimaryInk = Color(0xFF0B0B0B)
private val Hairline = Color(0x1A0B0B0B)

private const val CHART_HEIGHT_DP = 300

/**
 * Plot-area geometry in pixels, shared by the drawing and the hover tooltip.
 * The x axis is real time (not evenly spaced sessions), so the slope of a line is true growth
 * per unit of time: evenly spaced points would exaggerate growth across short gaps.
 */
private class PlotGeometry(
    val left: Float, val top: Float, val right: Float, val bottom: Float,
    val yMin: Double, val yMax: Double,
    /** Each session's position along the time axis, 0..1. */
    private val timeFractions: List<Double>
) {
    fun x(index: Int): Float = (left + timeFractions[index] * (right - left)).toFloat()

    fun y(ge: Double): Float = (bottom - (ge - yMin) / (yMax - yMin) * (bottom - top)).toFloat()

    /** Nearest point index to a pointer x position. */
    fun indexAt(px: Float): Int = timeFractions.indices.minBy { kotlin.math.abs(x(it) - px) }
}

private fun buildGeometry(size: IntSize, density: Float, history: GrowthHistory): PlotGeometry {
    val values = history.points.flatMap { p -> p.subjectGe.values + listOfNotNull(p.overallGe, p.expectedGe) }
    // Whole-grade bounds with a little headroom, kept inside the reportable range (below K .. above 10th).
    var lo = floor(values.minOrNull() ?: 0.0).coerceAtLeast(-1.0)
    var hi = ceil(values.maxOrNull() ?: 1.0).coerceAtMost(11.0)
    if (hi - lo < 3) { hi = (lo + 3).coerceAtMost(11.0); lo = (hi - 3).coerceAtLeast(-1.0) }
    val first = history.points.first().date.toEpochMilliseconds().toDouble()
    val span = (history.points.last().date.toEpochMilliseconds() - first).coerceAtLeast(1.0)
    return PlotGeometry(
        left = 48f * density, top = 14f * density,
        right = size.width - 52f * density, bottom = size.height - 34f * density,
        yMin = lo, yMax = hi,
        timeFractions = history.points.map { (it.date.toEpochMilliseconds() - first) / span }
    )
}

private fun gradeTick(g: Int): String = when {
    g < 0 -> "<K"
    g == 0 -> "K"
    g > 10 -> ">10"
    else -> "Gr $g"
}

private fun shortDate(point: GrowthPoint): String {
    val d = point.date.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${d.monthNumber}/${d.dayOfMonth}/${d.year % 100}"
}

private fun seriesName(series: Subject?): String = series?.displayName ?: "Overall"

@Composable
fun GrowthChartSection(history: GrowthHistory, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Growth Over Time", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        if (!history.hasGrowth) {
            Text(
                text = if (history.points.isEmpty()) {
                    "No assessments on the current scoring scale yet."
                } else {
                    "This is the first assessment on the current scoring scale. " +
                        "Growth will appear here after the next assessment."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ExcludedNote(history)
            return
        }

        // Chosen series: null = overall mean. Kept per dialog; defaults to Overall.
        var selected by remember(history) { mutableStateOf<Subject?>(null) }

        // Headline: change for the chosen series from the first to the latest assessment.
        val change = history.change(selected)
        if (change != null) {
            val sign = if (change >= 0) "+" else ""
            val first = history.points.first { it.valueFor(selected) != null }
            val last = history.points.last { it.valueFor(selected) != null }
            Text(
                text = "${seriesName(selected)}: $sign%.1f grade levels from ${shortDate(first)} to ${shortDate(last)}".format(change),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
        }

        // Series selector (one row above the chart).
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (listOf<Subject?>(null) + history.subjects).forEach { s ->
                FilterChip(
                    selected = selected == s,
                    onClick = { selected = s },
                    label = { Text(seriesName(s)) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        Legend(selected)
        Spacer(Modifier.height(4.dp))

        Surface(
            color = ChartSurface,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Hairline),
            modifier = Modifier.fillMaxWidth()
        ) {
            GrowthPlot(history, selected)
        }

        ExcludedNote(history)
        Spacer(Modifier.height(12.dp))
        GrowthTable(history)
    }
}

@Composable
private fun Legend(selected: Subject?) {
    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        LegendItem(seriesName(selected), SeriesBlue, dashed = false, thick = true)
        LegendItem(if (selected == null) "Each subject" else "Other subjects", ContextGray, dashed = false, thick = false)
        LegendItem("Expected for age", MutedInk, dashed = true, thick = false)
    }
}

@Composable
private fun LegendItem(label: String, color: Color, dashed: Boolean, thick: Boolean) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Canvas(Modifier.size(width = 24.dp, height = 12.dp)) {
            drawLine(
                color = color,
                start = Offset(0f, size.height / 2), end = Offset(size.width, size.height / 2),
                strokeWidth = (if (thick) 2.dp else 1.5.dp).toPx(),
                pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(5f, 4f)) else null,
                cap = StrokeCap.Round
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = SecondaryInk)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun GrowthPlot(history: GrowthHistory, selected: Subject?) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current.density
    var size by remember { mutableStateOf(IntSize.Zero) }
    var hoverIndex by remember(history) { mutableStateOf<Int?>(null) }
    val points = history.points

    val description = buildString {
        append("Growth chart for ${seriesName(selected)}: ")
        points.forEach { p ->
            p.valueFor(selected)?.let { append("${shortDate(p)} grade equivalent %.1f; ".format(it)) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CHART_HEIGHT_DP.dp)
            .onSizeChanged { size = it }
            .semantics { contentDescription = description }
            .onPointerEvent(PointerEventType.Move) { event ->
                if (size != IntSize.Zero) {
                    val geo = buildGeometry(size, density, history)
                    val px = event.changes.first().position.x
                    // Hit target is the whole plot column around each point, not just the marker.
                    hoverIndex = if (px in (geo.left - 24 * density)..(geo.right + 24 * density)) geo.indexAt(px) else null
                }
            }
            .onPointerEvent(PointerEventType.Exit) { hoverIndex = null }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            if (size == IntSize.Zero) return@Canvas
            val geo = buildGeometry(size, density, history)
            drawGrid(geo, textMeasurer)
            drawDateLabels(geo, points, textMeasurer)

            // Expected-for-age reference (dashed, behind the data). Drawn as steps: ages are whole
            // years, so the expectation only changes when the recorded age changes; a sloped line
            // would imply a gradual change the data does not contain.
            drawExpectedSteps(geo, points, PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx())))

            // Context: every subject other than the chosen one, in gray.
            history.subjects.filter { it != selected }.forEach { s ->
                drawSeries(geo, points.map { it.subjectGe[s] }, ContextGray, 1.5.dp.toPx(), null)
            }

            // Crosshair under the emphasized line.
            hoverIndex?.let { i ->
                drawLine(MutedInk.copy(alpha = 0.6f), Offset(geo.x(i), geo.top), Offset(geo.x(i), geo.bottom), 1.dp.toPx())
            }

            // Emphasized series: 2px line, 8px markers with a 2px surface ring.
            val values = points.map { it.valueFor(selected) }
            drawSeries(geo, values, SeriesBlue, 2.dp.toPx(), null)
            values.forEachIndexed { i, v ->
                if (v != null) {
                    val c = Offset(geo.x(i), geo.y(v))
                    val r = if (hoverIndex == i) 5.dp.toPx() else 4.dp.toPx()
                    drawCircle(ChartSurface, radius = r + 2.dp.toPx(), center = c)
                    drawCircle(SeriesBlue, radius = r, center = c)
                }
            }

            // Direct label on the latest value only (not on every point).
            values.indices.lastOrNull { values[it] != null }?.let { i ->
                val v = values[i]!!
                val label = textMeasurer.measure("%.1f".format(v), TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryInk))
                drawText(label, topLeft = Offset(geo.x(i) + 8.dp.toPx(), geo.y(v) - label.size.height / 2f))
            }
        }

        // Tooltip for the hovered assessment.
        hoverIndex?.let { i ->
            if (size != IntSize.Zero) {
                val geo = buildGeometry(size, density, history)
                val tooltipWidthPx = 230 * density
                val x = geo.x(i)
                val left = if (x + 12 * density + tooltipWidthPx < size.width) x + 12 * density else x - 12 * density - tooltipWidthPx
                Tooltip(
                    point = points[i],
                    previous = points.getOrNull(i - 1),
                    selected = selected,
                    modifier = Modifier.offset { IntOffset(left.roundToInt(), (geo.top).roundToInt()) }.width(230.dp)
                )
            }
        }
    }
}

@Composable
private fun Tooltip(point: GrowthPoint, previous: GrowthPoint?, selected: Subject?, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, Hairline)
    ) {
        Column(Modifier.padding(10.dp)) {
            Text("${shortDate(point)}  (age ${point.age})", style = MaterialTheme.typography.bodySmall, color = SecondaryInk)
            val v = point.valueFor(selected)
            if (v != null) {
                Text(
                    "${seriesName(selected)}: GE %.1f".format(v),
                    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = PrimaryInk
                )
                Text(GradeLevel.fromGradeEquivalent(v).display, style = MaterialTheme.typography.bodySmall, color = SecondaryInk)
                val prev = previous?.valueFor(selected)
                if (prev != null) {
                    val d = v - prev
                    Text(
                        "${if (d >= 0) "+" else ""}%.1f since previous".format(d),
                        style = MaterialTheme.typography.bodySmall, color = SecondaryInk
                    )
                }
            } else {
                Text("${seriesName(selected)} not taken", style = MaterialTheme.typography.bodyMedium, color = SecondaryInk)
            }
            Text("Expected for age: GE %.1f".format(point.expectedGe), style = MaterialTheme.typography.bodySmall, color = SecondaryInk)
        }
    }
}

private fun DrawScope.drawGrid(geo: PlotGeometry, textMeasurer: androidx.compose.ui.text.TextMeasurer) {
    val step = if (geo.yMax - geo.yMin > 8) 2 else 1
    val style = TextStyle(fontSize = 12.sp, color = MutedInk)
    var g = ceil(geo.yMin).toInt()
    while (g <= geo.yMax) {
        if (g % step == 0) {
            val y = geo.y(g.toDouble())
            drawLine(Gridline, Offset(geo.left, y), Offset(geo.right, y), 1f)
            val label = textMeasurer.measure(gradeTick(g), style)
            drawText(label, topLeft = Offset(geo.left - label.size.width - 8.dp.toPx(), y - label.size.height / 2f))
        }
        g++
    }
}

private fun DrawScope.drawDateLabels(geo: PlotGeometry, points: List<GrowthPoint>, textMeasurer: androidx.compose.ui.text.TextMeasurer) {
    val style = TextStyle(fontSize = 12.sp, color = MutedInk)
    val gap = 8.dp.toPx()
    // Sessions can be close together in time, so skip any label that would overlap the one before.
    // The latest session is always labelled; an earlier label that would collide with it is dropped.
    val measured = points.map { textMeasurer.measure(shortDate(it), style) }
    fun left(i: Int) = geo.x(i) - measured[i].size.width / 2f
    fun right(i: Int) = geo.x(i) + measured[i].size.width / 2f
    val last = points.lastIndex
    var lastDrawnRight = Float.NEGATIVE_INFINITY
    for (i in points.indices) {
        val clearOfPrevious = left(i) >= lastDrawnRight + gap
        val clearOfLast = i == last || right(i) + gap <= left(last)
        if (i == last || (clearOfPrevious && clearOfLast)) {
            drawText(measured[i], topLeft = Offset(left(i), geo.bottom + gap))
            lastDrawnRight = right(i)
        }
    }
}

private fun DrawScope.drawExpectedSteps(geo: PlotGeometry, points: List<GrowthPoint>, effect: PathEffect) {
    val path = Path()
    points.forEachIndexed { i, p ->
        val x = geo.x(i)
        val y = geo.y(p.expectedGe)
        if (i == 0) path.moveTo(x, y) else {
            path.lineTo(x, geo.y(points[i - 1].expectedGe)) // hold the previous level...
            path.lineTo(x, y)                               // ...then step at this session
        }
    }
    drawPath(path, MutedInk, style = Stroke(width = 1.5.dp.toPx(), pathEffect = effect))
}

/** Draws a polyline through the non-null values; a missing session breaks the line. */
private fun DrawScope.drawSeries(geo: PlotGeometry, values: List<Double?>, color: Color, width: Float, effect: PathEffect?) {
    val path = Path()
    var penDown = false
    var segmentPoints = 0
    values.forEachIndexed { i, v ->
        if (v == null) { penDown = false; return@forEachIndexed }
        val x = geo.x(i)
        val y = geo.y(v)
        if (penDown) path.lineTo(x, y) else path.moveTo(x, y)
        penDown = true
        segmentPoints++
    }
    if (segmentPoints > 0) {
        drawPath(path, color, style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = effect))
    }
}

@Composable
private fun ExcludedNote(history: GrowthHistory) {
    if (history.excludedSessions > 0) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${history.excludedSessions} earlier assessment(s) used an older scoring scale and are not shown, " +
                "because their grade levels are not comparable.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** The same data as the chart, in text: one row per assessment. */
@Composable
private fun GrowthTable(history: GrowthHistory) {
    val subjects = history.subjects
    val headerStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = SecondaryInk)
    val cellStyle = MaterialTheme.typography.bodySmall.copy(color = PrimaryInk)

    @Composable
    fun RowScope.cell(text: String, style: TextStyle, weight: Float = 1f) =
        Text(text, style = style, modifier = Modifier.weight(weight))

    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            cell("Date", headerStyle, 1.2f)
            cell("Age", headerStyle, 0.6f)
            cell("Expected", headerStyle)
            cell("Overall", headerStyle)
            subjects.forEach { cell(it.displayName, headerStyle) }
        }
        HorizontalDivider(color = Gridline)
        history.points.forEach { p ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                cell(shortDate(p), cellStyle, 1.2f)
                cell("${p.age}", cellStyle, 0.6f)
                cell("%.1f".format(p.expectedGe), cellStyle)
                cell(p.overallGe?.let { "%.1f".format(it) } ?: "-", cellStyle)
                subjects.forEach { s -> cell(p.subjectGe[s]?.let { "%.1f".format(it) } ?: "-", cellStyle) }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Values are grade equivalents (GE): 3.4 means working like a typical student four-tenths of the way through grade 3. " +
                "\"-\" means the subject was not taken.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
