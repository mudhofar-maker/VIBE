package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VideoItem
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CreatorProfileDialog
import com.example.ui.components.ShareBottomSheet
import com.example.ui.components.SoundDetailDialog
import com.example.ui.screens.CreateScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InboxScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.viewmodel.FeedTab
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokVidApp(viewModel: MainViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentNavTab.collectAsStateWithLifecycle()
    val feedTab by viewModel.feedTab.collectAsStateWithLifecycle()
    val feedVideos by viewModel.displayFeedVideos.collectAsStateWithLifecycle()
    val allVideos by viewModel.allVideos.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val commentsVideo by viewModel.selectedVideoForComments.collectAsStateWithLifecycle()
    val shareVideo by viewModel.selectedVideoForShare.collectAsStateWithLifecycle()
    val soundVideo by viewModel.selectedVideoForSound.collectAsStateWithLifecycle()
    val creatorVideo by viewModel.selectedCreator.collectAsStateWithLifecycle()
    val currentComments by viewModel.activeComments.collectAsStateWithLifecycle()

    val commentsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle back button for sub-screens
    BackHandler(enabled = currentTab != NavigationTab.HOME) {
        viewModel.setNavTab(NavigationTab.HOME)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main Screens - HomeScreen runs completely full-screen edge-to-edge
        when (currentTab) {
            NavigationTab.HOME -> {
                HomeScreen(
                    videos = feedVideos,
                    activeFeedTab = feedTab,
                    onTabSelected = { viewModel.setFeedTab(it) },
                    onSearchClick = { viewModel.setNavTab(NavigationTab.DISCOVER) },
                    onLiveClick = {
                        Toast.makeText(context, "Menghubungkan ke siaran langsung MyFlix...", Toast.LENGTH_SHORT).show()
                    },
                    onLikeClick = { viewModel.toggleLike(it) },
                    onBookmarkClick = { viewModel.toggleBookmark(it) },
                    onCommentClick = { viewModel.openComments(it) },
                    onShareClick = { viewModel.openShare(it) },
                    onSoundClick = { viewModel.openSound(it) },
                    onCreatorClick = { viewModel.openCreatorProfile(it) },
                    onFollowClick = { username, status -> viewModel.toggleFollow(username, status) }
                )
            }

            NavigationTab.DISCOVER -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    ExploreScreen(
                        videos = allVideos,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onCategoryChange = { viewModel.setSelectedCategory(it) },
                        onVideoSelected = { video ->
                            viewModel.setNavTab(NavigationTab.HOME)
                            viewModel.setFeedTab(FeedTab.FOR_YOU)
                        }
                    )
                }
            }

            NavigationTab.CREATE -> {
                CreateScreen(
                    onClose = { viewModel.setNavTab(NavigationTab.HOME) },
                    onPublishVideo = { caption, tags, sound, category ->
                        viewModel.createVideo(caption, tags, sound, category)
                        Toast.makeText(context, "Video berhasil dipublikasikan!", Toast.LENGTH_LONG).show()
                    }
                )
            }

            NavigationTab.INBOX -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    InboxScreen(notifications = viewModel.notifications)
                }
            }

            NavigationTab.PROFILE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp)
                ) {
                    ProfileScreen(
                        videos = allVideos,
                        onVideoClick = {
                            viewModel.setNavTab(NavigationTab.HOME)
                        }
                    )
                }
            }
        }

        // Floating Bottom Navigation Bar overlaid smoothly
        if (currentTab != NavigationTab.CREATE) {
            TokVidBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setNavTab(it) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Comments Bottom Sheet
        if (commentsVideo != null) {
            CommentsBottomSheet(
                sheetState = commentsSheetState,
                comments = currentComments,
                totalCommentsCount = commentsVideo!!.commentsCount,
                onDismiss = { viewModel.closeComments() },
                onSendComment = { text ->
                    viewModel.addComment(commentsVideo!!.id, text)
                    Toast.makeText(context, "Komentar terkirim!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Share Bottom Sheet
        if (shareVideo != null) {
            ShareBottomSheet(
                sheetState = shareSheetState,
                video = shareVideo!!,
                onDismiss = { viewModel.closeShare() }
            )
        }

        // Sound Detail Dialog
        if (soundVideo != null) {
            SoundDetailDialog(
                video = soundVideo!!,
                onDismiss = { viewModel.closeSound() },
                onUseSound = {
                    viewModel.closeSound()
                    viewModel.setNavTab(NavigationTab.CREATE)
                }
            )
        }

        // Creator Profile Dialog
        if (creatorVideo != null) {
            CreatorProfileDialog(
                video = creatorVideo!!,
                onDismiss = { viewModel.closeCreatorProfile() },
                onToggleFollow = {
                    viewModel.toggleFollow(creatorVideo!!.creatorUsername, creatorVideo!!.isFollowing)
                }
            )
        }
    }
}

@Composable
fun TokVidBottomNavBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val navBackgroundModifier = if (currentTab == NavigationTab.HOME) {
        Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x66000000),
                    Color(0xCC000000)
                )
            )
        )
    } else {
        Modifier.background(DarkBackground)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(navBackgroundModifier)
            .navigationBarsPadding()
    ) {
        if (currentTab != NavigationTab.HOME) {
            HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            NavIconItem(
                label = "Beranda",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                isSelected = currentTab == NavigationTab.HOME,
                onClick = { onTabSelected(NavigationTab.HOME) },
                testTag = "nav_home"
            )

            // Discover
            NavIconItem(
                label = "Teman",
                selectedIcon = Icons.Filled.Explore,
                unselectedIcon = Icons.Outlined.Explore,
                isSelected = currentTab == NavigationTab.DISCOVER,
                onClick = { onTabSelected(NavigationTab.DISCOVER) },
                testTag = "nav_discover"
            )

            // Central TikTok Plus Button
            TikTokPlusCreateButton(
                onClick = { onTabSelected(NavigationTab.CREATE) }
            )

            // Inbox with red notification badge
            NavIconItem(
                label = "Kotak Masuk",
                selectedIcon = Icons.Filled.ChatBubble,
                unselectedIcon = Icons.Outlined.ChatBubbleOutline,
                isSelected = currentTab == NavigationTab.INBOX,
                onClick = { onTabSelected(NavigationTab.INBOX) },
                showBadge = true,
                testTag = "nav_inbox"
            )

            // Profile
            NavIconItem(
                label = "Profil",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.PersonOutline,
                isSelected = currentTab == NavigationTab.PROFILE,
                onClick = { onTabSelected(NavigationTab.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
private fun NavIconItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    showBadge: Boolean = false,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = if (isSelected) Color.White else TextMuted,
                modifier = Modifier.size(24.dp)
            )
            if (showBadge) {
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = (-2).dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(LiveRed)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextMuted
        )
    }
}

@Composable
private fun TikTokPlusCreateButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
            .testTag("nav_create_button"),
        contentAlignment = Alignment.Center
    ) {
        // Cyan and Pink background layer shadows
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 30.dp)
                .offset(x = (-3).dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TikTokCyan)
        )
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 30.dp)
                .offset(x = 3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TikTokRed)
        )
        // White center pill with black plus icon
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Buat Video",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
