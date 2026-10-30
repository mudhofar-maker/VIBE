package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
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
import com.example.ui.components.MusicSelectorDialog
import com.example.ui.components.DraggableZoomableCaptionText
import com.example.ui.components.EditCaptionDialog
import com.example.ui.components.VideoCaptionItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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

enum class CreateMode {
    RECORD, GALLERY, LIVE
}

@Composable
fun CreateScreen(
    onClose: () -> Unit,
    onPublishVideo: (caption: String, tags: String, sound: String, category: String, isLongVideo: Boolean, durationFormatted: String) -> Unit
) {
    val context = LocalContext.current
    var isLiveStreamingActive by remember { mutableStateOf(false) }
    var showMusicSelector by remember { mutableStateOf(false) }
    var selectedCreateMode by remember { mutableStateOf(CreateMode.RECORD) }

    // Dynamic Pinch-to-zoom Captions State
    val captions = remember { mutableStateListOf<VideoCaptionItem>() }
    var captionBeingEdited by remember { mutableStateOf<VideoCaptionItem?>(null) }
    var pickedGalleryUri by remember { mutableStateOf<Uri?>(null) }

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

    // Android Zero-Permission Photo/Video Picker for Gallery Videos
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedGalleryUri = uri
            Toast.makeText(context, "Video dari galeri HP berhasil dimuat!", Toast.LENGTH_SHORT).show()
            showPublishDialog = true
        }
    }

    // Camera & Audio Permission Launcher for Phone Camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val camGranted = perms[android.Manifest.permission.CAMERA] ?: false
        val micGranted = perms[android.Manifest.permission.RECORD_AUDIO] ?: false
        if (camGranted) {
            Toast.makeText(context, "Kamera HP siap digunakan!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk merekam langsung.", Toast.LENGTH_SHORT).show()
        }
    }

    // Launch Live Streaming Studio if active
    if (isLiveStreamingActive) {
        LiveStreamingScreen(
            onEndLive = { isLiveStreamingActive = false }
        )
        return
    }

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
                .padding(28.dp)
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
        )

        // Dynamic Interactive Captions with Pinch-to-Zoom (Cubit Layar)
        captions.forEachIndexed { index, captionItem ->
            DraggableZoomableCaptionText(
                caption = captionItem,
                onUpdate = { updated ->
                    if (index < captions.size) {
                        captions[index] = updated
                    }
                },
                onDelete = {
                    if (index < captions.size) {
                        captions.removeAt(index)
                    }
                },
                onEditRequested = { captionBeingEdited = captionItem },
                modifier = Modifier.align(Alignment.Center)
            )
        }

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

            // Music sound selector pill (opens MusicSelectorDialog)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x77000000))
                    .border(1.dp, TikTokCyan.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .clickable { showMusicSelector = true }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("select_music_pill")
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = TikTokCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = selectedSound.take(22) + if (selectedSound.length > 22) "..." else "",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
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

        // Right side camera controls (Teks, Musik, Putar, Speed, Filter, Efek)
        val currentFilterItem = remember(selectedFilterId) { VideoEffectsRegistry.getFilterById(selectedFilterId) }
        val currentEffectItem = remember(selectedEffectId) { VideoEffectsRegistry.getEffectById(selectedEffectId) }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Add Text Font Full Color Button (Bisa di-cubit)
            CameraToolButton(
                icon = Icons.Default.TextFields,
                label = "Teks",
                isActive = captions.isNotEmpty(),
                onClick = {
                    val newCap = VideoCaptionItem(
                        id = "cap_${System.currentTimeMillis()}",
                        text = "VibeStream Keren ✨"
                    )
                    captions.add(newCap)
                    captionBeingEdited = newCap
                }
            )

            // Music Selector Button
            CameraToolButton(
                icon = Icons.Default.MusicNote,
                label = "Musik",
                isActive = true,
                onClick = { showMusicSelector = true }
            )

            // Flip Camera
            CameraToolButton(
                icon = Icons.Default.Cameraswitch,
                label = "Putar",
                isActive = isFrontCamera,
                onClick = { isFrontCamera = !isFrontCamera }
            )

            // Speed
            CameraToolButton(
                icon = Icons.Default.Speed,
                label = selectedSpeed,
                isActive = selectedSpeed != "1x",
                onClick = {
                    val nextIdx = (speeds.indexOf(selectedSpeed) + 1) % speeds.size
                    selectedSpeed = speeds[nextIdx]
                }
            )

            // Filter Studio
            CameraToolButton(
                icon = Icons.Default.AutoAwesome,
                label = if (selectedFilterId != "normal") currentFilterItem.name else "Filter",
                isActive = selectedFilterId != "normal",
                onClick = {
                    effectsStudioInitialTab = 1
                    showEffectsStudioSheet = true
                }
            )

            // Live Streaming Quick Button
            CameraToolButton(
                icon = Icons.Default.LiveTv,
                label = "LIVE",
                isActive = true,
                onClick = { isLiveStreamingActive = true }
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

        // Bottom Section: Mode Switcher, Shutter, Gallery & Upload
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode Switcher Tabs: Rekam | Galeri HP | LIVE STREAM
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x990A0A0E))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Rekam Video
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (selectedCreateMode == CreateMode.RECORD) TikTokCyan else Color.Transparent)
                        .clickable { selectedCreateMode = CreateMode.RECORD }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = if (selectedCreateMode == CreateMode.RECORD) Color.Black else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Rekam",
                            color = if (selectedCreateMode == CreateMode.RECORD) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Tab 2: Galeri HP (Android Photo/Video Picker)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Transparent)
                        .clickable {
                            selectedCreateMode = CreateMode.GALLERY
                            galleryPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("pick_from_phone_gallery_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Galeri HP",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Tab 3: LIVE STREAM Studio
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (selectedCreateMode == CreateMode.LIVE) LiveRed else Color.Transparent)
                        .clickable {
                            selectedCreateMode = CreateMode.LIVE
                            isLiveStreamingActive = true
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("launch_live_streaming_tab")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LiveRed)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isRecording) "Ketuk untuk berhenti & simpan" else "Ketuk tombol merah untuk merekam video",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

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
                            cameraPermissionLauncher.launch(
                                arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                            )
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

                // Galeri HP Direct Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            galleryPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
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
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Galeri HP",
                            tint = TikTokCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Galeri", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Music Selector Dialog
        if (showMusicSelector) {
            MusicSelectorDialog(
                currentSound = selectedSound,
                onDismiss = { showMusicSelector = false },
                onSelectSound = { title, artist ->
                    selectedSound = title
                    Toast.makeText(context, "Musik dipilih: $title", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Edit Caption Dialog
        if (captionBeingEdited != null) {
            EditCaptionDialog(
                initialCaption = captionBeingEdited!!,
                onDismiss = { captionBeingEdited = null },
                onConfirm = { updated ->
                    val existingIdx = captions.indexOfFirst { it.id == updated.id }
                    if (existingIdx >= 0) {
                        captions[existingIdx] = updated
                    } else {
                        captions.add(updated)
                    }
                    captionBeingEdited = null
                }
            )
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
            val captionDefault = if (captions.isNotEmpty()) {
                captions.joinToString(" ") { it.text } + " #fyp #vibe"
            } else {
                "Video keren terbaru saya! Yuk tonton sampai habis ✨ #fyp #tokvid"
            }

            PublishVideoDialog(
                defaultSound = selectedSound,
                activeFilterName = currentFilter.name,
                activeEffectName = currentEffect.name,
                initialCaption = captionDefault,
                onDismiss = { showPublishDialog = false },
                onPublish = { caption, tags, sound, category, isLong, duration ->
                    showPublishDialog = false
                    onPublishVideo(caption, tags, sound, category, isLong, duration)
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
    initialCaption: String = "Video keren terbaru saya! Yuk tonton sampai habis ✨ #fyp #tokvid",
    onDismiss: () -> Unit,
    onPublish: (caption: String, tags: String, sound: String, category: String, isLongVideo: Boolean, durationFormatted: String) -> Unit
) {
    var caption by remember { mutableStateOf(initialCaption) }
    var soundTitle by remember { mutableStateOf(if (defaultSound.isNotBlank()) defaultSound else "DJ Tabrak Masuk - Remix Nusantara") }
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

                Text("Musik / Audio Latar", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = soundTitle,
                    onValueChange = { soundTitle = it },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = TikTokCyan)
                    },
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("publish_sound_title_input")
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
                        onPublish(caption, tags, soundTitle.trim(), selectedCategory, isLongVideo, duration)
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
