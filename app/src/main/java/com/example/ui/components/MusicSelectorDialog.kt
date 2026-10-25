package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

data class SoundTrackItem(
    val id: String,
    val title: String,
    val artist: String,
    val durationFormatted: String,
    val coverUrl: String,
    val category: String,
    val usesCount: String,
    val audioUrl: String
)

@Composable
fun MusicSelectorDialog(
    currentSound: String,
    onDismiss: () -> Unit,
    onSelectSound: (title: String, artist: String) -> Unit
) {
    val soundTracks = remember {
        listOf(
            SoundTrackItem(
                id = "s_1",
                title = "DJ Tabrak Masuk - Viral Remix 2026",
                artist = "@dj_nusantara",
                durationFormatted = "0:30",
                coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=200",
                category = "Viral",
                usesCount = "1.8M video",
                audioUrl = ""
            ),
            SoundTrackItem(
                id = "s_2",
                title = "Dangdut Koplo Jedag Jedug Full Bass",
                artist = "@koplo_king",
                durationFormatted = "0:45",
                coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=200",
                category = "Dangdut",
                usesCount = "920K video",
                audioUrl = ""
            ),
            SoundTrackItem(
                id = "s_3",
                title = "Aesthetic Sunset Acoustic Vibes",
                artist = "@chillwave_studio",
                durationFormatted = "0:58",
                coverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=200",
                category = "Acoustic",
                usesCount = "640K video",
                audioUrl = ""
            ),
            SoundTrackItem(
                id = "s_4",
                title = "Epic Esports Cyber Synth Beat",
                artist = "@pro_gamer_id",
                durationFormatted = "0:35",
                coverUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=200",
                category = "Gaming",
                usesCount = "450K video",
                audioUrl = ""
            ),
            SoundTrackItem(
                id = "s_5",
                title = "Pop Nusantara Manis Romantis",
                artist = "@melodi_indonesia",
                durationFormatted = "0:42",
                coverUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=200",
                category = "Pop",
                usesCount = "780K video",
                audioUrl = ""
            ),
            SoundTrackItem(
                id = "s_6",
                title = "Suara Lucu Kartun Komedi Wkwk",
                artist = "@soundfx_id",
                durationFormatted = "0:15",
                coverUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                category = "Komedi",
                usesCount = "1.2M video",
                audioUrl = ""
            ),
            SoundTrackItem(
                id = "s_7",
                title = "Lofi Cooldown Coffee Shop Indonesia",
                artist = "@santai_beats",
                durationFormatted = "1:00",
                coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=200",
                category = "Acoustic",
                usesCount = "310K video",
                audioUrl = ""
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    var playingPreviewId by remember { mutableStateOf<String?>("s_1") }
    var selectedTrack by remember {
        mutableStateOf(soundTracks.firstOrNull { it.title == currentSound } ?: soundTracks.first())
    }
    var musicVolume by remember { mutableFloatStateOf(0.85f) }

    val categories = listOf("Semua", "Viral", "Dangdut", "Acoustic", "Gaming", "Pop", "Komedi")

    val filteredTracks = remember(searchQuery, selectedCategory) {
        soundTracks.filter { track ->
            val matchesCat = selectedCategory == "Semua" || track.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                track.title.contains(searchQuery, ignoreCase = true) ||
                track.artist.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .border(1.dp, TikTokCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TikTokCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = TikTokCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Pilih Musik & Audio",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Koleksi sound viral & remix bebas royalti",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari judul lagu, remix, artis...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TikTokCyan, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("music_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) TikTokCyan else DarkSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) TikTokCyan else DarkBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Music Tracks List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTracks) { track ->
                        val isTrackSelected = selectedTrack.id == track.id
                        val isPlaying = playingPreviewId == track.id

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isTrackSelected) Color(0xFF1E2838) else DarkSurfaceElevated
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = if (isTrackSelected) BorderStroke(1.5.dp, TikTokCyan) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTrack = track
                                    playingPreviewId = if (isPlaying) null else track.id
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Cover thumbnail with play indicator
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    AsyncImage(
                                        model = track.coverUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0x55000000)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Track Information
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        color = if (isTrackSelected) TikTokCyan else TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = track.artist,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = " • ${track.durationFormatted} • ${track.usesCount}",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Select Radio/Badge
                                if (isTrackSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(TikTokCyan),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Dipilih",
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sound Volume Slider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume Musik",
                        tint = TikTokCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Volume: ${(musicVolume * 100).toInt()}%",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = musicVolume,
                        onValueChange = { musicVolume = it },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = TikTokCyan,
                            activeTrackColor = TikTokCyan,
                            inactiveTrackColor = DarkBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom CTA Button
                Button(
                    onClick = {
                        onSelectSound(selectedTrack.title, selectedTrack.artist)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_selected_music_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gunakan Musik Ini",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
