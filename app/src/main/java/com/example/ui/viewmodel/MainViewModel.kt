package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TokVidDatabase
import com.example.data.model.CoinConversionCalculation
import com.example.data.model.CommentItem
import com.example.data.model.DanaWithdrawalResult
import com.example.data.model.ContentFilterType
import com.example.data.model.CreatorEarningsSummary
import com.example.data.model.CreatorNotification
import com.example.data.model.CreatorNotificationCategory
import com.example.data.model.CreatorNotificationType
import com.example.data.model.CreatorPerformanceSummary
import com.example.data.model.CreatorTimeRange
import com.example.data.model.DailyMetricPoint
import com.example.data.model.FanInteractionEconomics
import com.example.data.model.NotificationItem
import com.example.data.model.UserDanaProfile
import com.example.data.model.VibeGift
import com.example.data.model.VideoItem
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FeedTab {
    FOLLOWING, FOR_YOU
}

enum class NavigationTab {
    HOME, SHORTS, DISCOVER, CREATE, INBOX, PROFILE, WATCH, CREATOR_DASHBOARD
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VideoRepository = run {
        val db = TokVidDatabase.getDatabase(application)
        VideoRepository(db.tokVidDao())
    }

    private val _currentNavTab = MutableStateFlow(NavigationTab.HOME)
    val currentNavTab: StateFlow<NavigationTab> = _currentNavTab.asStateFlow()

    private val _feedTab = MutableStateFlow(FeedTab.FOR_YOU)
    val feedTab: StateFlow<FeedTab> = _feedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedWatchVideo = MutableStateFlow<VideoItem?>(null)
    val selectedWatchVideo: StateFlow<VideoItem?> = _selectedWatchVideo.asStateFlow()

    private val _selectedVideoForComments = MutableStateFlow<VideoItem?>(null)
    val selectedVideoForComments: StateFlow<VideoItem?> = _selectedVideoForComments.asStateFlow()

    private val _selectedVideoForShare = MutableStateFlow<VideoItem?>(null)
    val selectedVideoForShare: StateFlow<VideoItem?> = _selectedVideoForShare.asStateFlow()

    private val _selectedVideoForSound = MutableStateFlow<VideoItem?>(null)
    val selectedVideoForSound: StateFlow<VideoItem?> = _selectedVideoForSound.asStateFlow()

    private val _selectedCreator = MutableStateFlow<VideoItem?>(null)
    val selectedCreator: StateFlow<VideoItem?> = _selectedCreator.asStateFlow()

    // --- P2P DANA & Coin System Dialog States ---
    private val _selectedVideoForDanaSawer = MutableStateFlow<VideoItem?>(null)
    val selectedVideoForDanaSawer: StateFlow<VideoItem?> = _selectedVideoForDanaSawer.asStateFlow()

    private val _selectedVideoForGift = MutableStateFlow<VideoItem?>(null)
    val selectedVideoForGift: StateFlow<VideoItem?> = _selectedVideoForGift.asStateFlow()

    private val _isWalletDialogOpen = MutableStateFlow(false)
    val isWalletDialogOpen: StateFlow<Boolean> = _isWalletDialogOpen.asStateFlow()

    private val _isDanaProfileDialogOpen = MutableStateFlow(false)
    val isDanaProfileDialogOpen: StateFlow<Boolean> = _isDanaProfileDialogOpen.asStateFlow()

    private val _isPolicyDialogOpen = MutableStateFlow(false)
    val isPolicyDialogOpen: StateFlow<Boolean> = _isPolicyDialogOpen.asStateFlow()

    private val _isWithdrawalDialogOpen = MutableStateFlow(false)
    val isWithdrawalDialogOpen: StateFlow<Boolean> = _isWithdrawalDialogOpen.asStateFlow()

    private val _activeGiftAnimation = MutableStateFlow<com.example.data.model.VibeGiftAnimation?>(null)
    val activeGiftAnimation: StateFlow<com.example.data.model.VibeGiftAnimation?> = _activeGiftAnimation.asStateFlow()

