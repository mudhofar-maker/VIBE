package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.ui.components.UploadVideoDialog
import com.example.ui.components.EffectsAndFiltersStudioSheet
import com.example.ui.components.VideoEffectOverlay
import com.example.ui.components.VideoEffectsRegistry
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import kotlinx.coroutines.delay

@Composable
fun CreateScreen(
    onClose: () -> Unit,
    onPublishVideo: (caption: String, tags: String, sound: String, category: String, isLongVideo: Boolean, durationFormatted: String) -> Unit
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }
    var showPublishDialog by remember { mutableStateOf(false) }
    var showUploadDialogInCreate by remember { mutableStateOf(false) }

    // Camera features state
    var isFrontCamera by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var selectedSpeed by remember { mutableStateOf("1x") }
    var selectedFilterId by remember { mutableStateOf("normal") }
    var selectedEffectId by remember { mutableStateOf("none") }
    var filterIntensity by remember { mutableFloatStateOf(0.8f) }
    var showEffectsStudioSheet by remember { mutableStateOf(false) }
    var effectsStudioInitialTab by remember { mutableIntStateOf(0) }
    var selectedSound by remember { mutableStateOf("DJ Tabrak Masuk - Remix 2026") }

    val speeds = listOf("0.5x", "1x", "2x", "3x")

    // Recording simulation timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingProgress = 0f
            while (recordingProgress < 1.0f && isRecording) {
                delay(100)
                recordingProgress += 0.02f
            }
            if (isRecording) {
                isRecording = false
                showPublishDialog = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Simulated Camera Viewfinder background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1B1B26),
                            Color(0xFF0F0F17),
                            Color(0xFF181824)
                        )
                    )
                )
        )

        // Dynamic Animated Video Effect + Color Grading Filter Overlay
        VideoEffectOverlay(
            filterId = selectedFilterId,
            filterIntensity = filterIntensity,
            effectId = selectedEffectId
        )

        // Viewfinder guidelines/frame
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
        )

        // Top Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(36.dp).testTag("close_camera_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup Kamera",
                    tint = Color.White
                )
            }

            // Music sound selector pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x66000000))
                    .clickable {
                        Toast.makeText(context, "Musik aktif: $selectedSound", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = TikTokCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = selectedSound.take(20) + "...",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Flash button
            IconButton(
                onClick = { isFlashOn = !isFlashOn },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Flash",
                    tint = if (isFlashOn) TikTokCyan else Color.White
                )
            }
        }

        // Right side camera controls (Flip, Speed, Filter, Timer)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Active Filter & Effect items
            val currentFilterItem = remember(selectedFilterId) { VideoEffectsRegistry.getFilterById(selectedFilterId) }
            val currentEffectItem = remember(selectedEffectId) { VideoEffectsRegistry.getEffectById(selectedEffectId) }

            CameraToolButton(
                icon = Icons.Default.Cameraswitch,
                label = "Putar",
                isActive = isFrontCamera,
                onClick = { isFrontCamera = !isFrontCamera }
            )

            CameraToolButton(
                icon = Icons.Default.Speed,
                label = selectedSpeed,
                isActive = selectedSpeed != "1x",
                onClick = {
                    val nextIdx = (speeds.indexOf(selectedSpeed) + 1) % speeds.size
                    selectedSpeed = speeds[nextIdx]
                }
            )

            CameraToolButton(
                icon = Icons.Default.AutoAwesome,
                label = if (selectedFilterId != "normal") currentFilterItem.name else "Filter",
                isActive = selectedFilterId != "normal",
                onClick = {
                    effectsStudioInitialTab = 1
                    showEffectsStudioSheet = true
                }
            )

            CameraToolButton(
                icon = Icons.Default.Timer,
                label = "Timer",
                isActive = false,
                onClick = {
                    Toast.makeText(context, "Timer 3 detik disiapkan", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Active Filter & Effect indicator badge pill
        val currentEffect = remember(selectedEffectId) { VideoEffectsRegistry.getEffectById(selectedEffectId) }
        val currentFilter = remember(selectedFilterId) { VideoEffectsRegistry.getFilterById(selectedFilterId) }

        if (selectedEffectId != "none" || selectedFilterId != "normal") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x99000000))
                    .border(1.dp, TikTokCyan.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .clickable { showEffectsStudioSheet = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("active_effects_badge")
            ) {
                Icon(
                    imageVector = currentEffect.icon,
                    contentDescription = null,
                    tint = TikTokCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${if (selectedEffectId != "none") currentEffect.name else "Normal"} • ${currentFilter.name} (${(filterIntensity * 100).toInt()}%)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Center recording indicator
        if (isRecording) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (selectedEffectId != "none" || selectedFilterId != "normal") 104.dp else 80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x88000000))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(LiveRed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val seconds = (recordingProgress * 15).toInt()
                Text(
                    text = "00:${if (seconds < 10) "0$seconds" else "$seconds"} / 00:15",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom Controls (Record button and prompt)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRecording) "Ketuk untuk berhenti & simpan" else "Ketuk tombol merah untuk merekam video",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Shutter & Quick Action Row (Efek, Record, Upload)
            val infiniteTransition = rememberInfiniteTransition(label = "recordingPulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = if (isRecording) 1.15f else 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Effects / Filters Studio Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            effectsStudioInitialTab = 0
                            showEffectsStudioSheet = true
                        }
                        .padding(6.dp)
                        .testTag("open_effects_studio_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedEffectId != "none") TikTokCyan.copy(alpha = 0.25f) else DarkSurfaceElevated)
                            .border(
                                width = if (selectedEffectId != "none") 2.dp else 1.dp,
                                color = if (selectedEffectId != "none") TikTokCyan else Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentEffect.icon,
                            contentDescription = "Studio Efek & Filter",
                            tint = if (selectedEffectId != "none") TikTokCyan else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedEffectId != "none") currentEffect.name.take(6) else "Efek",
                        color = if (selectedEffectId != "none") TikTokCyan else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Center Big Record Button
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(TikTokRed)
                        .clickable {
                            if (isRecording) {
                                isRecording = false
                                showPublishDialog = true
                            } else {
                                isRecording = true
                            }
                        }
                        .testTag("record_video_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecording) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                        )
                    }
                }

                // Upload Video Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { showUploadDialogInCreate = true }
                        .padding(6.dp)
                        .testTag("upload_from_camera_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, TikTokCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Upload Video",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Upload", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Studio Efek & Filter Bottom Sheet
        if (showEffectsStudioSheet) {
            EffectsAndFiltersStudioSheet(
                initialTab = effectsStudioInitialTab,
                selectedEffectId = selectedEffectId,
                selectedFilterId = selectedFilterId,
                filterIntensity = filterIntensity,
                onEffectSelected = { effect ->
                    selectedEffectId = effect.id
                    Toast.makeText(context, "Efek: ${effect.name}", Toast.LENGTH_SHORT).show()
                },
                onFilterSelected = { filter ->
                    selectedFilterId = filter.id
                    Toast.makeText(context, "Filter: ${filter.name}", Toast.LENGTH_SHORT).show()
                },
                onIntensityChanged = { intensity ->
                    filterIntensity = intensity
                },
                onResetAll = {
                    selectedEffectId = "none"
                    selectedFilterId = "normal"
                    filterIntensity = 0.8f
                    Toast.makeText(context, "Semua efek direset ke normal", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showEffectsStudioSheet = false }
            )
        }

        // Publish recorded video dialog
        if (showPublishDialog) {
            PublishVideoDialog(
                defaultSound = selectedSound,
                activeFilterName = currentFilter.name,
                activeEffectName = currentEffect.name,
                onDismiss = { showPublishDialog = false },
                onPublish = { caption, tags, category, isLong, duration ->
                    showPublishDialog = false
                    onPublishVideo(caption, tags, selectedSound, category, isLong, duration)
                }
            )
        }

        // Upload Video Dialog directly from camera view
        if (showUploadDialogInCreate) {
            UploadVideoDialog(
                initialIsLongVideo = false,
                onDismiss = { showUploadDialogInCreate = false },
                onPublish = { caption, tags, sound, category, isLong, duration, _, _ ->
                    showUploadDialogInCreate = false
                    onPublishVideo(caption, tags, sound, category, isLong, duration)
                }
            )
        }
    }
}

@Composable
private fun CameraToolButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isActive) TikTokCyan else Color(0x55000000)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.Black else Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PublishVideoDialog(
    defaultSound: String,
    activeFilterName: String = "Normal",
    activeEffectName: String = "Tanpa Efek",
    onDismiss: () -> Unit,
    onPublish: (caption: String, tags: String, category: String, isLongVideo: Boolean, durationFormatted: String) -> Unit
) {
    var caption by remember { mutableStateOf("Video keren terbaru saya! Yuk tonton sampai habis ✨ #fyp #tokvid") }
    var tags by remember { mutableStateOf("#fyp #viral #indonesia #kreator") }
    var selectedCategory by remember { mutableStateOf("Tari & Musik") }
    var isLongVideo by remember { mutableStateOf(false) }
    var durationFormatted by remember { mutableStateOf("12:30") }
    val categories = listOf("Tari & Musik", "Kuliner", "Komedi", "Wisata", "Hewan Peliharaan", "Teknologi")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Posting Video Baru",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp).testTag("close_publish_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Batal",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // FORMAT SELECTOR
                Text("Format Video", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isLongVideo = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isLongVideo) TikTokRed else DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "⚡ Short (9:16)",
                            color = if (!isLongVideo) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { isLongVideo = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLongVideo) TikTokCyan else DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🎬 Panjang (16:9)",
                            color = if (isLongVideo) Color.Black else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Active Effects Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TikTokCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Efek: $activeEffectName • Filter: $activeFilterName",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Deskripsi Video", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("caption_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Hashtag", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Kategori", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSel = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSel) TikTokCyan else DarkSurfaceElevated)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.Black else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val duration = if (isLongVideo) durationFormatted else "0:45"
                        onPublish(caption, tags, selectedCategory, isLongVideo, duration)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_publish_video_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLongVideo) "Posting Video Panjang" else "Posting Short",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
