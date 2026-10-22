package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokYellow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Model data representasi Filter Warna (Color Grading)
 */
data class VideoFilterItem(
    val id: String,
    val name: String,
    val category: String,
    val previewGradient: List<Color>,
    val description: String,
    val overlayColor: (intensity: Float) -> Color
)

/**
 * Model data representasi Efek Visual & Animasi (Video Effects)
 */
data class VideoEffectItem(
    val id: String,
    val name: String,
    val category: String,
    val badge: String? = null,
    val icon: ImageVector,
    val description: String
)

object VideoEffectsRegistry {
    val filters = listOf(
        VideoFilterItem(
            id = "normal",
            name = "Normal",
            category = "Asli",
            previewGradient = listOf(Color(0xFF888888), Color(0xFF444444)),
            description = "Warna asli tanpa manipulasi warna",
            overlayColor = { Color.Transparent }
        ),
        VideoFilterItem(
            id = "vibrant",
            name = "Vibrant",
            category = "Populer",
            previewGradient = listOf(Color(0xFFFF007F), Color(0xFFFF5E3A)),
            description = "Warna cerah, hangat, dan sinematik memikat",
            overlayColor = { intensity -> Color(0xFFFF007F).copy(alpha = 0.35f * intensity) }
        ),
        VideoFilterItem(
            id = "cyberpunk",
            name = "Cyberpunk",
            category = "Estetik",
            previewGradient = listOf(Color(0xFF00F2FE), Color(0xFF4FACFE)),
            description = "Nuansa neon sci-fi electric cyan & magenta",
            overlayColor = { intensity -> Color(0xFF00F2FE).copy(alpha = 0.35f * intensity) }
        ),
        VideoFilterItem(
            id = "vintage",
            name = "Vintage 90s",
            category = "Retro",
            previewGradient = listOf(Color(0xFFFF9900), Color(0xFF8B4513)),
            description = "Karakter film hangat, klasik, dan nostalgia",
            overlayColor = { intensity -> Color(0xFFFFA500).copy(alpha = 0.38f * intensity) }
        ),
        VideoFilterItem(
            id = "noir",
            name = "Noir B&W",
            category = "Sinematik",
            previewGradient = listOf(Color(0xFFFFFFFF), Color(0xFF111111)),
            description = "Monokrom dramatis dengan kontras tinggi",
            overlayColor = { intensity -> Color(0xFF000000).copy(alpha = 0.55f * intensity) }
        ),
        VideoFilterItem(
            id = "sunset",
            name = "Sunset Gold",
            category = "Populer",
            previewGradient = listOf(Color(0xFFFF7A00), Color(0xFFFFD200)),
            description = "Cahaya keemasan golden hour saat matahari terbenam",
            overlayColor = { intensity -> Color(0xFFFF7A00).copy(alpha = 0.36f * intensity) }
        ),
        VideoFilterItem(
            id = "emerald",
            name = "Emerald Cool",
            category = "Estetik",
            previewGradient = listOf(Color(0xFF00E676), Color(0xFF00B0FF)),
            description = "Tone hijau toska segar yang menyejukkan mata",
            overlayColor = { intensity -> Color(0xFF00E676).copy(alpha = 0.30f * intensity) }
        ),
        VideoFilterItem(
            id = "pastel",
            name = "Pastel Dream",
            category = "Estetik",
            previewGradient = listOf(Color(0xFFBA68C8), Color(0xFFF48FB1)),
            description = "Gradasi lembut lavender dan merah muda estetik",
            overlayColor = { intensity -> Color(0xFFBA68C8).copy(alpha = 0.32f * intensity) }
        ),
        VideoFilterItem(
            id = "tokyo",
            name = "Tokyo Night",
            category = "Sinematik",
            previewGradient = listOf(Color(0xFF1A237E), Color(0xFF00E5FF)),
            description = "Biru pekat malam kota metropolitan berlampu neon",
            overlayColor = { intensity -> Color(0xFF1A237E).copy(alpha = 0.40f * intensity) }
        ),
        VideoFilterItem(
            id = "barbie",
            name = "Barbie Pink",
            category = "Populer",
            previewGradient = listOf(Color(0xFFFF1493), Color(0xFFFF80BF)),
            description = "Pink fuchsia ceria, mencolok, dan penuh energi",
            overlayColor = { intensity -> Color(0xFFFF1493).copy(alpha = 0.34f * intensity) }
        )
    )