    val userDanaProfile: StateFlow<com.example.data.model.UserDanaProfile> = repository.getUserDanaProfile()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            com.example.data.model.UserDanaProfile(
                id = "primary_user_dana",
                danaPhoneNumber = "0812-9876-5432",
                danaAccountName = "ALDI PRASETYA",
                qrCustomImageUrl = null,
                notes = "Dukung karya konten saya! Saweran langsung P2P via DANA 100% tanpa potongan.",
                isDanaActive = true,
                totalDirectSawerCount = 14
            )
        )

    val userCoinBalance: StateFlow<Long> = repository.getUserCoinWallet()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 500L)

    val coinTransactions: StateFlow<List<com.example.data.local.CoinTransactionEntity>> = repository.getAllCoinTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVideos: StateFlow<List<VideoItem>> = repository.allVideosFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val displayFeedVideos: StateFlow<List<VideoItem>> = combine(
        allVideos,
        _feedTab
    ) { list, tab ->
        when (tab) {
            FeedTab.FOR_YOU -> list
            FeedTab.FOLLOWING -> {
                val followed = list.filter { it.isFollowing }
                if (followed.isEmpty()) list.take(2) else followed
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Creator Dashboard States
    private val _creatorTimeRange = MutableStateFlow(CreatorTimeRange.LAST_7_DAYS)
    val creatorTimeRange: StateFlow<CreatorTimeRange> = _creatorTimeRange.asStateFlow()

    private val _contentFilter = MutableStateFlow(ContentFilterType.ALL)
    val contentFilter: StateFlow<ContentFilterType> = _contentFilter.asStateFlow()

    private val _contentSearchQuery = MutableStateFlow("")
    val contentSearchQuery: StateFlow<String> = _contentSearchQuery.asStateFlow()

    val creatorVideos: StateFlow<List<VideoItem>> = combine(
        allVideos,
        _contentFilter,
        _contentSearchQuery
    ) { videos, filter, query ->
        val creatorOwned = videos.filter { it.isUserCreated || it.creatorUsername == "@saya" }
        val sourceList = if (creatorOwned.isNotEmpty()) creatorOwned else videos.take(8)

        sourceList.filter { video ->
            val matchesFilter = when (filter) {
                ContentFilterType.ALL -> true
                ContentFilterType.SHORTS -> !video.isLongVideo
                ContentFilterType.LONG_VIDEO -> video.isLongVideo
            }
            val matchesQuery = query.isBlank() ||
                video.caption.contains(query, ignoreCase = true) ||
                video.category.contains(query, ignoreCase = true) ||
                video.tags.any { it.contains(query, ignoreCase = true) }

            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val creatorEarningsSummary: StateFlow<CreatorEarningsSummary> = combine(
        userDanaProfile,
        userCoinBalance,
        allVideos,
        _creatorTimeRange
    ) { profile, coins, videos, range ->
        val sawerDirect = profile?.sawerBalanceRupiah ?: 175_000.0
        val creatorOwned = videos.filter { it.isUserCreated || it.creatorUsername == "@saya" }
        val effectiveVideos = if (creatorOwned.isNotEmpty()) creatorOwned else videos.take(8)
        val totalInteractions = effectiveVideos.sumOf { it.totalFanInteractionsCount } + 1850L
        val fanRoyalty = totalInteractions * FanInteractionEconomics.PRICE_PER_INTERACTION_RUPIAH
        val coinGiftValue = (coins * 8000.0).coerceAtLeast(40_000.0)

        val rangeMultiplier = when (range) {
            CreatorTimeRange.LAST_7_DAYS -> 0.45
            CreatorTimeRange.LAST_30_DAYS -> 0.85
            CreatorTimeRange.ALL_TIME -> 1.0
        }

        val totalGross = (sawerDirect + fanRoyalty + coinGiftValue) * rangeMultiplier
        val directSawerSegment = sawerDirect * rangeMultiplier
        val fanRoyaltySegment = fanRoyalty * rangeMultiplier
        val coinGiftSegment = coinGiftValue * rangeMultiplier

        CreatorEarningsSummary(
            totalEarningsRupiah = totalGross,
            directSawerRupiah = directSawerSegment,
            fanRoyaltyRupiah = fanRoyaltySegment,
            coinGiftRupiah = coinGiftSegment,
            withdrawableRupiah = sawerDirect,
            growthRatePercent = if (range == CreatorTimeRange.LAST_7_DAYS) 24.8 else 18.5,
            totalSawerTransactions = profile?.totalDirectSawerCount ?: 14L
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreatorEarningsSummary(245_000.0, 175_000.0, 30_000.0, 40_000.0, 175_000.0))

    val thirtyDayEarningsTrend: StateFlow<com.example.data.model.ThirtyDayEarningsTrend> = userDanaProfile.map { profile ->
        com.example.data.model.ThirtyDayEarningsTrend.generateDefault(profile?.sawerBalanceRupiah ?: 175_000.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.model.ThirtyDayEarningsTrend.generateDefault())

    val creatorPerformanceSummary: StateFlow<CreatorPerformanceSummary> = combine(
        allVideos,
        _creatorTimeRange
    ) { videos, range ->
        val creatorOwned = videos.filter { it.isUserCreated || it.creatorUsername == "@saya" }
        val effectiveVideos = if (creatorOwned.isNotEmpty()) creatorOwned else videos.take(8)

        val totalViews = effectiveVideos.sumOf { it.viewsCount } + 12_400L
        val totalLikes = effectiveVideos.sumOf { it.likesCount } + 1_820L
        val totalComments = effectiveVideos.sumOf { it.commentsCount } + 340L
        val totalShares = effectiveVideos.sumOf { it.sharesCount } + 210L
        val totalBookmarks = effectiveVideos.sumOf { it.bookmarksCount } + 450L
        val totalEngagement = totalLikes + totalComments + totalShares + totalBookmarks
        val engRate = if (totalViews > 0) (totalEngagement.toDouble() / totalViews.toDouble()) * 100.0 else 8.4

        val dailyTrend = when (range) {
            CreatorTimeRange.LAST_7_DAYS -> listOf(
                DailyMetricPoint("Sen", 1820L, 25000.0, 142L),
                DailyMetricPoint("Sel", 2400L, 35000.0, 198L),
                DailyMetricPoint("Rab", 3100L, 42000.0, 260L),
                DailyMetricPoint("Kam", 2890L, 38000.0, 215L),
                DailyMetricPoint("Jum", 4200L, 55000.0, 380L),
                DailyMetricPoint("Sab", 5600L, 72000.0, 490L),
                DailyMetricPoint("Min", 6100L, 85000.0, 560L)
            )
            CreatorTimeRange.LAST_30_DAYS -> listOf(
                DailyMetricPoint("Mgg 1", 14200L, 180000.0, 1200L),
                DailyMetricPoint("Mgg 2", 18900L, 240000.0, 1650L),
                DailyMetricPoint("Mgg 3", 22400L, 310000.0, 2100L),
                DailyMetricPoint("Mgg 4", 28100L, 395000.0, 2750L)
            )
            CreatorTimeRange.ALL_TIME -> listOf(
                DailyMetricPoint("Jan", 45000L, 620000.0, 4200L),
                DailyMetricPoint("Feb", 68000L, 890000.0, 6100L),
                DailyMetricPoint("Mar", 92000L, 1250000.0, 8900L),
                DailyMetricPoint("Apr", 134000L, 1850000.0, 12400L)
            )
        }

        CreatorPerformanceSummary(
            totalViews = (totalViews * if (range == CreatorTimeRange.LAST_7_DAYS) 0.35 else 1.0).toLong(),
            totalLikes = (totalLikes * if (range == CreatorTimeRange.LAST_7_DAYS) 0.35 else 1.0).toLong(),
            totalComments = (totalComments * if (range == CreatorTimeRange.LAST_7_DAYS) 0.35 else 1.0).toLong(),
            totalShares = (totalShares * if (range == CreatorTimeRange.LAST_7_DAYS) 0.35 else 1.0).toLong(),
            totalBookmarks = (totalBookmarks * if (range == CreatorTimeRange.LAST_7_DAYS) 0.35 else 1.0).toLong(),
            engagementRate = engRate,
            avgWatchTimeSeconds = 54,
            totalFollowers = 1842L,
            dailyTrends = dailyTrend
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreatorPerformanceSummary(28000L, 3400L, 520L, 310L, 680L, 9.2))

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeComments: StateFlow<List<CommentItem>> = _selectedVideoForComments.flatMapLatest { video ->
        if (video != null) repository.getCommentsForVideo(video.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: List<NotificationItem> = repository.getNotifications()

    val creatorNotifications: StateFlow<List<CreatorNotification>> = repository.creatorNotificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCreatorNotificationCount: StateFlow<Int> = creatorNotifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)

    private val _isNotificationCenterOpen = MutableStateFlow(false)
    val isNotificationCenterOpen: StateFlow<Boolean> = _isNotificationCenterOpen.asStateFlow()

    fun openNotificationCenter() {
        _isNotificationCenterOpen.value = true
    }

    fun closeNotificationCenter() {
        _isNotificationCenterOpen.value = false
    }

    fun markAllCreatorNotificationsAsRead() {
        repository.markAllNotificationsAsRead()
    }

    fun simulateRealtimeFanAlert(onResult: ((CreatorNotification) -> Unit)? = null) {
        viewModelScope.launch {
            val notif = repository.simulateRealtimeFanAlert()
            onResult?.invoke(notif)
        }
    }

    fun setNavTab(tab: NavigationTab) {
        _currentNavTab.value = tab
    }

    fun setFeedTab(tab: FeedTab) {
        _feedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun openComments(video: VideoItem) {
        _selectedVideoForComments.value = video
    }

    fun closeComments() {
        _selectedVideoForComments.value = null
    }

    fun openShare(video: VideoItem) {
        _selectedVideoForShare.value = video
    }

    fun closeShare() {
        _selectedVideoForShare.value = null
    }

    fun openSound(video: VideoItem) {
        _selectedVideoForSound.value = video
    }

    fun closeSound() {
        _selectedVideoForSound.value = null
    }

    fun openCreatorProfile(video: VideoItem) {
        _selectedCreator.value = video
    }

    fun closeCreatorProfile() {
        _selectedCreator.value = null
    }

    fun toggleLike(video: VideoItem) {
        viewModelScope.launch {
            repository.toggleLike(video)
            if (!video.isLiked) {
                repository.rewardCoinForInteraction("Reward menyukai video ${video.creatorUsername}", 5L)
            }
        }
    }

    fun toggleBookmark(video: VideoItem) {
        viewModelScope.launch {
            repository.toggleBookmark(video)
            if (!video.isBookmarked) {
                incrementCoinsForBookmark(5L)
            }
        }
    }

    fun toggleFollow(creatorUsername: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFollow(creatorUsername, currentStatus)
            if (!currentStatus) {
                incrementCoinsForFollow(creatorUsername, 5L)
            }
        }
    }

    fun openWatchVideo(video: VideoItem) {
        _selectedWatchVideo.value = video
        _currentNavTab.value = NavigationTab.WATCH
    }

    fun closeWatchVideo() {
        _selectedWatchVideo.value = null
        _currentNavTab.value = NavigationTab.HOME
    }

    fun openShorts(targetVideo: VideoItem? = null) {
        _currentNavTab.value = NavigationTab.SHORTS
    }

    fun addComment(videoId: String, text: String) {
        viewModelScope.launch {
            repository.addComment(videoId, text)
            repository.rewardCoinForInteraction("Reward berkomentar di video", 10L)
        }
    }

    // --- P2P DANA & Coin Actions ---
    fun openDanaSawer(video: VideoItem) {
        _selectedVideoForDanaSawer.value = video
    }

    fun closeDanaSawer() {
        _selectedVideoForDanaSawer.value = null
    }

    fun openGiftSheet(video: VideoItem) {
        _selectedVideoForGift.value = video
    }

    fun closeGiftSheet() {
        _selectedVideoForGift.value = null
    }

    fun openWalletDialog() {
        _isWalletDialogOpen.value = true
    }

    fun closeWalletDialog() {
        _isWalletDialogOpen.value = false
    }

    fun openDanaProfileDialog() {
        _isDanaProfileDialogOpen.value = true
    }

    fun closeDanaProfileDialog() {
        _isDanaProfileDialogOpen.value = false
    }

    fun openPolicyDialog() {
        _isPolicyDialogOpen.value = true
    }

    fun closePolicyDialog() {
        _isPolicyDialogOpen.value = false
    }

    fun openWithdrawalDialog() {
        _isWithdrawalDialogOpen.value = true
    }

    fun closeWithdrawalDialog() {
        _isWithdrawalDialogOpen.value = false
    }

    fun withdrawFromSawerBalanceToDana(
        amountRupiah: Double,
        targetPhone: String,
        targetName: String,
        onResult: (DanaWithdrawalResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.withdrawFromSawerBalanceToDana(amountRupiah, targetPhone, targetName)
            onResult(result)
        }
    }

    fun withdrawCoinsToDana(
        coinCount: Long,
        targetPhone: String,
        targetName: String,
        onResult: (DanaWithdrawalResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.withdrawCoinsToDana(coinCount, targetPhone, targetName)
            onResult(result)
        }
    }

    fun sendGift(video: VideoItem, gift: VibeGift) {
        viewModelScope.launch {
            decrementCoinsForGift(gift, video.creatorName)
        }
    }

    fun decrementCoinsForGift(gift: VibeGift, creatorName: String, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val success = repository.decrementCoinsForGift(gift, creatorName)
            if (success) {
                _activeGiftAnimation.value = com.example.data.model.VibeGiftAnimation(
                    id = "anim_${System.currentTimeMillis()}",
                    gift = gift,
                    senderName = "@saya",
                    receiverName = creatorName
                )
            }
            onResult?.invoke(success)
        }
    }

    fun incrementCoinsForLike(creatorUsername: String, amount: Long = 5L) {
        viewModelScope.launch {
            repository.incrementCoinsForLike(creatorUsername, amount)
        }
    }

    fun incrementCoinsForComment(amount: Long = 10L) {
        viewModelScope.launch {
            repository.incrementCoinsForComment(amount)
        }
    }

    fun incrementCoinsForShare(targetOrCreator: String, amount: Long = 15L) {
        viewModelScope.launch {
            repository.incrementCoinsForShare(targetOrCreator, amount)
        }
    }

    fun incrementCoinsForBookmark(amount: Long = 5L) {
        viewModelScope.launch {
            repository.incrementCoinsForBookmark(amount)
        }
    }

    fun incrementCoinsForFollow(creatorUsername: String, amount: Long = 5L) {
        viewModelScope.launch {
            repository.incrementCoinsForFollow(creatorUsername, amount)
        }
    }

    fun convertCoins(coinCount: Long, onResult: ((CoinConversionCalculation) -> Unit)? = null) {
        viewModelScope.launch {
            val calculation = repository.convertCoinsWithPlatformFee(coinCount)
            onResult?.invoke(calculation)
        }
    }

    fun clearGiftAnimation() {
        _activeGiftAnimation.value = null
    }

    fun rechargeCoins(amount: Long, packageName: String) {
        viewModelScope.launch {
            repository.rechargeCoins(amount, packageName)
        }
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            repository.claimDailyReward()
        }
    }

    fun redeemVoucher(code: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.redeemVoucher(code)
            onResult(result)
        }
    }

    fun saveUserDanaProfile(
        phoneNumber: String,
        accountName: String,
        qrImageUrl: String?,
        notes: String,
        isActive: Boolean
    ) {
        viewModelScope.launch {
            repository.saveUserDanaProfile(phoneNumber, accountName, qrImageUrl, notes, isActive)
        }
    }

    fun confirmP2PDanaSent(creatorName: String, nominal: String) {
        viewModelScope.launch {
            repository.incrementDirectSawerCount()
            // Bonus coin booster for supporting creators directly!
            repository.rewardCoinForInteraction("Bonus Sawer DANA P2P ($nominal) ke $creatorName", 50L)
        }
    }

    fun createVideo(
        caption: String,
        tags: String,
        soundTitle: String,
        category: String,
        isLongVideo: Boolean = false,
        durationFormatted: String = if (isLongVideo) "14:20" else "0:45",
        videoUrl: String? = null,
        thumbnailUrl: String? = null
    ) {
        viewModelScope.launch {
            repository.createVideo(caption, tags, soundTitle, category, isLongVideo, durationFormatted, videoUrl, thumbnailUrl)
            _currentNavTab.value = if (isLongVideo) NavigationTab.HOME else NavigationTab.SHORTS
            _feedTab.value = FeedTab.FOR_YOU
        }
    }

    fun setCreatorTimeRange(range: CreatorTimeRange) {
        _creatorTimeRange.value = range
    }

    fun setContentFilter(filter: ContentFilterType) {
        _contentFilter.value = filter
    }

    fun setContentSearchQuery(query: String) {
        _contentSearchQuery.value = query
    }

    fun deleteVideo(videoId: String) {
        viewModelScope.launch {
            repository.deleteVideo(videoId)
        }
    }

    fun updateVideo(videoId: String, newCaption: String, newTags: List<String>) {
        viewModelScope.launch {
            repository.updateVideo(videoId, newCaption, newTags)
        }
    }
}
