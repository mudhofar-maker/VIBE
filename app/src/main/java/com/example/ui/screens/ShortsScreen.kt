package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.components.EffectsAndFiltersStudioSheet
import com.example.ui.components.VideoEffectOverlay
import com.example.ui.components.VideoEffectsRegistry
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.VideoItem
import com.example.ui.components.VideoPlayerView
import com.example.ui.components.VinylDisc
import com.example.ui.theme.LiveRed
import com.example.ui.theme.OverlayDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokYellow
import com.example.ui.viewmodel.FeedTab

@Composable
fun ShortsScreen(
    videos: List<VideoItem>,
    activeFeedTab: FeedTab,
    onTabSelected: (FeedTab) -> Unit,
    onSearchClick: () -> Unit,
    onLiveClick: () -> Unit,
    onLikeClick: (VideoItem) -> Unit,
    onBookmarkClick: (VideoItem) -> Unit,
    onCommentClick: (VideoItem) -> Unit,
    onShareClick: (VideoItem) -> Unit,
    onSoundClick: (VideoItem) -> Unit,
    onCreatorClick: (VideoItem) -> Unit,
    onFollowClick: (String, Boolean) -> Unit,
    onDanaSawerClick: (VideoItem) -> Unit = {},
    onGiftClick: (VideoItem) -> Unit = {}
) {
    val shortsList = remember(videos) {
        val filtered = videos.filter { !it.isLongVideo }
        if (filtered.isEmpty()) videos else filtered
    }

    if (shortsList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Belum ada video Shorts",
                color = TextPrimary,
                fontSize = 16.sp
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { shortsList.size })

    // Viewer Video Effects and Filters State
    var viewerEffectId by remember { mutableStateOf("none") }
    var viewerFilterId by remember { mutableStateOf("normal") }
    var viewerFilterIntensity by remember { mutableFloatStateOf(0.8f) }
    var showViewerEffectsStudio by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val video = shortsList[page]
            val isCurrent = pagerState.currentPage == page

            ShortVideoPage(
                video = video,
                isCurrent = isCurrent,
                viewerEffectId = viewerEffectId,
                viewerFilterId = viewerFilterId,
                viewerFilterIntensity = viewerFilterIntensity,
                onEffectsClick = { showViewerEffectsStudio = true },
                onLikeClick = { onLikeClick(video) },
                onBookmarkClick = { onBookmarkClick(video) },
                onCommentClick = { onCommentClick(video) },
                onShareClick = { onShareClick(video) },
                onSoundClick = { onSoundClick(video) },
                onCreatorClick = { onCreatorClick(video) },
                onFollowClick = { onFollowClick(video.creatorUsername, video.isFollowing) },
                onDanaSawerClick = { onDanaSawerClick(video) },
                onGiftClick = { onGiftClick(video) }
            )
        }

        // Active Viewer Effect Pill Indicator
        val activeViewerEffect = remember(viewerEffectId) { VideoEffectsRegistry.getEffectById(viewerEffectId) }
        val activeViewerFilter = remember(viewerFilterId) { VideoEffectsRegistry.getFilterById(viewerFilterId) }

        if (viewerEffectId != "none" || viewerFilterId != "normal") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x99000000))
                    .border(1.dp, TikTokCyan.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .clickable { showViewerEffectsStudio = true }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = activeViewerEffect.icon,
                    contentDescription = null,
                    tint = TikTokCyan,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (viewerEffectId != "none") activeViewerEffect.name else "Normal"} • ${activeViewerFilter.name}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "✕",
                    color = TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable {
                        viewerEffectId = "none"
                        viewerFilterId = "normal"
                    }
                )
            }
        }

        // Top Navigation Bar (Mengikuti | Untuk Anda, Live, Search)
        TopShortsBar(
            activeTab = activeFeedTab,
            onTabSelected = onTabSelected,
            onSearchClick = onSearchClick,
            onLiveClick = onLiveClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        // Viewer Effects Studio Bottom Sheet
        if (showViewerEffectsStudio) {
            EffectsAndFiltersStudioSheet(
                initialTab = 0,
                selectedEffectId = viewerEffectId,
                selectedFilterId = viewerFilterId,
                filterIntensity = viewerFilterIntensity,
                onEffectSelected = { effect ->
                    viewerEffectId = effect.id
                },
                onFilterSelected = { filter ->
                    viewerFilterId = filter.id
                },
                onIntensityChanged = { intensity ->
                    viewerFilterIntensity = intensity
                },
                onResetAll = {
                    viewerEffectId = "none"
                    viewerFilterId = "normal"
                    viewerFilterIntensity = 0.8f
                },
                onDismiss = { showViewerEffectsStudio = false }
            )
        }
    }
}

