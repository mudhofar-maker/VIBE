package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ContentFilterType
import com.example.data.model.CreatorEarningsSummary
import com.example.data.model.CreatorPerformanceSummary
import com.example.data.model.CreatorTimeRange
import com.example.data.model.DailyMetricPoint
import com.example.data.model.ThirtyDayEarningsTrend
import com.example.data.model.UserDanaProfile
import com.example.data.model.VideoItem
import com.example.ui.components.DanaBlue
import com.example.ui.components.RechartsEarningsLineChart
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorDashboardScreen(
    earningsSummary: CreatorEarningsSummary,
    performanceSummary: CreatorPerformanceSummary,
    creatorVideos: List<VideoItem>,
    userDanaProfile: UserDanaProfile?,
    selectedTimeRange: CreatorTimeRange,
    selectedFilterType: ContentFilterType,
    searchQuery: String,
    thirtyDayTrend: ThirtyDayEarningsTrend = ThirtyDayEarningsTrend.generateDefault(),
    onTimeRangeSelected: (CreatorTimeRange) -> Unit,
    onFilterSelected: (ContentFilterType) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onDeleteVideo: (String) -> Unit,
    onUpdateVideo: (videoId: String, newCaption: String, newTags: List<String>) -> Unit,
    onOpenWithdrawal: () -> Unit,
    onOpenUploadNewVideo: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedDashboardTab by remember { mutableIntStateOf(0) } // 0: Ringkasan & Performa, 1: Kelola Konten
    val dashboardTabs = listOf("📊 Analisis & Saldo", "🎬 Kelola Konten (${creatorVideos.size})")

    // State for video editing
    var editingVideo by remember { mutableStateOf<VideoItem?>(null) }
    // State for video deep dive analytics sheet
    var analyzingVideo by remember { mutableStateOf<VideoItem?>(null) }
    // State for delete confirmation
    var videoToDelete by remember { mutableStateOf<VideoItem?>(null) }

    val analyticsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler {
        onBackClick()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("creator_dashboard_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali ke Profil",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Studio Kreator",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Terverifikasi",
                            tint = TikTokCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${userDanaProfile?.danaAccountName ?: "Aldi Prasetya"} • Dashboard Bisnis",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Button(
                onClick = onOpenUploadNewVideo,
                colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("creator_new_video_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Video Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Time Range Filter Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CreatorTimeRange.values()) { range ->
                val isSelected = range == selectedTimeRange
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) TikTokCyan.copy(alpha = 0.2f) else DarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) TikTokCyan else DarkBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onTimeRangeSelected(range) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("timerange_${range.name}")
                ) {
                    Text(
                        text = range.label,
                        color = if (isSelected) TikTokCyan else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main Tab Switcher: Overview vs Manage Content
        TabRow(
            selectedTabIndex = selectedDashboardTab,
            containerColor = DarkBackground,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedDashboardTab]),
                    color = TikTokCyan,
                    height = 2.5.dp
                )
            },
            divider = { HorizontalDivider(color = DarkBorder, thickness = 0.5.dp) }
        ) {
            dashboardTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedDashboardTab == index,
                    onClick = { selectedDashboardTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedDashboardTab == index) Color.White else TextMuted,
                            fontSize = 13.sp,
                            fontWeight = if (selectedDashboardTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Screen Tab Body
        when (selectedDashboardTab) {
            0 -> {
                // Tab 0: Earnings & Performance Overview
                CreatorAnalyticsAndEarningsView(
                    earningsSummary = earningsSummary,
                    performanceSummary = performanceSummary,
                    thirtyDayTrend = thirtyDayTrend,
                    onOpenWithdrawal = onOpenWithdrawal
                )
            }
            1 -> {
                // Tab 1: Manage Content Hub
                CreatorContentManagementView(
                    creatorVideos = creatorVideos,
                    selectedFilter = selectedFilterType,
                    searchQuery = searchQuery,
                    onFilterSelected = onFilterSelected,
                    onSearchQueryChange = onSearchQueryChange,
                    onVideoClick = onVideoClick,
                    onEditClick = { editingVideo = it },
                    onAnalyzeClick = { analyzingVideo = it },
                    onDeleteClick = { videoToDelete = it },
                    onUploadClick = onOpenUploadNewVideo
                )
            }
        }
    }

    // Edit Video Caption Dialog
    if (editingVideo != null) {
        val video = editingVideo!!
        var captionText by remember { mutableStateOf(video.caption) }
        var tagsText by remember { mutableStateOf(video.tags.joinToString(" ") { "#$it" }) }

        AlertDialog(
            onDismissRequest = { editingVideo = null },
            title = {
                Text("Edit Informasi Video", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Perbarui judul, caption, atau tagar video agar performa rekomendasi lebih maksimal.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = captionText,
                        onValueChange = { captionText = it },
                        label = { Text("Caption / Deskripsi") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_caption_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TikTokCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 4
                    )

                    OutlinedTextField(
                        value = tagsText,
                        onValueChange = { tagsText = it },
                        label = { Text("Tagar (Pisahkan spasi)") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_tags_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TikTokCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanedTags = tagsText.split(" ")
                            .map { it.trim().removePrefix("#") }
                            .filter { it.isNotBlank() }
                        onUpdateVideo(video.id, captionText.trim(), cleanedTags)
                        editingVideo = null
                        Toast.makeText(context, "Konten video berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Simpan Perubahan", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingVideo = null }) {
                    Text("Batal", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Delete Confirmation Dialog
    if (videoToDelete != null) {
        val video = videoToDelete!!
        AlertDialog(
            onDismissRequest = { videoToDelete = null },
            title = {
                Text("Hapus Video dari Saluran?", color = TikTokRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Text(
                    text = "Video \"${video.caption.take(60)}...\" akan dihapus permanen dari feed dan profil Anda.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteVideo(video.id)
                        videoToDelete = null
                        Toast.makeText(context, "Video berhasil dihapus", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hapus Video", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { videoToDelete = null }) {
                    Text("Batal", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }

    // Detailed Video Analytics Bottom Sheet
    if (analyzingVideo != null) {
        val video = analyzingVideo!!
        ModalBottomSheet(
            onDismissRequest = { analyzingVideo = null },
            sheetState = analyticsSheetState,
            containerColor = DarkSurface,
            contentColor = Color.White
        ) {
            VideoAnalyticsDetailSheetContent(
                video = video,
                onClose = { analyzingVideo = null },
                onWatch = {
                    analyzingVideo = null
                    onVideoClick(video)
                }
            )
        }
    }
}

@Composable
private fun CreatorAnalyticsAndEarningsView(
    earningsSummary: CreatorEarningsSummary,
    performanceSummary: CreatorPerformanceSummary,
    thirtyDayTrend: ThirtyDayEarningsTrend,
    onOpenWithdrawal: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- SECTION 1: TOTAL EARNINGS & DANA WITHDRAWAL ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E2638),
                                Color(0xFF141926),
                                DarkSurface
                            )
                        )
                    )
                    .border(1.5.dp, TikTokYellow.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
                    .testTag("creator_earnings_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💰", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Total Pendapatan Kreator",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00C853).copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF00C853),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${earningsSummary.growthRatePercent}%",
                                color = Color(0xFF00C853),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = earningsSummary.formattedTotal(),
                        color = TikTokYellow,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Termasuk hasil sawer DANA, royalti interaksi @Rp 0,05, & koin hadiah",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tarik ke DANA CTA
                    Button(
                        onClick = onOpenWithdrawal,
                        colors = ButtonDefaults.buttonColors(containerColor = DanaBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("creator_withdraw_dana_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tarik Saldo ke DANA Pribadi (${earningsSummary.formattedWithdrawable()})",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Source Breakdown Columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sawer DANA P2P", color = TextMuted, fontSize = 10.sp)
                            Text(earningsSummary.formattedSawer(), color = TikTokCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${earningsSummary.totalSawerTransactions}x diterima", color = TextSecondary, fontSize = 9.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Royalti Aksi (Rp 0,05)", color = TextMuted, fontSize = 10.sp)
                            Text(earningsSummary.formattedRoyalty(), color = TikTokYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Like, Komen, Share", color = TextSecondary, fontSize = 9.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("VibeGifts Koin", color = TextMuted, fontSize = 10.sp)
                            Text(earningsSummary.formattedCoinGift(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("@Rp 8.000 / koin", color = TextSecondary, fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        // --- SECTION: RECHARTS 30-DAY EARNINGS LINE CHART ---
        item {
            RechartsEarningsLineChart(trendData = thirtyDayTrend)
        }

        // --- SECTION 2: VIDEO PERFORMANCE METRICS ---
        item {
            Text(
                text = "Metrik Performa & Interaksi Video",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 4 Grid Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Tayangan",
                    value = performanceSummary.formattedViews(),
                    subtitle = "Tayangan video",
                    icon = Icons.Default.Visibility,
                    tint = TikTokCyan
                )

                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Engagement Rate",
                    value = performanceSummary.formattedEngagementRate(),
                    subtitle = "Tingkat interaksi",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    tint = Color(0xFF00C853)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Suka",
                    value = performanceSummary.formattedLikes(),
                    subtitle = "${performanceSummary.formattedComments()} Komentar",
                    icon = Icons.Default.Favorite,
                    tint = TikTokRed
                )

                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Share & Simpan",
                    value = "${performanceSummary.formattedShares()} Share",
                    subtitle = "${performanceSummary.formattedBookmarks()} Disimpan",
                    icon = Icons.Default.Bookmark,
                    tint = TikTokYellow
                )
            }
        }

        // Daily Trends Visualization (Bar Chart representation)
        if (performanceSummary.dailyTrends.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Grafik Tren Tayangan & Penghasilan",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Rata-rata retensi video: ${performanceSummary.formattedAvgWatchTime()}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = "📈 Dinamis",
                                color = TikTokCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Custom Bar Visualization
                        val maxViews = performanceSummary.dailyTrends.maxOfOrNull { it.views } ?: 1000L
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            performanceSummary.dailyTrends.forEach { point ->
                                val ratio = (point.views.toFloat() / maxViews.toFloat()).coerceIn(0.15f, 1f)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = VideoItem.formatCount(point.views),
                                        color = if (ratio > 0.8f) TikTokYellow else TextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height((80 * ratio).dp)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(
                                                if (ratio > 0.8f) {
                                                    Brush.verticalGradient(listOf(TikTokYellow, TikTokRed))
                                                } else {
                                                    Brush.verticalGradient(listOf(TikTokCyan, Color(0xFF104E6E)))
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = point.dayLabel,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Audience Insights Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Wawasan Audiens & Distribusi Rekomendasi",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sumber Trafik Utama", color = TextMuted, fontSize = 11.sp)
                        Text("Untuk Anda / FYP (82%)", color = TikTokCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Demografi Usia Terbanyak", color = TextMuted, fontSize = 11.sp)
                        Text("18 - 24 Tahun (56%)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Jam Aktif Penggemar", color = TextMuted, fontSize = 11.sp)
                        Text("19:00 - 22:30 WIB", color = TikTokYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun CreatorContentManagementView(
    creatorVideos: List<VideoItem>,
    selectedFilter: ContentFilterType,
    searchQuery: String,
    onFilterSelected: (ContentFilterType) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onEditClick: (VideoItem) -> Unit,
    onAnalyzeClick: (VideoItem) -> Unit,
    onDeleteClick: (VideoItem) -> Unit,
    onUploadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Cari video berdasarkan judul atau tagar...", fontSize = 12.sp, color = TextMuted) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Hapus", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("creator_content_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TikTokCyan,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Pills Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ContentFilterType.values()) { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) TikTokCyan else DarkSurfaceElevated)
                        .clickable { onFilterSelected(filter) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("filter_${filter.name}")
                ) {
                    Text(
                        text = filter.label,
                        color = if (isSelected) Color.Black else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (creatorVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🎬", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Belum Ada Konten yang Sesuai",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Unggah video atau ubah kata kunci pencarian Anda.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onUploadClick,
                        colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Unggah Video Baru Sekarang")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(creatorVideos, key = { it.id }) { video ->
                    CreatorVideoManageCard(
                        video = video,
                        onPreview = { onVideoClick(video) },
                        onEdit = { onEditClick(video) },
                        onAnalyze = { onAnalyzeClick(video) },
                        onDelete = { onDeleteClick(video) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreatorVideoManageCard(
    video: VideoItem,
    onPreview: () -> Unit,
    onEdit: () -> Unit,
    onAnalyze: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
            .testTag("manage_video_card_${video.id}")
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Thumbnail preview with duration badge
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black)
                        .clickable { onPreview() }
                ) {
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Type & duration badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = video.durationFormatted,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Format Badge (Shorts / Long)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (video.isLongVideo) TikTokYellow else TikTokRed)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (video.isLongVideo) "16:9" else "9:16",
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = video.category,
                            color = TikTokCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (video.isUserCreated) "Dipublikasikan Anda" else "Video Saluran",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = video.caption,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Video Performance Metrics Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👁️ ${video.formattedViews()}", color = TextSecondary, fontSize = 10.sp)
                        Text("❤️ ${video.formattedLikes()}", color = TextSecondary, fontSize = 10.sp)
                        Text("💬 ${video.formattedComments()}", color = TextSecondary, fontSize = 10.sp)
                        Text("💸 ${video.formattedFanInteractionValue()}", color = TikTokYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onPreview,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TikTokCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TikTokCyan.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Tonton", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onEdit,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("edit_button_${video.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Edit", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onAnalyze,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TikTokYellow),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TikTokYellow.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("analyze_button_${video.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Statistik", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(30.dp).testTag("delete_button_${video.id}")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = TikTokRed, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextMuted, fontSize = 11.sp)
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = tint, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun VideoAnalyticsDetailSheetContent(
    video: VideoItem,
    onClose: () -> Unit,
    onWatch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Analisis Mendalam Video", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(video.category, color = TikTokCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = video.caption,
            color = TextSecondary,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Breakdown Stats Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkCard)
                .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Penonton Unik", color = TextMuted, fontSize = 12.sp)
                    Text(video.formattedViews(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Apresiasi Penggemar", color = TextMuted, fontSize = 12.sp)
                    Text(video.formattedFanInteractionValue(), color = TikTokYellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Retensi Rata-rata Penonton", color = TextMuted, fontSize = 12.sp)
                    Text("68.4% (Tinggi)", color = Color(0xFF00C853), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Sumber Trafik Utama", color = TextMuted, fontSize = 12.sp)
                    Text("FYP Beranda (84%)", color = TikTokCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onWatch,
            colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(44.dp)
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tonton & Uji Player Video", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
