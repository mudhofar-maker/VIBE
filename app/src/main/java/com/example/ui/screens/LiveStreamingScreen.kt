package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

data class LiveChatMessage(
    val id: String = "msg_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
    val username: String,
    val text: String,
    val badge: String? = null,
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
    val isSystemAlert: Boolean = false,
    val highlightColor: Color? = null
)

data class LiveGiftBanner(
    val id: String,
    val senderName: String,
    val avatarUrl: String,
    val giftEmoji: String,
    val giftTitle: String,
    val nominalFormatted: String,
    val isDanaSawer: Boolean = false
)

@Composable
fun LiveStreamingScreen(
    onEndLive: () -> Unit,
    onSawerReceived: (Double) -> Unit = {},
    onCoinReceived: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isFrontCamera by remember { mutableStateOf(true) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isBeautyFilterOn by remember { mutableStateOf(true) }

    var liveDurationSeconds by remember { mutableIntStateOf(0) }
    var liveViewerCount by remember { mutableIntStateOf(1420) }
    var liveCoinsEarned by remember { mutableLongStateOf(250L) }
    var liveDanaEarned by remember { mutableStateOf(75_000.0) }

    var showEndSummaryDialog by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var activeGiftBanner by remember { mutableStateOf<LiveGiftBanner?>(null) }

    val chatMessages = remember {
        mutableStateListOf(
            LiveChatMessage(username = "Sistem VibeStream", text = "Siaran langsung Anda telah tayang. Patuhi panduan komunitas!", isSystemAlert = true),
            LiveChatMessage(username = "Bella Poarch", text = "Halo kak! Semangat live streaming-nya! 🔥❤️", badge = "Top Fan"),
            LiveChatMessage(username = "Fahri Danz", text = "Kualitas videonya jernih banget kak!", badge = "Sultan"),
            LiveChatMessage(username = "Chef Rendy", text = "Salam dari dapur nusantara chef! 🍳✨"),
            LiveChatMessage(username = "Rina Cantika", text = "Request sapaan untuk kota Bandung dong kak 🌸")
        )
    }

    val chatListState = rememberLazyListState()

    // Handle back button press
    BackHandler {
        showEndSummaryDialog = true
    }

    // Live clock timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            liveDurationSeconds += 1
            // Random fluctuation in viewers
            val delta = Random.nextInt(-15, 25)
            liveViewerCount = (liveViewerCount + delta).coerceIn(800, 4800)
        }
    }

    // Auto simulated incoming chat comments every 4 seconds
    LaunchedEffect(Unit) {
        val sampleUsers = listOf(
            Pair("Dimas Gaming", "Keren banget bang gameplay-nya! 🔥"),
            Pair("Maya Foodie", "Nonton sambil makan siang seru nih 🍜"),
            Pair("Budi Santoso", "Izin gabung kak, salam kenal semuanya!"),
            Pair("Siti Dance", "Gerakannya luwes banget kak, idola! 💃"),
            Pair("Andi Lawak", "Wkwkwk kocak parah ceritanya kak 😂"),
            Pair("Dewi Lestari", "Semoga berkah selalu kak, lancar live-nya! ✨"),
            Pair("Gita Gamer", "Auto follow & share ke temen-temen! 🎮")
        )

        while (true) {
            delay(4500)
            val pick = sampleUsers.random()
            chatMessages.add(
                LiveChatMessage(
                    username = pick.first,
                    text = pick.second,
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200"
                )
            )
            if (chatMessages.size > 50) chatMessages.removeAt(0)
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Live Camera Viewfinder Background Simulator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isBeautyFilterOn) {
                            listOf(Color(0xFF261C2C), Color(0xFF13111C), Color(0xFF1C1322))
                        } else {
                            listOf(Color(0xFF181822), Color(0xFF0F0F14), Color(0xFF14141C))
                        }
                    )
                )
        )

        // Beauty Glow Filter Overlay Effect
        if (isBeautyFilterOn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0x22FE2C55),
                                Color(0x1100E5FF),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Top Status Header Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Creator live avatar & Live badge pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x990A0A0E))
                        .border(1.dp, TikTokCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, TikTokRed, CircleShape)
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Blinking Red Dot
                            val infiniteTransition = rememberInfiniteTransition(label = "liveBlink")
                            val blinkAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 1.0f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(700, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "blinkAlpha"
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .scale(blinkAlpha)
                                    .clip(CircleShape)
                                    .background(LiveRed)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "LIVE",
                                color = LiveRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val mins = liveDurationSeconds / 60
                            val secs = liveDurationSeconds % 60
                            Text(
                                text = String.format(Locale.US, "%02d:%02d", mins, secs),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "@saya (Siaran Langsung)",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Viewers Counter & End Live Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Viewers Counter Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x88000000))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TikTokCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = String.format(Locale.US, "%,d", liveViewerCount),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Close/End Live Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .clickable { showEndSummaryDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Akhiri Live",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Real-time Earnings Tally Ribbon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xAA0A0D14))
                    .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // VibeCoins Tally
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪙", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$liveCoinsEarned Koin",
                        color = TikTokYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " (Rp ${String.format(Locale.US, "%,.0f", liveCoinsEarned * 8000.0)})",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                // DANA Saweran Tally
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💸", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "DANA: Rp ${String.format(Locale.US, "%,.0f", liveDanaEarned)}",
                        color = TikTokCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Right-Side Live Camera & Audio Tools
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flip Camera
            LiveToolIcon(
                icon = Icons.Default.Cameraswitch,
                label = if (isFrontCamera) "Depan" else "Belakang",
                isActive = isFrontCamera,
                onClick = {
                    isFrontCamera = !isFrontCamera
                    Toast.makeText(context, if (isFrontCamera) "Kamera Depan Aktif" else "Kamera Belakang Aktif", Toast.LENGTH_SHORT).show()
                }
            )

            // Flash
            LiveToolIcon(
                icon = Icons.Default.FlashOn,
                label = if (isFlashOn) "Flash On" else "Flash",
                isActive = isFlashOn,
                onClick = { isFlashOn = !isFlashOn }
            )

            // Mic Mute
            LiveToolIcon(
                icon = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                label = if (isMicMuted) "Mute" else "Mic On",
                isActive = !isMicMuted,
                onClick = {
                    isMicMuted = !isMicMuted
                    Toast.makeText(context, if (isMicMuted) "Mikrofon Dimatikan" else "Mikrofon Menyala", Toast.LENGTH_SHORT).show()
                }
            )

            // Beauty Filter
            LiveToolIcon(
                icon = Icons.Default.AutoAwesome,
                label = "Glow",
                isActive = isBeautyFilterOn,
                onClick = { isBeautyFilterOn = !isBeautyFilterOn }
            )

            // Simulate Gift / Sawer Button
            LiveToolIcon(
                icon = Icons.Default.CardGiftcard,
                label = "Tes Gift",
                isActive = true,
                badgeColor = TikTokRed,
                onClick = {
                    val sampleGifts = listOf(
                        LiveGiftBanner("g1", "Sultan Kemang", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200", "🦁", "Singa Emas", "+500 Koin (Rp 4.000.000)"),
                        LiveGiftBanner("g2", "Rania Cantika", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200", "🚀", "Roket Galaksi", "+250 Koin (Rp 2.000.000)"),
                        LiveGiftBanner("g3", "Bima Sakti", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200", "💸", "Sawer DANA P2P", "Rp 50.000 (0% Admin)", isDanaSawer = true),
                        LiveGiftBanner("g4", "Clarissa Maya", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200", "🌹", "Mawar Cinta", "+20 Koin (Rp 160.000)")
                    )
                    val gift = sampleGifts.random()
                    activeGiftBanner = gift
                    if (gift.isDanaSawer) {
                        liveDanaEarned += 50_000.0
                        onSawerReceived(50_000.0)
                    } else {
                        val coins = if (gift.giftEmoji == "🦁") 500L else if (gift.giftEmoji == "🚀") 250L else 20L
                        liveCoinsEarned += coins
                        onCoinReceived(coins)
                    }
                    chatMessages.add(
                        LiveChatMessage(
                            username = gift.senderName,
                            text = "Mengirim ${gift.giftEmoji} ${gift.giftTitle}! ${gift.nominalFormatted}",
                            isSystemAlert = true,
                            highlightColor = TikTokYellow
                        )
                    )
                }
            )
        }

        // Live Floating Gift / Sawer Banner
        AnimatedVisibility(
            visible = activeGiftBanner != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 110.dp, start = 20.dp, end = 20.dp)
        ) {
            activeGiftBanner?.let { gift ->
                LaunchedEffect(gift.id) {
                    delay(3800)
                    activeGiftBanner = null
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = if (gift.isDanaSawer) {
                                    listOf(Color(0xFF0D47A1), Color(0xFF118EEA))
                                } else {
                                    listOf(Color(0xFFE91E63), Color(0xFFFF9800))
                                }
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    ) {
                        AsyncImage(
                            model = gift.avatarUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${gift.senderName} mengirim ${gift.giftEmoji} ${gift.giftTitle}!",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = gift.nominalFormatted,
                            color = TikTokYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bottom Section: Live Chat Stream & Creator Interaction Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Live Chat Feed (Vertical scrollable box)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(200.dp)
            ) {
                LazyColumn(
                    state = chatListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(chatMessages, key = { it.id }) { msg ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x770A0A0E))
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            if (msg.badge != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (msg.badge == "Sultan") TikTokYellow else TikTokCyan)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = msg.badge,
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Text(
                                text = "${msg.username}: ",
                                color = msg.highlightColor ?: if (msg.isSystemAlert) TikTokCyan else Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = msg.text,
                                color = if (msg.isSystemAlert) TikTokYellow else Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (msg.isSystemAlert) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Creator Chat Reply Input & Quick Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Balas chat penonton...", color = TextMuted, fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("live_chat_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0x990A0A0E),
                        unfocusedContainerColor = Color(0x990A0A0E),
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (replyText.isNotBlank()) TikTokCyan else DarkSurfaceElevated)
                        .clickable(enabled = replyText.isNotBlank()) {
                            chatMessages.add(
                                LiveChatMessage(
                                    username = "@saya (Host)",
                                    text = replyText.trim(),
                                    badge = "Kreator",
                                    highlightColor = TikTokCyan
                                )
                            )
                            replyText = ""
                            coroutineScope.launch {
                                chatListState.animateScrollToItem(chatMessages.size - 1)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Kirim",
                        tint = if (replyText.isNotBlank()) Color.Black else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Share Live Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                        .clickable {
                            Toast.makeText(context, "Tautan siaran langsung berhasil disalin!", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Bagikan Live",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // End Live Recap Summary Dialog
    if (showEndSummaryDialog) {
        Dialog(onDismissRequest = { showEndSummaryDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurface)
                    .border(1.dp, TikTokCyan.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                    .padding(22.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(TikTokRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔴", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Siaran Langsung Selesai",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ringkasan performa dan perolehan monetisasi Anda",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Metrics Grid Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LiveSummaryRow(
                                title = "Durasi Siaran",
                                value = String.format(Locale.US, "%02d:%02d Menit", liveDurationSeconds / 60, liveDurationSeconds % 60)
                            )
                            LiveSummaryRow(
                                title = "Puncak Penonton",
                                value = String.format(Locale.US, "%,d Penonton", (liveViewerCount * 1.35).toInt())
                            )
                            HorizontalDivider(color = DarkBorder)
                            LiveSummaryRow(
                                title = "Total Hadiah Koin",
                                value = "$liveCoinsEarned Koin (Rp ${String.format(Locale.US, "%,.0f", liveCoinsEarned * 8000.0)})",
                                valueColor = TikTokYellow
                            )
                            LiveSummaryRow(
                                title = "Total Saweran DANA (0% Admin)",
                                value = "Rp ${String.format(Locale.US, "%,.0f", liveDanaEarned)}",
                                valueColor = TikTokCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            showEndSummaryDialog = false
                            onEndLive()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_end_live_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan)
                    ) {
                        Text(
                            text = "Kembali ke Beranda",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveToolIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    badgeColor: Color? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (badgeColor != null) badgeColor else if (isActive) Color(0x8800E5FF) else Color(0x66000000))
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (badgeColor != null) Color.White else if (isActive) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LiveSummaryRow(
    title: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
