package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun VinylDisc(
    soundCoverUrl: String,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "discRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )

    Box(
        modifier = modifier
            .size(54.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Floating musical notes when playing
        if (isPlaying) {
            FloatingMusicNote(symbol = "♪", startDelay = 0)
            FloatingMusicNote(symbol = "♫", startDelay = 1200)
        }

        // Vinyl outer ring
        Box(
            modifier = Modifier
                .size(48.dp)
                .rotate(if (isPlaying) rotation else 0f)
                .clip(CircleShape)
                .background(Color(0xFF1E1E24))
                .border(1.5.dp, Color(0xFF33333F), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Inner vinyl grooves
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF121216))
                    .border(0.5.dp, Color(0xFF454554), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Center Album Image
                AsyncImage(
                    model = soundCoverUrl,
                    contentDescription = "Cover Suara",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}

@Composable
private fun FloatingMusicNote(symbol: String, startDelay: Long) {
    val yOffset = remember { Animatable(0f) }
    val xOffset = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-10f) }

    LaunchedEffect(Unit) {
        delay(startDelay)
        while (true) {
            yOffset.snapTo(0f)
            xOffset.snapTo(0f)
            alpha.snapTo(1f)
            rotation.snapTo(-15f)

            // Float up and drift left
            launchAnim(yOffset, xOffset, alpha, rotation)
            delay(500)
        }
    }

    Text(
        text = symbol,
        color = Color.White,
        fontSize = 14.sp,
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (-20 + xOffset.value).toInt(),
                    y = (-10 + yOffset.value).toInt()
                )
            }
            .alpha(alpha.value)
            .rotate(rotation.value)
    )
}

private suspend fun launchAnim(
    yOffset: Animatable<Float, *>,
    xOffset: Animatable<Float, *>,
    alpha: Animatable<Float, *>,
    rotation: Animatable<Float, *>
) {
    yOffset.animateTo(
        targetValue = -70f,
        animationSpec = tween(1600, easing = LinearEasing)
    )
    xOffset.animateTo(
        targetValue = -35f,
        animationSpec = tween(1600, easing = LinearEasing)
    )
    alpha.animateTo(
        targetValue = 0f,
        animationSpec = tween(1600, easing = LinearEasing)
    )
    rotation.animateTo(
        targetValue = 25f,
        animationSpec = tween(1600, easing = LinearEasing)
    )
}
