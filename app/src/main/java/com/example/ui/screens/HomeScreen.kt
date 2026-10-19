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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
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
fun HomeScreen(
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
    onFollowClick: (String, Boolean) -> Unit
) {
    if (videos.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Memuat video...", color = TextSecondary, fontSize = 16.sp)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { videos.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Vertical Video Pager
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val video = videos[pageIndex]
            val isCurrentPage = pagerState.currentPage == pageIndex

            Box(modifier = Modifier.fillMaxSize()) {
                // Video Player with ExoPlayer
                VideoPlayerView(
                    videoUrl = video.videoUrl,
                    thumbnailUrl = video.thumbnailUrl,
                    isCurrentPage = isCurrentPage,
                    onDoubleTapLike = {
                        if (!video.isLiked) {
                            onLikeClick(video)
                        }
                    }
                )

                // Bottom Gradient Scrim for readable overlay text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, OverlayDark)
                            )
                        )
                )

                // Right Action Sidebar
                RightActionSidebar(
                    video = video,
                    onCreatorClick = { onCreatorClick(video) },
                    onFollowClick = { onFollowClick(video.creatorUsername, video.isFollowing) },
                    onLikeClick = { onLikeClick(video) },
                    onCommentClick = { onCommentClick(video) },
                    onBookmarkClick = { onBookmarkClick(video) },
                    onShareClick = { onShareClick(video) },
                    onSoundClick = { onSoundClick(video) },
                    isPlaying = isCurrentPage,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(end = 12.dp, bottom = 64.dp)
                )

                // Bottom Info Overlay (Creator, Caption, Sound ticker)
                BottomVideoInfoOverlay(
                    video = video,
                    onCreatorClick = { onCreatorClick(video) },
                    onSoundClick = { onSoundClick(video) },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .navigationBarsPadding()
                        .padding(start = 16.dp, end = 90.dp, bottom = 64.dp)
                )
            }
        }

        // Top Navigation Header (LIVE, Following, For You, Search)
        TopFeedHeader(
            activeTab = activeFeedTab,
            onTabSelected = onTabSelected,
            onSearchClick = onSearchClick,
            onLiveClick = onLiveClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun TopFeedHeader(
    activeTab: FeedTab,
    onTabSelected: (FeedTab) -> Unit,
    onSearchClick: () -> Unit,
    onLiveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LIVE button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x33000000))
                .clickable(onClick = onLiveClick)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("live_feed_button")
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LiveRed)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "LIVE",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Centered Tabs: Mengikuti | Untuk Anda
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            FeedTabItem(
                title = "Mengikuti",
                isSelected = activeTab == FeedTab.FOLLOWING,
                onClick = { onTabSelected(FeedTab.FOLLOWING) }
            )
            Spacer(modifier = Modifier.width(18.dp))
            FeedTabItem(
                title = "Untuk Anda",
                isSelected = activeTab == FeedTab.FOR_YOU,
                onClick = { onTabSelected(FeedTab.FOR_YOU) }
            )
        }

        // Search icon
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier.size(36.dp).testTag("search_feed_button")
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Cari",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun FeedTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextMuted,
            fontSize = if (isSelected) 17.sp else 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

@Composable
private fun RightActionSidebar(
    video: VideoItem,
    onCreatorClick: () -> Unit,
    onFollowClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
    onSoundClick: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Avatar with Follow (+) Button
        Box(contentAlignment = Alignment.BottomCenter) {
            AsyncImage(
                model = video.creatorAvatar,
                contentDescription = video.creatorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, Color.White, CircleShape)
                    .clickable(onClick = onCreatorClick)
            )

            // Follow button
            if (!video.isFollowing && !video.isUserCreated) {
                Box(
                    modifier = Modifier
                        .offset(y = 10.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(TikTokRed)
                        .clickable(onClick = onFollowClick)
                        .testTag("follow_plus_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ikuti Kreator",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else if (video.isFollowing && !video.isUserCreated) {
                Box(
                    modifier = Modifier
                        .offset(y = 10.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onFollowClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Mengikuti",
                        tint = TikTokRed,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Like Button
        val likeScale by animateFloatAsState(
            targetValue = if (video.isLiked) 1.25f else 1.0f,
            animationSpec = tween(150, easing = FastOutSlowInEasing),
            label = "likeScale"
        )
        ActionButtonItem(
            icon = if (video.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            iconTint = if (video.isLiked) TikTokRed else Color.White,
            countText = video.formattedLikes(),
            onClick = onLikeClick,
            testTag = "like_video_button",
            modifier = Modifier.scale(likeScale)
        )

        // Comments Button
        ActionButtonItem(
            icon = Icons.Filled.Comment,
            iconTint = Color.White,
            countText = video.formattedComments(),
            onClick = onCommentClick,
            testTag = "comments_video_button"
        )

        // Bookmark Button
        val bookmarkScale by animateFloatAsState(
            targetValue = if (video.isBookmarked) 1.2f else 1.0f,
            animationSpec = tween(150),
            label = "bookmarkScale"
        )
        ActionButtonItem(
            icon = if (video.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            iconTint = if (video.isBookmarked) TikTokYellow else Color.White,
            countText = video.formattedBookmarks(),
            onClick = onBookmarkClick,
            testTag = "bookmark_video_button",
            modifier = Modifier.scale(bookmarkScale)
        )

        // Share Button
        ActionButtonItem(
            icon = Icons.Filled.Share,
            iconTint = Color.White,
            countText = video.formattedShares(),
            onClick = onShareClick,
            testTag = "share_video_button"
        )

        // Spinning Vinyl Disc
        VinylDisc(
            soundCoverUrl = video.soundCoverUrl,
            isPlaying = isPlaying,
            onClick = onSoundClick,
            modifier = Modifier.testTag("vinyl_sound_disc")
        )
    }
}

@Composable
private fun ActionButtonItem(
    icon: ImageVector,
    iconTint: Color,
    countText: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = countText,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BottomVideoInfoOverlay(
    video: VideoItem,
    onCreatorClick: () -> Unit,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Creator Handle + Verified Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onCreatorClick)
        ) {
            Text(
                text = video.creatorUsername,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (video.isVerified) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = "Terverifikasi",
                    tint = TikTokCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Caption text (expandable on click)
        Text(
            text = video.caption,
            color = Color.White.copy(alpha = 0.95f),
            fontSize = 14.sp,
            maxLines = if (isExpanded) 10 else 2,
            lineHeight = 18.sp,
            modifier = Modifier.clickable { isExpanded = !isExpanded }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Sound ticker info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x33000000))
                .clickable(onClick = onSoundClick)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("sound_ticker")
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
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
