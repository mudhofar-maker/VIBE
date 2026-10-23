package com.example.ui.components

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import java.util.Locale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.ui.theme.TikTokCyan

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerView(
    videoUrl: String,
    thumbnailUrl: String,
    isCurrentPage: Boolean,
    onDoubleTapLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(false) }
    var showPauseOverlay by remember { mutableStateOf(false) }
    val heartBursts = remember { mutableStateListOf<HeartEffect>() }

    // Pinch-to-zoom (Cubit layar) & pan gesture state
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(isCurrentPage) {
        if (!isCurrentPage) {
            zoomScale = 1f
            panOffset = Offset.Zero
        }
    }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (zoomScale * zoomChange).coerceIn(1f, 4.5f)
        zoomScale = newScale
        if (newScale > 1f) {
            val maxPanX = if (containerSize.width > 0) (containerSize.width * (newScale - 1f)) / 2f else 500f
            val maxPanY = if (containerSize.height > 0) (containerSize.height * (newScale - 1f)) / 2f else 800f
            panOffset = Offset(
                x = (panOffset.x + panChange.x).coerceIn(-maxPanX, maxPanX),
                y = (panOffset.y + panChange.y).coerceIn(-maxPanY, maxPanY)
            )
        } else {
            panOffset = Offset.Zero
        }
    }

    val fallbackVideoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4"

    // Only instantiate ExoPlayer when this specific page is active (prevents decoder/RAM exhaustion)
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }

    DisposableEffect(isCurrentPage, videoUrl) {
        if (isCurrentPage) {
            val player = try {
                val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                    .setUserAgent("Mozilla/5.0 (Linux; Android 14; Mobile) MyFlix/1.0")
                    .setAllowCrossProtocolRedirects(true)
                    .setConnectTimeoutMs(8000)
                    .setReadTimeoutMs(8000)

                val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
                val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

                ExoPlayer.Builder(context)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .build().apply {
                        val mediaItem = MediaItem.fromUri(videoUrl)
                        setMediaItem(mediaItem)
                        repeatMode = Player.REPEAT_MODE_ONE
                        playWhenReady = true
                        prepare()
                    }
            } catch (_: Throwable) {
                null
            }

            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    isBuffering = playbackState == Player.STATE_BUFFERING
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }

                override fun onPlayerError(error: PlaybackException) {
                    try {
                        val currentUri = player?.currentMediaItem?.localConfiguration?.uri?.toString()
                        if (currentUri != fallbackVideoUrl) {
                            player?.setMediaItem(MediaItem.fromUri(fallbackVideoUrl))
                            player?.prepare()
                            player?.play()
                        }
                    } catch (_: Throwable) {}
                }
            }
            player?.addListener(listener)
            exoPlayer = player

            onDispose {
                try {
                    player?.removeListener(listener)
                    player?.stop()
                    player?.release()
                } catch (_: Throwable) {}
                exoPlayer = null
            }
        } else {
            try {
                exoPlayer?.stop()
                exoPlayer?.release()
            } catch (_: Throwable) {}
            exoPlayer = null
            onDispose {}
        }
    }

    // App lifecycle awareness: pause when app goes to background, resume when app returns
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    try {
                        exoPlayer?.pause()
                    } catch (_: Throwable) {}
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (isCurrentPage && isPlaying) {
                        try {
                            exoPlayer?.play()
                        } catch (_: Throwable) {}
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onSizeChanged { containerSize = it }
            .transformable(state = transformableState)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (zoomScale > 1.05f) {
                            zoomScale = 1f
                            panOffset = Offset.Zero
                        } else {
                            heartBursts.add(HeartEffect())
                            onDoubleTapLike()
                        }
                    },
                    onTap = {
                        val player = exoPlayer
                        if (player != null) {
                            if (player.isPlaying) {
                                player.pause()
                                showPauseOverlay = true
                            } else {
                                player.play()
                                showPauseOverlay = false
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Pinch-zoomable video canvas layer (Cubit Layar)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                    translationX = panOffset.x
                    translationY = panOffset.y
                },
            contentAlignment = Alignment.Center
        ) {
            // Thumbnail poster backdrop: always visible immediately, zero latency
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // PlayerView: only renders when player is active on the current page
            if (isCurrentPage && exoPlayer != null) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            player = exoPlayer
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { view ->
                        view.player = exoPlayer
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Floating Pinch Zoom Indicator Pill
        AnimatedVisibility(
            visible = zoomScale > 1.05f,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 58.dp, start = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xDD0A0A0C))
                    .border(1.dp, TikTokCyan.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = null,
                    tint = TikTokCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${String.format(Locale.US, "%.1f", zoomScale)}x",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))

                // Emulator / Click Zoom In (+)
                Text(
                    text = "+",
                    color = if (zoomScale < 4.5f) TikTokCyan else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clickable(enabled = zoomScale < 4.5f) {
                            zoomScale = (zoomScale + 0.5f).coerceAtMost(4.5f)
                        }
                        .padding(horizontal = 6.dp)
                )

                // Emulator / Click Zoom Out (-)
                Text(
                    text = "−",
                    color = if (zoomScale > 1f) TikTokCyan else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clickable(enabled = zoomScale > 1f) {
                            val s = (zoomScale - 0.5f).coerceAtLeast(1f)
                            zoomScale = s
                            if (s == 1f) panOffset = Offset.Zero
                        }
                        .padding(horizontal = 6.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Reset",
                    color = TikTokRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            zoomScale = 1f
                            panOffset = Offset.Zero
                        }
                        .padding(horizontal = 4.dp)
                )
            }
        }

        // Loading spinner during initial buffering
        if (isBuffering && isCurrentPage) {
            CircularProgressIndicator(
                color = TikTokCyan,
                strokeWidth = 3.dp,
                modifier = Modifier.size(42.dp)
            )
        }

        // Animated Big Pause/Play overlay icon
        AnimatedVisibility(
            visible = showPauseOverlay,
            enter = fadeIn() + scaleIn(initialScale = 1.4f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(Color(0x77000000), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Jeda",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Floating Hearts when double tapped
        HeartBurstOverlay(
            hearts = heartBursts,
            onComplete = { id -> heartBursts.removeAll { it.id == id } }
        )
    }
}
