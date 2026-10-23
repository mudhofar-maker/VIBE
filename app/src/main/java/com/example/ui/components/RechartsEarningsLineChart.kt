package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyEarningsPoint
import com.example.data.model.ThirtyDayEarningsTrend
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokYellow

/**
 * Recharts-inspired interactive 30-Day Line Chart for Creator Earnings.
 * Recreates Recharts <ResponsiveContainer><AreaChart><Area /><Line type="monotone" /><Tooltip /></AreaChart></ResponsiveContainer>
 * using native Jetpack Compose Canvas with smooth cubic bezier interpolation, gradient fill,
 * interactive touch scrubber, and floating tooltip.
 */
@Composable
fun RechartsEarningsLineChart(
    trendData: ThirtyDayEarningsTrend,
    modifier: Modifier = Modifier
) {
    val points = trendData.points
    if (points.isEmpty()) return

    // Selected point index for interactive scrubbing (default: peak day or last day)
    var selectedIndex by remember { mutableIntStateOf(trendData.peakEarningsPoint.dayIndex - 1) }
    var isTouching by remember { mutableStateOf(false) }

    val activePoint: DailyEarningsPoint = points.getOrElse(selectedIndex.coerceIn(0, points.size - 1)) { points.last() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurfaceElevated)
            .border(1.2.dp, TikTokCyan.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("recharts_earnings_line_chart_card")
    ) {
        Column {
            // Header with Recharts Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(TikTokCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = TikTokCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Tren Pendapatan 30 Hari",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recharts Style",
                                color = TikTokCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TikTokCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            text = "Sentuh & geser kurva untuk rincian harian",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = trendData.formattedTotal(),
                    color = TikTokYellow,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Stats Metric Bar (30D Total, Avg Daily, Peak Day)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkCard)
                    .border(0.5.dp, DarkBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Rata-rata/Hari", color = TextMuted, fontSize = 10.sp)
                    Text(trendData.formattedAverage(), color = TikTokCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Puncak Tertinggi", color = TextMuted, fontSize = 10.sp)
                    Text(
                        trendData.peakEarningsPoint.formattedTotal(),
                        color = TikTokYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Hari Puncak", color = TextMuted, fontSize = 10.sp)
                    Text(trendData.peakEarningsPoint.dateLabel, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Scrubber Floating Tooltip (Recharts <Tooltip />)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F1522))
                    .border(1.dp, TikTokCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("recharts_tooltip_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = activePoint.fullDate,
                            color = TikTokCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📱 Sawer DANA: ${activePoint.formattedSawer()}", color = TextMuted, fontSize = 10.sp)
                            Text("💎 Fans: ${activePoint.formattedRoyalty()}", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Pendapatan Harian", color = TextMuted, fontSize = 9.sp)
                        Text(
                            text = activePoint.formattedTotal(),
                            color = TikTokYellow,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- CANVAS: LINE & AREA CHART WITH MONOTONE CUBIC BEZIER ---
            val maxEarnings = points.maxOf { it.totalEarningsRupiah } * 1.15
            val minEarnings = (points.minOf { it.totalEarningsRupiah } * 0.85).coerceAtLeast(0.0)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .pointerInput(points) {
                        detectTapGestures(
                            onPress = { offset ->
                                isTouching = true
                                val stepX = size.width / (points.size - 1)
                                val idx = (offset.x / stepX).toInt().coerceIn(0, points.size - 1)
                                selectedIndex = idx
                                tryAwaitRelease()
                                isTouching = false
                            }
                        )
                    }
                    .pointerInput(points) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isTouching = true
                                val stepX = size.width / (points.size - 1)
                                val idx = (offset.x / stepX).toInt().coerceIn(0, points.size - 1)
                                selectedIndex = idx
                            },
                            onDragEnd = { isTouching = false },
                            onDragCancel = { isTouching = false },
                            onDrag = { change, _ ->
                                change.consume()
                                val stepX = size.width / (points.size - 1)
                                val idx = (change.position.x / stepX).toInt().coerceIn(0, points.size - 1)
                                selectedIndex = idx
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val range = (maxEarnings - minEarnings).toFloat().coerceAtLeast(1f)

                    // 1. Draw Horizontal Reference Grid Lines (Recharts <CartesianGrid strokeDasharray="3 3" />)
                    val gridLinesCount = 4
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

                    for (i in 0..gridLinesCount) {
                        val y = height * (i.toFloat() / gridLinesCount)
                        drawLine(
                            color = DarkBorder.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f,
                            pathEffect = dashEffect
                        )
                    }

                    // Compute pixel coordinates for each of the 30 points
                    val stepX = width / (points.size - 1)
                    val coords = points.mapIndexed { index, point ->
                        val x = index * stepX
                        val normalized = ((point.totalEarningsRupiah - minEarnings) / range).toFloat()
                        val y = height - (normalized * (height * 0.82f)) - (height * 0.08f)
                        Offset(x, y)
                    }

                    // Secondary Sawer Line coords (for dual series)
                    val sawerCoords = points.mapIndexed { index, point ->
                        val x = index * stepX
                        val normalized = ((point.sawerDanaRupiah - minEarnings) / range).toFloat()
                        val y = height - (normalized * (height * 0.82f)) - (height * 0.08f)
                        Offset(x, y)
                    }

                    // 2. Build Monotone Cubic Bezier Smooth Path for Primary Series (Total Earnings)
                    val linePath = Path()
                    val areaPath = Path()

                    if (coords.isNotEmpty()) {
                        linePath.moveTo(coords.first().x, coords.first().y)
                        areaPath.moveTo(coords.first().x, height)
                        areaPath.lineTo(coords.first().x, coords.first().y)

                        for (i in 0 until coords.size - 1) {
                            val p0 = coords[i]
                            val p1 = coords[i + 1]
                            val controlX = (p0.x + p1.x) / 2f
                            linePath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                            areaPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                        }

                        areaPath.lineTo(coords.last().x, height)
                        areaPath.close()

                        // Draw Shaded Gradient Fill Under Curve (Recharts <Area />)
                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    TikTokCyan.copy(alpha = 0.30f),
                                    TikTokCyan.copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = height
                            )
                        )

                        // Draw Smooth Line Curve (Recharts <Line type="monotone" stroke="#00E5FF" strokeWidth={2.5} />)
                        drawPath(
                            path = linePath,
                            color = TikTokCyan,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // 3. Draw Secondary Sawer Line (Yellow dashed curve)
                        val sawerPath = Path()
                        sawerPath.moveTo(sawerCoords.first().x, sawerCoords.first().y)
                        for (i in 0 until sawerCoords.size - 1) {
                            val p0 = sawerCoords[i]
                            val p1 = sawerCoords[i + 1]
                            val controlX = (p0.x + p1.x) / 2f
                            sawerPath.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                        }
                        drawPath(
                            path = sawerPath,
                            color = TikTokYellow.copy(alpha = 0.75f),
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                        )

                        // 4. Draw Active Scrubber Vertical Reference Line & Glowing Indicator Dot
                        val activeCoord = coords[selectedIndex.coerceIn(0, coords.size - 1)]
                        drawLine(
                            color = TikTokCyan.copy(alpha = 0.8f),
                            start = Offset(activeCoord.x, 0f),
                            end = Offset(activeCoord.x, height),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                        )

                        // Outer Glowing Halo
                        drawCircle(
                            color = TikTokCyan.copy(alpha = 0.35f),
                            radius = 10.dp.toPx(),
                            center = activeCoord
                        )
                        // Middle Solid Circle
                        drawCircle(
                            color = TikTokCyan,
                            radius = 5.dp.toPx(),
                            center = activeCoord
                        )
                        // Inner White Pip
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = activeCoord
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // X-Axis Timeline Markers (Day 1, 5, 10, 15, 20, 25, 30)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(1, 6, 12, 18, 24, 30).forEach { dayNum ->
                    val isDayActive = activePoint.dayIndex == dayNum
                    Text(
                        text = "$dayNum Okt",
                        color = if (isDayActive) TikTokCyan else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isDayActive) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Recharts-Style Legend Row (<Legend />)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(TikTokCyan)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Total Pendapatan", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(TikTokYellow)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Sawer DANA P2P", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Interaktif", color = TextMuted, fontSize = 10.sp)
                }
            }
        }
    }
}