    val effects = listOf(
        VideoEffectItem(
            id = "none",
            name = "Tanpa Efek",
            category = "Dasar",
            icon = Icons.Default.Block,
            description = "Rekaman murni tanpa efek visual animasi"
        ),
        VideoEffectItem(
            id = "sparkle",
            name = "Sparkle Stars",
            category = "Partikel",
            badge = "HOT",
            icon = Icons.Default.AutoAwesome,
            description = "Taburan bintang berkilau animasi bercahaya indah"
        ),
        VideoEffectItem(
            id = "glitch",
            name = "Glitch VHS",
            category = "Glitch",
            badge = "TREN",
            icon = Icons.Default.Bolt,
            description = "Scanlines tape analog retro dan flicker dinamis"
        ),
        VideoEffectItem(
            id = "rgb_split",
            name = "3D RGB Split",
            category = "Glitch",
            badge = "VIRAL",
            icon = Icons.Default.Flip,
            description = "Pergeseran kroma merah & cyan bergaya 3D anaglyph"
        ),
        VideoEffectItem(
            id = "zoom_beat",
            name = "Beat Pulse",
            category = "Gerakan",
            badge = "POP",
            icon = Icons.Default.GraphicEq,
            description = "Denyut zoom berirama mengikuti ketukan ritme musik"
        ),
        VideoEffectItem(
            id = "film_grain",
            name = "Film Grain",
            category = "Retro",
            badge = "ESTETIK",
            icon = Icons.Default.MovieFilter,
            description = "Tekstur butiran film 16mm dengan pinggiran vignette"
        ),
        VideoEffectItem(
            id = "floating_hearts",
            name = "Love Hearts",
            category = "Partikel",
            badge = "CUTE",
            icon = Icons.Default.Favorite,
            description = "Hati neon bercahaya melayang lembut ke atas"
        ),
        VideoEffectItem(
            id = "disco_party",
            name = "Disco Strobe",
            category = "Pesta",
            badge = "SERU",
            icon = Icons.Default.Lightbulb,
            description = "Pencahayaan pesta berputar warna-warni yang meriah"
        ),
        VideoEffectItem(
            id = "neon_edge",
            name = "Cyber HUD",
            category = "Futuristik",
            badge = "KEREN",
            icon = Icons.Default.CropFree,
            description = "Bingkai grid sci-fi dengan sudut neon berpendar"
        ),
        VideoEffectItem(
            id = "golden_lens",
            name = "Golden Flare",
            category = "Sinematik",
            badge = "PRO",
            icon = Icons.Default.WbSunny,
            description = "Berkas cahaya matahari keemasan dan kilau optik"
        )
    )

    fun getFilterById(id: String): VideoFilterItem {
        return filters.find { it.id.equals(id, ignoreCase = true) } ?: filters.first()
    }

    fun getEffectById(id: String): VideoEffectItem {
        return effects.find { it.id.equals(id, ignoreCase = true) } ?: effects.first()
    }
}

/**
 * Overlay rendering engine untuk merender animasi efek video dan grading filter warna
 */
@Composable
fun VideoEffectOverlay(
    filterId: String,
    filterIntensity: Float = 0.8f,
    effectId: String = "none",
    modifier: Modifier = Modifier
) {
    val filter = remember(filterId) { VideoEffectsRegistry.getFilterById(filterId) }
    val infiniteTransition = rememberInfiniteTransition(label = "videoEffectsTransition")

    // Animasi global untuk efek dinamis
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animProgress"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val discoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "discoRotation"
    )

    val glitchJitter by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glitchJitter"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (effectId == "zoom_beat") Modifier.scale(pulseScale) else Modifier
            )
    ) {
        // 1. Color Grading Filter Overlay
        if (filter.id != "normal") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(filter.overlayColor(filterIntensity))
            )

            // Gradien sekunder untuk gaya estetik cyberpunk & sunset
            if (filter.id == "cyberpunk") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0x3300F2FE),
                                    Color.Transparent,
                                    Color(0x33FF007F)
                                )
                            )
                        )
                )
            } else if (filter.id == "sunset") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x44FFA000),
                                    Color(0x22FF5722),
                                    Color(0x33673AB7)
                                )
                            )
                        )
                )
            }
        }

        // 2. Animated Video Effects (Canvas & Visual Layers)
        when (effectId) {
            "sparkle" -> {
                SparkleParticlesCanvas(progress = animProgress)
            }
            "glitch" -> {
                GlitchScanlinesCanvas(jitter = glitchJitter, progress = animProgress)
            }
            "rgb_split" -> {
                RgbSplitCanvas(progress = animProgress)
            }
            "film_grain" -> {
                FilmGrainVignetteCanvas(progress = animProgress)
            }
            "floating_hearts" -> {
                FloatingHeartsCanvas(progress = animProgress)
            }
            "disco_party" -> {
                DiscoPartyStrobeOverlay(rotation = discoRotation, pulse = pulseScale)
            }
            "neon_edge" -> {
                NeonCyberHudOverlay(pulse = pulseScale)
            }
            "golden_lens" -> {
                GoldenLensFlareOverlay(progress = animProgress)
            }
        }
    }
}

