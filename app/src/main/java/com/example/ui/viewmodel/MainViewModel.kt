package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TokVidDatabase
import com.example.data.model.CommentItem
import com.example.data.model.NotificationItem
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FeedTab {
    FOLLOWING, FOR_YOU
}

enum class NavigationTab {
    HOME, SHORTS, DISCOVER, CREATE, INBOX, PROFILE, WATCH
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeComments: StateFlow<List<CommentItem>> = _selectedVideoForComments.flatMapLatest { video ->
        if (video != null) repository.getCommentsForVideo(video.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: List<NotificationItem> = repository.getNotifications()

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
                repository.rewardCoinForInteraction("Reward simpan video ${video.creatorUsername}", 5L)
            }
        }
    }

    fun toggleFollow(creatorUsername: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFollow(creatorUsername, currentStatus)
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

    fun sendGift(video: VideoItem, gift: com.example.data.model.VibeGift) {
        viewModelScope.launch {
            val success = repository.spendCoinsForGift(gift, video.creatorName)
            if (success) {
                _activeGiftAnimation.value = com.example.data.model.VibeGiftAnimation(
                    id = "anim_${System.currentTimeMillis()}",
                    gift = gift,
                    senderName = "@saya",
                    receiverName = video.creatorName
                )
            }
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
}
