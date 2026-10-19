package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TikTokRed
import kotlin.random.Random

data class HeartEffect(
    val id: Long = System.currentTimeMillis() + Random.nextLong(1000),
    val xOffset: Float = (Random.nextFloat() - 0.5f) * 120f,
    val yOffset: Float = (Random.nextFloat() - 0.5f) * 120f,
    val rotation: Float = (Random.nextFloat() - 0.5f) * 45f
)

@Composable
fun HeartBurstOverlay(
    hearts: List<HeartEffect>,
    onComplete: (Long) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        hearts.forEach { heart ->
            SingleHeartBurst(
                heart = heart,
                onFinished = { onComplete(heart.id) }
            )
        }
    }
}

@Composable
private fun SingleHeartBurst(
    heart: HeartEffect,
    onFinished: () -> Unit
) {
    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(1f) }
    val yDisplacement = remember { Animatable(0f) }

    LaunchedEffect(heart.id) {
        // Pop and scale up
        scale.animateTo(
            targetValue = 1.3f,
            animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
        )
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 100)
        )
        // Float upwards and fade out
        yDisplacement.animateTo(
            targetValue = -180f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 250)
        )
        onFinished()
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = heart.xOffset.toInt(),
                    y = (heart.yOffset + yDisplacement.value).toInt()
                )
            }
            .scale(scale.value)
            .alpha(alpha.value)
            .rotate(heart.rotation)
    ) {
        // Outer glow/shadow icon
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = Color(0x66FF0050),
            modifier = Modifier.size(96.dp)
        )
        // Main vibrant pink heart
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Suka",
            tint = TikTokRed,
            modifier = Modifier.size(86.dp)
        )
    }
}
