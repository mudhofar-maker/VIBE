package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PresetSampleVideo(
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val suggestedTags: String,
    val category: String,
    val durationFormatted: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadVideoDialog(
    initialIsLongVideo: Boolean = false,
    onDismiss: () -> Unit,
    onPublish: (
        caption: String,
        tags: String,
        soundTitle: String,
        category: String,
        isLongVideo: Boolean,
        durationFormatted: String,
        videoUrl: String?,
        thumbnailUrl: String?
    ) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLongVideo by remember { mutableStateOf(initialIsLongVideo) }
    var caption by remember {
        mutableStateOf(
            if (initialIsLongVideo) "Review & Eksplorasi Mendalam: Teknologi Masa Depan 2026! 🚀"
            else "Video kreasi baru yang wajib kamu tonton! ✨ #vibestream #fyp"
        )
    }
    var tags by remember { mutableStateOf("#fyp #vibestream #viral #kreator") }
    var soundTitle by remember { mutableStateOf("Suara Asli - @kreator_vibestream") }
    var selectedCategory by remember { mutableStateOf(if (initialIsLongVideo) "Teknologi" else "Tari & Musik") }
    var customVideoUrl by remember { mutableStateOf("") }
    var customThumbnailUrl by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    // Video Effects and Filters state
    var selectedFilterId by remember { mutableStateOf("vibrant") }
    var selectedEffectId by remember { mutableStateOf("sparkle") }
    var filterIntensity by remember { mutableFloatStateOf(0.75f) }

    val presetVideos = remember {
        listOf(
            PresetSampleVideo(
                title = "Cinematic Alam Nusantara",
                description = "Keindahan laut dan terumbu karang laut lepas Indonesia",
                videoUrl = "https://vjs.zencdn.net/v/oceans.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
                suggestedTags = "#nature #pesonaindonesia #ocean #cinematic",
                category = "Wisata",
                durationFormatted = "01:15"
            ),
            PresetSampleVideo(
                title = "Big Buck Bunny HD",
                description = "Animasi kartun pendek beresolusi tinggi",
                videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800",
                suggestedTags = "#animation #kartun #funny #bunny",
                category = "Komedi",
                durationFormatted = "09:56"
            ),
            PresetSampleVideo(
                title = "Bunga Mekar Time-lapse",
                description = "Video estetika makro mekarnya kelopak bunga",
                videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1490750967868-88aa4486c946?w=800",
                suggestedTags = "#timelapse #aesthetic #macro #flower",
                category = "Wisata",
                durationFormatted = "00:30"
            ),
            PresetSampleVideo(
                title = "Tech & AI Smart Cities",
                description = "Dokumenter perkembangan teknologi dan kecerdasan buatan",
                videoUrl = "https://raw.githubusercontent.com/intel-iot-devkit/sample-videos/master/people-detection.mp4",
                thumbnailUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800",
                suggestedTags = "#teknologi #ai #smartcity #gadget",
                category = "Teknologi",
                durationFormatted = "14:20"
            )
        )
    }

    var selectedPreset by remember { mutableStateOf<PresetSampleVideo?>(presetVideos.first()) }

    val categories = listOf("Tari & Musik", "Kuliner", "Komedi", "Wisata", "Hewan Peliharaan", "Teknologi", "Gaming")
    val quickHashtags = listOf("#fyp", "#vibestream", "#viral", "#indonesia", "#kreator", "#trending", "#videokeren", "#tips")

    Dialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
                .testTag("upload_video_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(TikTokCyan, TikTokRed))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Upload Video Baru",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Publikasikan ke feed VibeStream",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isUploading,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_upload_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup Dialog",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Format Selector (Short 9:16 vs Long 16:9)
                Text(
                    text = "Pilih Format Video",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Short Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isLongVideo) TikTokRed.copy(alpha = 0.2f) else DarkSurfaceElevated)
                            .border(
                                width = if (!isLongVideo) 1.5.dp else 1.dp,
                                color = if (!isLongVideo) TikTokRed else DarkBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { isLongVideo = false }
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                            .testTag("format_short_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "⚡ Short (9:16)",
                                color = if (!isLongVideo) TikTokRed else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Vertikal, Penuh Layar",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Long Video Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isLongVideo) TikTokCyan.copy(alpha = 0.2f) else DarkSurfaceElevated)
                            .border(
                                width = if (isLongVideo) 1.5.dp else 1.dp,
                                color = if (isLongVideo) TikTokCyan else DarkBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { isLongVideo = true }
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                            .testTag("format_long_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🎬 Video Panjang (16:9)",
                                color = if (isLongVideo) TikTokCyan else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Widescreen, Tonton Lanjut",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Select Video File / Preset
                Text(
                    text = "Pilih Klip Video Siap Pakai atau Gunakan Link Sendiri",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetVideos) { preset ->
                        val isSelected = selectedPreset == preset && customVideoUrl.isBlank()
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) TikTokCyan else DarkBorder
                            ),
                            modifier = Modifier
                                .width(130.dp)
                                .clickable {
                                    selectedPreset = preset
                                    customVideoUrl = ""
                                    selectedCategory = preset.category
                                    if (tags.length < 15) {
                                        tags = "$tags ${preset.suggestedTags}".trim()
                                    }
                                }
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(65.dp)
                                ) {
                                    AsyncImage(
                                        model = preset.thumbnailUrl,
                                        contentDescription = preset.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxWidth().height(65.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(24.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.padding(6.dp)) {
                                    Text(
                                        text = preset.title,
                                        color = if (isSelected) TikTokCyan else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = preset.durationFormatted,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Video URL Input (Optional)
                OutlinedTextField(
                    value = customVideoUrl,
                    onValueChange = {
                        customVideoUrl = it
                        if (it.isNotBlank()) selectedPreset = null
                    },
                    label = { Text("Link / URL Video Sendiri (Opsional)") },
                    placeholder = { Text("https://contoh.com/video.mp4") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = TikTokCyan)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = TikTokCyan,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("video_url_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Caption / Title
                Text(
                    text = if (isLongVideo) "Judul & Deskripsi Video Panjang" else "Caption Video Short",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    placeholder = { Text("Tulis deskripsi menarik tentang videomu...") },
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
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("caption_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Hashtag Chips
                Text(
                    text = "Hashtag Populer (Ketuk untuk menambah)",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    quickHashtags.forEach { tag ->
                        val isIncluded = tags.contains(tag)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isIncluded) TikTokCyan.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                .border(
                                    0.5.dp,
                                    if (isIncluded) TikTokCyan else DarkBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    if (!isIncluded) {
                                        tags = (tags.trim() + " " + tag).trim()
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag,
                                color = if (isIncluded) TikTokCyan else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isIncluded) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Daftar Hashtag") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = TikTokCyan,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tags_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Selector
                Text(
                    text = "Kategori Konten",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSel = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSel) TikTokRed else DarkSurfaceElevated)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.White else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sound Title
                OutlinedTextField(
                    value = soundTitle,
                    onValueChange = { soundTitle = it },
                    label = { Text("Audio / Musik Latar") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = TikTokRed)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = TikTokCyan,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("sound_title_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Video Effects & Filters Selection Section
                val activeFilter = remember(selectedFilterId) { VideoEffectsRegistry.getFilterById(selectedFilterId) }
                val activeEffect = remember(selectedEffectId) { VideoEffectsRegistry.getEffectById(selectedEffectId) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, TikTokCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
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
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sentuhan Efek & Filter Video",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${activeFilter.name} • ${activeEffect.name}",
                            color = TikTokCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mini Preview Card with real VideoEffectOverlay!
                    val previewThumbnail = when {
                        customThumbnailUrl.isNotBlank() -> customThumbnailUrl
                        selectedPreset != null -> selectedPreset!!.thumbnailUrl
                        else -> "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = previewThumbnail,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Live Dynamic Effect Overlay
                        VideoEffectOverlay(
                            filterId = selectedFilterId,
                            filterIntensity = filterIntensity,
                            effectId = selectedEffectId
                        )

                        // Mini badge in preview
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Preview: ${activeEffect.name} • ${activeFilter.name}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Options Row
                    Text("Pilih Filter Warna", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(VideoEffectsRegistry.filters) { filter ->
                            val isSel = filter.id == selectedFilterId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) TikTokCyan.copy(alpha = 0.25f) else DarkSurface)
                                    .border(
                                        width = if (isSel) 1.5.dp else 0.5.dp,
                                        color = if (isSel) TikTokCyan else DarkBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedFilterId = filter.id }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = filter.name,
                                    color = if (isSel) TikTokCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Effect Options Row
                    Text("Pilih Efek Video Visual", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(VideoEffectsRegistry.effects) { effect ->
                            val isSel = effect.id == selectedEffectId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) TikTokRed.copy(alpha = 0.25f) else DarkSurface)
                                    .border(
                                        width = if (isSel) 1.5.dp else 0.5.dp,
                                        color = if (isSel) TikTokRed else DarkBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedEffectId = effect.id }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = effect.icon,
                                        contentDescription = null,
                                        tint = if (isSel) TikTokRed else TextMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = effect.name,
                                        color = if (isSel) Color.White else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isUploading,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            if (caption.isBlank()) {
                                Toast.makeText(context, "Silakan isi deskripsi video!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isUploading = true
                            coroutineScope.launch {
                                // Short simulated progress for nice UX
                                delay(600)
                                val finalVideoUrl = if (customVideoUrl.isNotBlank()) {
                                    customVideoUrl
                                } else {
                                    selectedPreset?.videoUrl
                                }

                                val finalThumbnailUrl = if (customThumbnailUrl.isNotBlank()) {
                                    customThumbnailUrl
                                } else {
                                    selectedPreset?.thumbnailUrl
                                }

                                val duration = if (isLongVideo) {
                                    selectedPreset?.durationFormatted ?: "12:30"
                                } else {
                                    selectedPreset?.durationFormatted ?: "0:45"
                                }

                                onPublish(
                                    caption.trim(),
                                    tags.trim(),
                                    soundTitle.trim(),
                                    selectedCategory,
                                    isLongVideo,
                                    duration,
                                    finalVideoUrl,
                                    finalThumbnailUrl
                                )
                                isUploading = false
                                Toast.makeText(context, "Video berhasil diunggah ke VibeStream! 🎉", Toast.LENGTH_LONG).show()
                                onDismiss()
                            }
                        },
                        enabled = !isUploading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLongVideo) TikTokCyan else TikTokRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp)
                            .testTag("submit_upload_button")
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                color = if (isLongVideo) Color.Black else Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mengunggah...",
                                color = if (isLongVideo) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = if (isLongVideo) Color.Black else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isLongVideo) "Publikasikan Video" else "Unggah Short",
                                color = if (isLongVideo) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