/**
 * Efek Taburan Bintang Berkilau (Sparkle Stars)
 */
@Composable
private fun SparkleParticlesCanvas(progress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val starPositions = listOf(
            Offset(w * 0.22f, h * 0.18f) to 1.1f,
            Offset(w * 0.78f, h * 0.24f) to 1.4f,
            Offset(w * 0.45f, h * 0.38f) to 0.8f,
            Offset(w * 0.15f, h * 0.52f) to 1.3f,
            Offset(w * 0.85f, h * 0.65f) to 1.2f,
            Offset(w * 0.32f, h * 0.76f) to 1.5f,
            Offset(w * 0.70f, h * 0.84f) to 0.9f,
            Offset(w * 0.50f, h * 0.12f) to 1.0f
        )

        starPositions.forEachIndexed { index, (pos, scale) ->
            val phase = (progress + index * 0.13f) % 1f
            val alpha = (sin(phase * 2 * PI).toFloat().coerceIn(0f, 1f))
            val currentScale = scale * (0.4f + 0.6f * alpha)
            val starColor = when (index % 3) {
                0 -> Color(0xFFFFF9C4)
                1 -> Color(0xFF00E5FF)
                else -> Color(0xFFFF4081)
            }.copy(alpha = alpha * 0.9f)

            drawSparkleStar(pos, size = 26f * currentScale, color = starColor)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSparkleStar(
    center: Offset,
    size: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        quadraticTo(center.x, center.y, center.x + size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + size)
        quadraticTo(center.x, center.y, center.x - size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - size)
        close()
    }
    drawPath(path, color)

    // Center bright core
    drawCircle(
        color = Color.White.copy(alpha = color.alpha),
        radius = size * 0.22f,
        center = center
    )
}

/**
 * Efek Glitch VHS Scanlines
 */
@Composable
private fun GlitchScanlinesCanvas(jitter: Float, progress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Scanlines horizontal
        val step = 8f
        var y = 0f
        while (y < h) {
            drawLine(
                color = Color.Black.copy(alpha = 0.22f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 2.5f
            )
            y += step
        }

        // 2. Glitch horizontal bands
        val bandY1 = ((progress * 1.5f) % 1f) * h
        val bandY2 = (((progress + 0.5f) * 1.2f) % 1f) * h

        drawRect(
            color = Color(0x3300F2FE),
            topLeft = Offset(jitter * 2f, bandY1),
            size = Size(w, 24f)
        )
        drawRect(
            color = Color(0x33FF0055),
            topLeft = Offset(-jitter * 2f, bandY2),
            size = Size(w, 18f)
        )
    }
}

/**
 * Efek 3D RGB Split (Chromatic Aberration)
 */
@Composable
private fun RgbSplitCanvas(progress: Float) {
    val offsetShift = (sin(progress * 2 * PI).toFloat() * 12f)

    Box(modifier = Modifier.fillMaxSize()) {
        // Red Shift
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = (6f + offsetShift).coerceAtLeast(0f).dp)
                .background(Color(0x22FF0055))
        )
        // Cyan Shift
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = (6f + offsetShift).coerceAtLeast(0f).dp)
                .background(Color(0x2200F2FE))
        )
    }
}

/**
 * Efek Film Grain & Vignette Retro
 */
@Composable
private fun FilmGrainVignetteCanvas(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x33000000),
                        Color(0x88000000)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Grain speckles
            val seedOffset = (progress * 100).toInt()
            for (i in 0..70) {
                val rx = ((i * 73 + seedOffset * 31) % 100) / 100f * w
                val ry = ((i * 127 + seedOffset * 47) % 100) / 100f * h
                drawCircle(
                    color = Color.White.copy(alpha = 0.25f),
                    radius = if (i % 2 == 0) 1.5f else 2.5f,
                    center = Offset(rx, ry)
                )
            }
        }
    }
}

