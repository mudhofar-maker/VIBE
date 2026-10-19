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
    HOME, DISCOVER, CREATE, INBOX, PROFILE
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

    private val _selectedVideoForComments = MutableStateFlow<VideoItem?>(null)
    val selectedVideoForComments: StateFlow<VideoItem?> = _selectedVideoForComments.asStateFlow()

    private val _selectedVideoForShare = MutableStateFlow<VideoItem?>(null)
    val selectedVideoForShare: StateFlow<VideoItem?> = _selectedVideoForShare.asStateFlow()

    private val _selectedVideoForSound = MutableStateFlow<VideoItem?>(null)
    val selectedVideoForSound: StateFlow<VideoItem?> = _selectedVideoForSound.asStateFlow()

    private val _selectedCreator = MutableStateFlow<VideoItem?>(null)
    val selectedCreator: StateFlow<VideoItem?> = _selectedCreator.asStateFlow()

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
        }
    }

    fun toggleBookmark(video: VideoItem) {
        viewModelScope.launch {
            repository.toggleBookmark(video)
        }
    }

    fun toggleFollow(creatorUsername: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFollow(creatorUsername, currentStatus)
        }
    }

    fun addComment(videoId: String, text: String) {
        viewModelScope.launch {
            repository.addComment(videoId, text)
        }
    }

    fun createVideo(caption: String, tags: String, soundTitle: String, category: String) {
        viewModelScope.launch {
            repository.createVideo(caption, tags, soundTitle, category)
            _currentNavTab.value = NavigationTab.HOME
            _feedTab.value = FeedTab.FOR_YOU
        }
    }
}