@Composable
private fun ShortVideoPage(
    video: VideoItem,
    isCurrent: Boolean,
    viewerEffectId: String = "none",
    viewerFilterId: String = "normal",
    viewerFilterIntensity: Float = 0.8f,
    onEffectsClick: () -> Unit = {},
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSoundClick: () -> Unit,
    onCreatorClick: () -> Unit,
    onFollowClick: () -> Unit,
    onDanaSawerClick: () -> Unit = {},
    onGiftClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VideoPlayerView(
            videoUrl = video.videoUrl,
            thumbnailUrl = video.thumbnailUrl,
            isCurrentPage = isCurrent,
            onDoubleTapLike = {
                if (!video.isLiked) {
                    onLikeClick()
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Live Dynamic Video Effect & Filter Overlay
        if (isCurrent && (viewerEffectId != "none" || viewerFilterId != "normal")) {
            VideoEffectOverlay(
                filterId = viewerFilterId,
                filterIntensity = viewerFilterIntensity,
                effectId = viewerEffectId
            )
        }

        // Gradient overlay at the bottom for readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x33000000),
                            Color(0xCC000000)
                        )
                    )
                )
        )

        // Right Action Column (Avatar + Follow, Like, Comment, Bookmark, Share, Sound Disc)
        RightShortsActionColumn(
            video = video,
            viewerEffectId = viewerEffectId,
            viewerFilterId = viewerFilterId,
            onEffectsClick = onEffectsClick,
            onLikeClick = onLikeClick,
            onBookmarkClick = onBookmarkClick,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
            onCreatorClick = onCreatorClick,
            onFollowClick = onFollowClick,
            onSoundClick = onSoundClick,
            onDanaSawerClick = onDanaSawerClick,
            onGiftClick = onGiftClick,
            isPlaying = isCurrent,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 80.dp)
        )

        // Bottom Left Creator Info (Username, Caption, Tags, Sound Title)
        BottomShortsInfo(
            video = video,
            onCreatorClick = onCreatorClick,
            onSoundClick = onSoundClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 14.dp, bottom = 80.dp)
        )
    }
}