/**
 * Efek Hati Neon Melayang (Floating Hearts)
 */
@Composable
private fun FloatingHeartsCanvas(progress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val heartColumns = listOf(0.18f, 0.35f, 0.52f, 0.70f, 0.85f)

        heartColumns.forEachIndexed { idx, colRatio ->
            val phase = (progress + idx * 0.22f) % 1f
            val yPos = (1f - phase) * h
            val xWiggle = sin(phase * 4 * PI).toFloat() * 24f
            val xPos = colRatio * w + xWiggle
            val alpha = (sin(phase * PI).toFloat()).coerceIn(0f, 1f)
            val heartSize = 18f + (idx % 3) * 6f

            val heartColor = if (idx % 2 == 0) Color(0xFFFF2A6D) else Color(0xFFFF69B4)
            drawHeart(
                center = Offset(xPos, yPos),
                size = heartSize,
                color = heartColor.copy(alpha = alpha * 0.85f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeart(
    center: Offset,
    size: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y + size * 0.5f)
        cubicTo(
            center.x - size, center.y - size * 0.3f,
            center.x - size * 0.8f, center.y - size,
            center.x, center.y - size * 0.4f
        )
        cubicTo(
            center.x + size * 0.8f, center.y - size,
            center.x + size, center.y - size * 0.3f,
            center.x, center.y + size * 0.5f
        )
        close()
    }
    drawPath(path, color)
}

/**
 * Efek Disco Party Strobe
 */
@Composable
private fun DiscoPartyStrobeOverlay(rotation: Float, pulse: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(pulse)
            .background(
                Brush.sweepGradient(
                    colors = listOf(
                        Color(0x33FF007F),
                        Color(0x2200F2FE),
                        Color(0x33FFD200),
                        Color(0x227B1FA2),
                        Color(0x33FF007F)
                    )
                )
            )
    )
}

/**
 * Efek Cyberpunk HUD Brackets & Neon Borders
 */
@Composable
private fun NeonCyberHudOverlay(pulse: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        val w = size.width
        val h = size.height
        val cornerLen = 36f
        val strokeW = 3f * pulse
        val neonColor = Color(0xFF00F2FE)

        // Top Left Bracket
        drawLine(neonColor, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
        drawLine(neonColor, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

        // Top Right Bracket
        drawLine(neonColor, Offset(w, 0f), Offset(w - cornerLen, 0f), strokeW)
        drawLine(neonColor, Offset(w, 0f), Offset(w, cornerLen), strokeW)

        // Bottom Left Bracket
        drawLine(neonColor, Offset(0f, h), Offset(cornerLen, h), strokeW)
        drawLine(neonColor, Offset(0f, h), Offset(0f, h - cornerLen), strokeW)

        // Bottom Right Bracket
        drawLine(neonColor, Offset(w, h), Offset(w - cornerLen, h), strokeW)
        drawLine(neonColor, Offset(w, h), Offset(w, h - cornerLen), strokeW)

        // Center crosshair
        val cx = w / 2f
        val cy = h / 2f
        drawCircle(neonColor.copy(alpha = 0.5f), radius = 16f, center = Offset(cx, cy), style = Stroke(1.5f))
        drawLine(neonColor.copy(alpha = 0.5f), Offset(cx - 10f, cy), Offset(cx + 10f, cy), 1.5f)
        drawLine(neonColor.copy(alpha = 0.5f), Offset(cx, cy - 10f), Offset(cx, cy + 10f), 1.5f)
    }
}

/**
 * Efek Berkas Cahaya Matahari Keemasan (Golden Flare)
 */
@Composable
private fun GoldenLensFlareOverlay(progress: Float) {
    val flareShift = (sin(progress * 2 * PI).toFloat() * 30f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0x55FFB300),
                        Color(0x22FF6F00),
                        Color.Transparent
                    ),
                    center = Offset(200f + flareShift, 250f)
                )
            )
    )
}

