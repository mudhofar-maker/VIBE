package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContentBottomSheet(
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onUploadVideoClick: () -> Unit,
    onRecordShortsClick: () -> Unit,
    onUploadLongVideoClick: () -> Unit,
    onGoLiveClick: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.25f))
            )
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding()
                .testTag("create_content_bottom_sheet")
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Buat Konten Baru",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pilih format konten yang ingin dibagikan",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).testTag("close_create_options_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Option 1: Upload Video (Short / Long)
            CreationOptionCard(
                title = "Upload Video",
                subtitle = "Pilih klip video atau masukkan tautan (Shorts & Video Panjang)",
                icon = Icons.Default.CloudUpload,
                iconGradient = listOf(TikTokCyan, Color(0xFF00B4D8)),
                badgeText = "Paling Sering",
                badgeColor = TikTokCyan,
                badgeTextColor = Color.Black,
                testTag = "action_upload_video",
                onClick = {
                    onDismiss()
                    onUploadVideoClick()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 2: Record Shorts (Camera)
            CreationOptionCard(
                title = "Rekam Video Shorts",
                subtitle = "Buka studio kamera dengan filter, musik latar, dan efek",
                icon = Icons.Default.Videocam,
                iconGradient = listOf(TikTokRed, Color(0xFFFF5277)),
                badgeText = "Studio Shorts",
                badgeColor = TikTokRed,
                badgeTextColor = Color.White,
                testTag = "action_record_shorts",
                onClick = {
                    onDismiss()
                    onRecordShortsClick()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 3: Upload Long Video (16:9 Widescreen)
            CreationOptionCard(
                title = "Video Panjang Widescreen (16:9)",
                subtitle = "Publikasikan video durasi panjang dengan pemutar horizontal",
                icon = Icons.Default.VideoLibrary,
                iconGradient = listOf(Color(0xFF8338EC), Color(0xFF3A86FF)),
                badgeText = "16:9",
                badgeColor = Color(0xFF8338EC),
                badgeTextColor = Color.White,
                testTag = "action_upload_long_video",
                onClick = {
                    onDismiss()
                    onUploadLongVideoClick()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option 4: Go Live
            CreationOptionCard(
                title = "Siaran Langsung (Live Streaming)",
                subtitle = "Interaksi langsung dan dapatkan gift dari penonton VibeStream",
                icon = Icons.Default.Podcasts,
                iconGradient = listOf(LiveRed, TikTokYellow),
                badgeText = "LIVE",
                badgeColor = LiveRed,
                badgeTextColor = Color.White,
                testTag = "action_go_live",
                onClick = {
                    onDismiss()
                    onGoLiveClick()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CreationOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconGradient: List<Color>,
    badgeText: String? = null,
    badgeColor: Color = TikTokCyan,
    badgeTextColor: Color = Color.Black,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with gradient bubble
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(iconGradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Texts
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(badgeColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = badgeTextColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