@Composable
private fun TopShortsBar(
    activeTab: FeedTab,
    onTabSelected: (FeedTab) -> Unit,
    onSearchClick: () -> Unit,
    onLiveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LIVE button
        IconButton(
            onClick = onLiveClick,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Videocam,
                contentDescription = "Siaran Langsung",
                tint = LiveRed,
                modifier = Modifier.size(28.dp)
            )
        }

        // Tabs: Mengikuti | Untuk Anda
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(FeedTab.FOLLOWING) }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Mengikuti",
                    color = if (activeTab == FeedTab.FOLLOWING) TextPrimary else TextMuted,
                    fontSize = 17.sp,
                    fontWeight = if (activeTab == FeedTab.FOLLOWING) FontWeight.Bold else FontWeight.Medium
                )
                if (activeTab == FeedTab.FOLLOWING) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .width(28.dp)
                            .height(2.5.dp)
                            .background(Color.White, RoundedCornerShape(2.dp))
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(FeedTab.FOR_YOU) }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Untuk Anda",
                    color = if (activeTab == FeedTab.FOR_YOU) TextPrimary else TextMuted,
                    fontSize = 17.sp,
                    fontWeight = if (activeTab == FeedTab.FOR_YOU) FontWeight.Bold else FontWeight.Medium
                )
                if (activeTab == FeedTab.FOR_YOU) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .width(28.dp)
                            .height(2.5.dp)
                            .background(Color.White, RoundedCornerShape(2.dp))
                    )
                }
            }
        }

        // Search button
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(44.dp)
                .testTag("shorts_search_button")
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Cari",
                tint = TextPrimary,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun RightShortsActionColumn(
    video: VideoItem,
    viewerEffectId: String = "none",
    viewerFilterId: String = "normal",
    onEffectsClick: () -> Unit = {},
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onCreatorClick: () -> Unit,
    onFollowClick: () -> Unit,
    onSoundClick: () -> Unit,
    onDanaSawerClick: () -> Unit = {},
    onGiftClick: () -> Unit = {},
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Avatar with Follow Plus Badge
        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            AsyncImage(
                model = video.creatorAvatar,
                contentDescription = video.creatorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .clickable(onClick = onCreatorClick)
            )

            // Follow Plus Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 2.dp)
                    .size(22.dp)
                    .background(
                        color = if (video.isFollowing) TikTokCyan else TikTokRed,
                        shape = CircleShape
                    )
                    .clickable(onClick = onFollowClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (video.isFollowing) Icons.Filled.Check else Icons.Filled.Add,
                    contentDescription = if (video.isFollowing) "Mengikuti" else "Ikuti",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Like Button
        val likeScale by animateFloatAsState(
            targetValue = if (video.isLiked) 1.25f else 1f,
            animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
            label = "likeScale"
        )
        ActionShortsItem(
            icon = if (video.isLiked) Icons.Filled.Favorite else Icons.Filled.Favorite,
            label = video.formattedLikes(),
            tint = if (video.isLiked) TikTokRed else Color.White,
            iconScale = likeScale,
            onClick = onLikeClick,
            testTag = "shorts_like_button"
        )

        // Comment Button
        ActionShortsItem(
            icon = Icons.AutoMirrored.Filled.Comment,
            label = video.formattedComments(),
            tint = Color.White,
            onClick = onCommentClick,
            testTag = "shorts_comment_button"
        )

        // Bookmark / Favorite Button
        ActionShortsItem(
            icon = if (video.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.Bookmark,
            label = video.formattedBookmarks(),
            tint = if (video.isBookmarked) TikTokYellow else Color.White,
            onClick = onBookmarkClick,
            testTag = "shorts_bookmark_button"
        )

        // Share Button
        ActionShortsItem(
            icon = Icons.Filled.Share,
            label = video.formattedShares(),
            tint = Color.White,
            onClick = onShareClick,
            testTag = "shorts_share_button"
        )

        // Video Effect & Filter Studio Button
        val hasActiveFilterOrEffect = viewerEffectId != "none" || viewerFilterId != "normal"
        ActionShortsItem(
            icon = Icons.Filled.AutoAwesome,
            label = if (hasActiveFilterOrEffect) "Efek ON" else "Efek",
            tint = if (hasActiveFilterOrEffect) TikTokCyan else Color.White,
            onClick = onEffectsClick,
            testTag = "shorts_effects_button"
        )

        // Sawer / Dukung Kreator via DANA Barcode (P2P direct)
        ActionShortsItem(
            icon = Icons.Filled.QrCode2,
            label = "Sawer",
            tint = Color(0xFF118EEA),
            onClick = onDanaSawerClick,
            testTag = "shorts_dana_sawer_button"
        )

        // Kirim Gift Koin Interaksi
        ActionShortsItem(
            icon = Icons.Filled.CardGiftcard,
            label = "Gift",
            tint = TikTokYellow,
            onClick = onGiftClick,
            testTag = "shorts_gift_button"
        )

        // Rotating Vinyl Disc
        VinylDisc(
            soundCoverUrl = video.soundCoverUrl,
            isPlaying = isPlaying,
            onClick = onSoundClick
        )
    }
}

@Composable
private fun ActionShortsItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    iconScale: Float = 1f,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier
                .size(36.dp)
                .scale(iconScale)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BottomShortsInfo(
    video: VideoItem,
    onCreatorClick: () -> Unit,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // Creator Username & Verified Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onCreatorClick)
        ) {
            Text(
                text = video.creatorUsername,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (video.isVerified) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = "Verified Creator",
                    tint = TikTokCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Caption & Hashtags
        Text(
            text = video.caption,
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 19.sp,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Sound Track Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(onClick = onSoundClick)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = "Sound Track",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${video.soundTitle} - ${video.soundAuthor}",
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}