/**
 * Bottom Sheet Studio Efek & Filter Video interaktif lengkap
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EffectsAndFiltersStudioSheet(
    initialTab: Int = 0, // 0 for Efek, 1 for Filter
    selectedEffectId: String,
    selectedFilterId: String,
    filterIntensity: Float,
    onEffectSelected: (VideoEffectItem) -> Unit,
    onFilterSelected: (VideoFilterItem) -> Unit,
    onIntensityChanged: (Float) -> Unit,
    onResetAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var activeTab by remember { mutableIntStateOf(initialTab) }
    var selectedCategory by remember { mutableStateOf("Semua") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 28.dp)
        ) {
            // Header: Judul & Tombol Tutup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TikTokCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Studio Efek & Filter",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp).testTag("close_effects_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup Studio Efek",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs: Efek Video vs Filter Warna
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = DarkSurfaceElevated,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = TikTokCyan,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = {
                        activeTab = 0
                        selectedCategory = "Semua"
                    },
                    modifier = Modifier.testTag("effects_sheet_tab_effects"),
                    text = {
                        Text(
                            text = "✨ Efek Video (${VideoEffectsRegistry.effects.size})",
                            fontSize = 13.sp,
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == 0) TikTokCyan else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = {
                        activeTab = 1
                        selectedCategory = "Semua"
                    },
                    modifier = Modifier.testTag("effects_sheet_tab_filters"),
                    text = {
                        Text(
                            text = "🎨 Filter Warna (${VideoEffectsRegistry.filters.size})",
                            fontSize = 13.sp,
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == 1) TikTokCyan else TextSecondary
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Categories horizontal chip row
            val categories = if (activeTab == 0) {
                listOf("Semua", "Partikel", "Glitch", "Gerakan", "Retro", "Pesta", "Futuristik", "Sinematik")
            } else {
                listOf("Semua", "Populer", "Estetik", "Retro", "Sinematik")
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TikTokCyan.copy(alpha = 0.25f),
                            selectedLabelColor = TikTokCyan,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) TikTokCyan else DarkBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TAB 0: EFEK VIDEO GRID
            if (activeTab == 0) {
                val filteredEffects = remember(selectedCategory) {
                    if (selectedCategory == "Semua") VideoEffectsRegistry.effects
                    else VideoEffectsRegistry.effects.filter { it.category == selectedCategory || it.id == "none" }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredEffects) { effect ->
                        val isSelected = effect.id == selectedEffectId
                        EffectGridItem(
                            effect = effect,
                            isSelected = isSelected,
                            onClick = { onEffectSelected(effect) }
                        )
                    }
                }
            }

            // TAB 1: FILTER WARNA & SLIDER INTENSITAS
            if (activeTab == 1) {
                val filteredFilters = remember(selectedCategory) {
                    if (selectedCategory == "Semua") VideoEffectsRegistry.filters
                    else VideoEffectsRegistry.filters.filter { it.category == selectedCategory || it.id == "normal" }
                }

                // Filter items list
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(filteredFilters) { filter ->
                        val isSelected = filter.id == selectedFilterId
                        FilterSwatchItem(
                            filter = filter,
                            isSelected = isSelected,
                            onClick = { onFilterSelected(filter) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Intensity Slider
                if (selectedFilterId != "normal") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = TikTokCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Intensitas Filter", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Text(
                                text = "${(filterIntensity * 100).toInt()}%",
                                color = TikTokCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = filterIntensity,
                            onValueChange = onIntensityChanged,
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = TikTokCyan,
                                activeTrackColor = TikTokCyan,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.testTag("filter_intensity_slider")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom action row: Reset vs Terapkan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onResetAll,
                    modifier = Modifier.weight(1f).height(46.dp).testTag("reset_effects_button"),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Asli", fontSize = 13.sp)
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1.5f).height(46.dp).testTag("apply_effects_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Terapkan Efek", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EffectGridItem(
    effect: VideoEffectItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(2.dp)
            .testTag("effect_item_${effect.id}")
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) TikTokCyan.copy(alpha = 0.2f) else DarkSurfaceElevated)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) TikTokCyan else DarkBorder,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = effect.icon,
                contentDescription = effect.name,
                tint = if (isSelected) TikTokCyan else Color.White,
                modifier = Modifier.size(28.dp)
            )

            // Badge e.g. HOT, TREN
            if (effect.badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (effect.badge == "HOT") TikTokRed else TikTokCyan)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = effect.badge,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = effect.name,
            color = if (isSelected) TikTokCyan else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
private fun FilterSwatchItem(
    filter: VideoFilterItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
            .testTag("filter_item_${filter.id}")
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(
                    width = if (isSelected) 2.5.dp else 1.dp,
                    color = if (isSelected) TikTokCyan else DarkBorder,
                    shape = CircleShape
                )
                .padding(3.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(filter.previewGradient)),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Terpilih",
                        tint = TikTokCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = filter.name,
            color = if (isSelected) TikTokCyan else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
