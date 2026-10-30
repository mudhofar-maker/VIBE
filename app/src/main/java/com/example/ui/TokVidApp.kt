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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Bolt
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.BureaucracyFreePolicyDialog
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CreateContentBottomSheet
import com.example.ui.components.CreatorNotificationCenterDialog
import com.example.ui.components.CreatorProfileDialog
import com.example.ui.components.DanaProfileEtalaseDialog
import com.example.ui.components.DanaSawerP2PDialog
import com.example.ui.components.DanaWithdrawalDialog
import com.example.ui.components.GiftBurstOverlay
import com.example.ui.components.ShareBottomSheet
import com.example.ui.components.SoundDetailDialog
import com.example.ui.components.UploadVideoDialog
import com.example.ui.components.VibeCoinWalletDialog
import com.example.ui.components.VibeGiftBottomSheet
import com.example.ui.screens.CreateScreen
import com.example.ui.screens.CreatorDashboardScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InboxScreen
import com.example.ui.screens.LongVideoWatchScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ShortsScreen
import com.example.ui.screens.LiveStreamingScreen
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
    val selectedWatchVideo by viewModel.selectedWatchVideo.collectAsStateWithLifecycle()

    val commentsVideo by viewModel.selectedVideoForComments.collectAsStateWithLifecycle()
    val shareVideo by viewModel.selectedVideoForShare.collectAsStateWithLifecycle()
    val soundVideo by viewModel.selectedVideoForSound.collectAsStateWithLifecycle()
    val creatorVideo by viewModel.selectedCreator.collectAsStateWithLifecycle()
    val currentComments by viewModel.activeComments.collectAsStateWithLifecycle()

    // P2P DANA & Coin States
    val userDanaProfile by viewModel.userDanaProfile.collectAsStateWithLifecycle()
    val userCoins by viewModel.userCoinBalance.collectAsStateWithLifecycle()
    val coinTransactions by viewModel.coinTransactions.collectAsStateWithLifecycle()
    val danaSawerVideo by viewModel.selectedVideoForDanaSawer.collectAsStateWithLifecycle()
    val giftVideo by viewModel.selectedVideoForGift.collectAsStateWithLifecycle()
    val isWalletOpen by viewModel.isWalletDialogOpen.collectAsStateWithLifecycle()
    val isDanaProfileOpen by viewModel.isDanaProfileDialogOpen.collectAsStateWithLifecycle()
    val isPolicyOpen by viewModel.isPolicyDialogOpen.collectAsStateWithLifecycle()
    val isWithdrawalOpen by viewModel.isWithdrawalDialogOpen.collectAsStateWithLifecycle()
    val activeGiftAnimation by viewModel.activeGiftAnimation.collectAsStateWithLifecycle()

    // Creator Dashboard States
    val creatorEarnings by viewModel.creatorEarningsSummary.collectAsStateWithLifecycle()
    val creatorPerformance by viewModel.creatorPerformanceSummary.collectAsStateWithLifecycle()
    val creatorVideos by viewModel.creatorVideos.collectAsStateWithLifecycle()
    val creatorTimeRange by viewModel.creatorTimeRange.collectAsStateWithLifecycle()
    val contentFilter by viewModel.contentFilter.collectAsStateWithLifecycle()
    val contentSearchQuery by viewModel.contentSearchQuery.collectAsStateWithLifecycle()
    val thirtyDayTrend by viewModel.thirtyDayEarningsTrend.collectAsStateWithLifecycle()
    val creatorNotifications by viewModel.creatorNotifications.collectAsStateWithLifecycle()
    val unreadNotificationCount by viewModel.unreadCreatorNotificationCount.collectAsStateWithLifecycle()
    val isNotificationCenterOpen by viewModel.isNotificationCenterOpen.collectAsStateWithLifecycle()

    val commentsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val giftSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showCreateOptionsSheet by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var uploadInitialIsLongVideo by remember { mutableStateOf(false) }
    var isDirectLiveStreamingActive by remember { mutableStateOf(false) }

    // Handle back button for sub-screens
    BackHandler(enabled = currentTab != NavigationTab.HOME || isDirectLiveStreamingActive) {
        if (isDirectLiveStreamingActive) {
            isDirectLiveStreamingActive = false
        } else if (currentTab == NavigationTab.WATCH) {
            viewModel.closeWatchVideo()
        } else if (currentTab == NavigationTab.CREATOR_DASHBOARD) {
            viewModel.setNavTab(NavigationTab.PROFILE)
        } else {
            viewModel.setNavTab(NavigationTab.HOME)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main Screens Router
        when (currentTab) {
            NavigationTab.HOME -> {
                HomeScreen(
                    videos = allVideos,
                    onOpenWatchVideo = { video -> viewModel.openWatchVideo(video) },
                    onOpenShorts = { video -> viewModel.openShorts(video) },
                    onSearchClick = { viewModel.setNavTab(NavigationTab.DISCOVER) },
                    onNotificationClick = { viewModel.openNotificationCenter() },
                    onProfileClick = { viewModel.setNavTab(NavigationTab.PROFILE) }
                )
            }

            NavigationTab.SHORTS -> {
                ShortsScreen(
                    videos = feedVideos,
                    activeFeedTab = feedTab,
                    onTabSelected = { viewModel.setFeedTab(it) },
                    onSearchClick = { viewModel.setNavTab(NavigationTab.DISCOVER) },
                    onLiveClick = {
                        isDirectLiveStreamingActive = true
                    },
                    onLikeClick = {
                        viewModel.toggleLike(it)
                        if (!it.isLiked) {
                            Toast.makeText(context, "❤️ Disukai!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onBookmarkClick = {
                        viewModel.toggleBookmark(it)
                        if (!it.isBookmarked) {
                            Toast.makeText(context, "🔖 Disimpan ke favorit", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onCommentClick = { viewModel.openComments(it) },
                    onShareClick = { viewModel.openShare(it) },
                    onSoundClick = { viewModel.openSound(it) },
                    onCreatorClick = { viewModel.openCreatorProfile(it) },
                    onFollowClick = { username, status -> viewModel.toggleFollow(username, status) },
                    onDanaSawerClick = { viewModel.openDanaSawer(it) },
                    onGiftClick = { viewModel.openGiftSheet(it) }
                )
            }

            NavigationTab.WATCH -> {
                if (selectedWatchVideo != null) {
                    LongVideoWatchScreen(
                        video = selectedWatchVideo!!,
                        relatedVideos = allVideos.filter { it.isLongVideo },
                        comments = currentComments,
                        onBackClick = { viewModel.closeWatchVideo() },
                        onVideoSelected = { video -> viewModel.openWatchVideo(video) },
                        onLikeClick = {
                            viewModel.toggleLike(it)
                            if (!it.isLiked) {
                                Toast.makeText(context, "❤️ Disukai!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onBookmarkClick = {
                            viewModel.toggleBookmark(it)
                            if (!it.isBookmarked) {
                                Toast.makeText(context, "🔖 Disimpan ke favorit", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onShareClick = { viewModel.openShare(it) },
                        onAddComment = { text ->
                            viewModel.addComment(selectedWatchVideo!!.id, text)
                            Toast.makeText(context, "💬 Komentar terkirim", Toast.LENGTH_SHORT).show()
                        },
                        onDanaSawerClick = { viewModel.openDanaSawer(it) },
                        onGiftClick = { viewModel.openGiftSheet(it) }
                    )
                } else {
                    viewModel.setNavTab(NavigationTab.HOME)
                }
            }

            NavigationTab.DISCOVER -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 76.dp)
                ) {
                    ExploreScreen(
                        videos = allVideos,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onCategoryChange = { viewModel.setSelectedCategory(it) },
                        onVideoSelected = { video ->
                            if (video.isLongVideo) {
                                viewModel.openWatchVideo(video)
                            } else {
                                viewModel.openShorts(video)
                            }
                        }
                    )
                }
            }

            NavigationTab.CREATE -> {
                CreateScreen(
                    onClose = { viewModel.setNavTab(NavigationTab.HOME) },
                    onPublishVideo = { caption, tags, sound, category, isLongVideo, durationFormatted ->
                        viewModel.createVideo(caption, tags, sound, category, isLongVideo, durationFormatted)
                        val msg = if (isLongVideo) "Video Panjang berhasil dipublikasikan!" else "Short berhasil dipublikasikan!"
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                )
            }

            NavigationTab.INBOX -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 76.dp)
                ) {
                    InboxScreen(notifications = viewModel.notifications)
                }
            }

            NavigationTab.PROFILE -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 76.dp)
                ) {
                    ProfileScreen(
                        videos = allVideos,
                        userDanaProfile = userDanaProfile,
                        userCoins = userCoins,
                        onVideoClick = { video ->
                            if (video.isLongVideo) {
                                viewModel.openWatchVideo(video)
                            } else {
                                viewModel.openShorts(video)
                            }
                        },
                        onOpenDanaEtalase = { viewModel.openDanaProfileDialog() },
                        onOpenWallet = { viewModel.openWalletDialog() },
                        onOpenPolicy = { viewModel.openPolicyDialog() },
                        onOpenWithdrawal = { viewModel.openWithdrawalDialog() },
                        onOpenCreatorDashboard = { viewModel.setNavTab(NavigationTab.CREATOR_DASHBOARD) }
                    )
                }
            }

            NavigationTab.CREATOR_DASHBOARD -> {
                CreatorDashboardScreen(
                    earningsSummary = creatorEarnings,
                    performanceSummary = creatorPerformance,
                    creatorVideos = creatorVideos,
                    userDanaProfile = userDanaProfile,
                    selectedTimeRange = creatorTimeRange,
                    selectedFilterType = contentFilter,
                    searchQuery = contentSearchQuery,
                    thirtyDayTrend = thirtyDayTrend,
                    onTimeRangeSelected = { viewModel.setCreatorTimeRange(it) },
                    onFilterSelected = { viewModel.setContentFilter(it) },
                    onSearchQueryChange = { viewModel.setContentSearchQuery(it) },
                    onVideoClick = { video ->
                        if (video.isLongVideo) {
                            viewModel.openWatchVideo(video)
                        } else {
                            viewModel.openShorts(video)
                        }
                    },
                    onDeleteVideo = { viewModel.deleteVideo(it) },
                    onUpdateVideo = { id, caption, tags -> viewModel.updateVideo(id, caption, tags) },
                    onOpenWithdrawal = { viewModel.openWithdrawalDialog() },
                    onOpenUploadNewVideo = {
                        uploadInitialIsLongVideo = false
                        showUploadDialog = true
                    },
                    onOpenNotificationCenter = { viewModel.openNotificationCenter() },
                    unreadNotificationCount = unreadNotificationCount,
                    onBackClick = { viewModel.setNavTab(NavigationTab.PROFILE) }
                )
            }
        }

        // Floating Bottom Navigation Bar (Hidden during full-screen player, recording, or creator studio)
        if (currentTab != NavigationTab.CREATE && currentTab != NavigationTab.WATCH && currentTab != NavigationTab.CREATOR_DASHBOARD) {
            TokVidBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setNavTab(it) },
                onCreateClick = { showCreateOptionsSheet = true },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Create Options Bottom Sheet (Triggered by the (+) Button)
        if (showCreateOptionsSheet) {
            CreateContentBottomSheet(
                onDismiss = { showCreateOptionsSheet = false },
                onUploadVideoClick = {
                    showCreateOptionsSheet = false
                    viewModel.setNavTab(NavigationTab.CREATE)
                },
                onRecordShortsClick = {
                    showCreateOptionsSheet = false
                    viewModel.setNavTab(NavigationTab.CREATE)
                },
                onUploadLongVideoClick = {
                    showCreateOptionsSheet = false
                    uploadInitialIsLongVideo = true
                    showUploadDialog = true
                },
                onGoLiveClick = {
                    showCreateOptionsSheet = false
                    isDirectLiveStreamingActive = true
                }
            )
        }

        // Upload Video Dialog
        if (showUploadDialog) {
            UploadVideoDialog(
                initialIsLongVideo = uploadInitialIsLongVideo,
                onDismiss = { showUploadDialog = false },
                onPublish = { caption, tags, soundTitle, category, isLongVideo, durationFormatted, videoUrl, thumbnailUrl ->
                    viewModel.createVideo(
                        caption = caption,
                        tags = tags,
                        soundTitle = soundTitle,
                        category = category,
                        isLongVideo = isLongVideo,
                        durationFormatted = durationFormatted,
                        videoUrl = videoUrl,
                        thumbnailUrl = thumbnailUrl
                    )
                }
            )
        }

        // Comments Bottom Sheet (for Shorts)
        if (commentsVideo != null && currentTab == NavigationTab.SHORTS) {
            CommentsBottomSheet(
                sheetState = commentsSheetState,
                comments = currentComments,
                totalCommentsCount = commentsVideo!!.commentsCount,
                onDismiss = { viewModel.closeComments() },
                onSendComment = { text ->
                    viewModel.addComment(commentsVideo!!.id, text)
                    Toast.makeText(context, "💬 Komentar terkirim!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Share Bottom Sheet
        if (shareVideo != null && currentTab != NavigationTab.WATCH) {
            ShareBottomSheet(
                sheetState = shareSheetState,
                video = shareVideo!!,
                onShareAction = {
                    viewModel.incrementCoinsForShare(shareVideo!!.creatorUsername)
                    Toast.makeText(context, "↗️ Video berhasil dibagikan!", Toast.LENGTH_SHORT).show()
                },
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
                },
                onDanaSawerClick = { viewModel.openDanaSawer(it) },
                onGiftClick = { viewModel.openGiftSheet(it) }
            )
        }

        // P2P DANA Sawer Dialog (Direct Barcode Scan & Transfer)
        if (danaSawerVideo != null) {
            DanaSawerP2PDialog(
                video = danaSawerVideo!!,
                onConfirmSent = { nominal ->
                    viewModel.confirmP2PDanaSent(danaSawerVideo!!.creatorName, nominal)
                    viewModel.closeDanaSawer()
                },
                onDismiss = { viewModel.closeDanaSawer() }
            )
        }

        // Internal VibeGift Bottom Sheet
        if (giftVideo != null) {
            VibeGiftBottomSheet(
                sheetState = giftSheetState,
                video = giftVideo!!,
                userCoinBalance = userCoins,
                onSendGift = { gift ->
                    viewModel.sendGift(giftVideo!!, gift)
                },
                onOpenWallet = {
                    viewModel.closeGiftSheet()
                    viewModel.openWalletDialog()
                },
                onDismiss = { viewModel.closeGiftSheet() }
            )
        }

        // Dompet Koin Mandiri Dialog
        if (isWalletOpen) {
            VibeCoinWalletDialog(
                currentCoins = userCoins,
                transactions = coinTransactions,
                onRechargeCoins = { amount, label -> viewModel.rechargeCoins(amount, label) },
                onConvertCoins = { count, onDone -> viewModel.convertCoins(count, onDone) },
                onClaimDailyReward = { viewModel.claimDailyReward() },
                onRedeemVoucher = { code ->
                    var success = false
                    viewModel.redeemVoucher(code) { success = it }
                    success
                },
                onOpenPolicy = {
                    viewModel.closeWalletDialog()
                    viewModel.openPolicyDialog()
                },
                onOpenWithdrawal = {
                    viewModel.closeWalletDialog()
                    viewModel.openWithdrawalDialog()
                },
                onDismiss = { viewModel.closeWalletDialog() }
            )
        }

        // Personal DANA QR Profile Showcase Dialog
        if (isDanaProfileOpen) {
            DanaProfileEtalaseDialog(
                initialProfile = userDanaProfile,
                onSaveProfile = { phone, name, qrUrl, notes, active ->
                    viewModel.saveUserDanaProfile(phone, name, qrUrl, notes, active)
                },
                onDismiss = { viewModel.closeDanaProfileDialog() }
            )
        }

        // Bureaucracy Free Policy Dialog
        if (isPolicyOpen) {
            BureaucracyFreePolicyDialog(
                onDismiss = { viewModel.closePolicyDialog() }
            )
        }

        // Tool Tarik ke DANA Sendiri Dialog (Hasil Sawer / Apresiasi & Koin Mandiri)
        if (isWithdrawalOpen) {
            DanaWithdrawalDialog(
                userDanaProfile = userDanaProfile ?: com.example.data.model.UserDanaProfile(),
                currentCoins = userCoins,
                onWithdrawSawerBalance = { amountRupiah, targetPhone, targetName, onResult ->
                    viewModel.withdrawFromSawerBalanceToDana(amountRupiah, targetPhone, targetName, onResult)
                },
                onWithdrawCoins = { coinCount, targetPhone, targetName, onResult ->
                    viewModel.withdrawCoinsToDana(coinCount, targetPhone, targetName, onResult)
                },
                onDismiss = { viewModel.closeWithdrawalDialog() }
            )
        }

        // Creator Notification Center Dialog
        if (isNotificationCenterOpen) {
            CreatorNotificationCenterDialog(
                notifications = creatorNotifications,
                onMarkAllAsRead = { viewModel.markAllCreatorNotificationsAsRead() },
                onSimulateAlert = { callback -> viewModel.simulateRealtimeFanAlert(callback) },
                onDismiss = { viewModel.closeNotificationCenter() }
            )
        }

        // Fullscreen Floating Gift Burst Celebration Overlay
        GiftBurstOverlay(
            animation = activeGiftAnimation,
            onFinished = { viewModel.clearGiftAnimation() }
        )

        // Fullscreen Live Streaming Studio Screen
        if (isDirectLiveStreamingActive) {
            LiveStreamingScreen(
                onEndLive = { isDirectLiveStreamingActive = false },
                onSawerReceived = { amount -> viewModel.incrementDirectSawerCount(amount) },
                onCoinReceived = { coins -> viewModel.rechargeCoins(coins, "Hadiah Siaran Langsung") }
            )
        }
    }
}

@Composable
fun TokVidBottomNavBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    onCreateClick: () -> Unit = { onTabSelected(NavigationTab.CREATE) },
    modifier: Modifier = Modifier
) {
    val navBackgroundModifier = if (currentTab == NavigationTab.SHORTS) {
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
        if (currentTab != NavigationTab.SHORTS) {
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
            // Home / Beranda
            NavIconItem(
                label = "Beranda",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                isSelected = currentTab == NavigationTab.HOME,
                onClick = { onTabSelected(NavigationTab.HOME) },
                testTag = "nav_home"
            )

            // Shorts
            NavIconItem(
                label = "Shorts",
                selectedIcon = Icons.Filled.Bolt,
                unselectedIcon = Icons.Outlined.Bolt,
                isSelected = currentTab == NavigationTab.SHORTS,
                onClick = { onTabSelected(NavigationTab.SHORTS) },
                testTag = "nav_shorts"
            )

            // Central TikTok Plus Button
            TikTokPlusCreateButton(
                onClick = onCreateClick
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
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = if (isSelected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    tint = if (isSelected) TextPrimary else TextMuted,
                    modifier = Modifier.size(24.dp)
                )

                // Optional Red Dot Badge
                if (showBadge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 3.dp, y = (-2).dp)
                            .size(7.dp)
                            .background(LiveRed, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                color = if (isSelected) TextPrimary else TextMuted,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun TikTokPlusCreateButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 54.dp, height = 48.dp)
            .clickable(onClick = onClick)
            .testTag("nav_create_button"),
        contentAlignment = Alignment.Center
    ) {
        // Left Cyan Underlay
        Box(
            modifier = Modifier
                .offset(x = (-3).dp)
                .size(width = 38.dp, height = 28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TikTokCyan)
        )

        // Right Red Underlay
        Box(
            modifier = Modifier
                .offset(x = 3.dp)
                .size(width = 38.dp, height = 28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(TikTokRed)
        )

        // Center White Foreground
        Box(
            modifier = Modifier
                .size(width = 38.dp, height = 28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Buat Konten Baru atau Upload Video",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
