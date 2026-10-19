package com.example.data.model

data class VideoItem(
    val id: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val creatorUsername: String,
    val creatorName: String,
    val creatorAvatar: String,
    val isVerified: Boolean = false,
    val caption: String,
    val tags: List<String> = emptyList(),
    val soundTitle: String,
    val soundAuthor: String,
    val soundCoverUrl: String,
    val likesCount: Long,
    val commentsCount: Long,
    val bookmarksCount: Long,
    val sharesCount: Long,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isFollowing: Boolean = false,
    val category: String = "Untuk Anda",
    val isUserCreated: Boolean = false
) {
    fun formattedLikes(): String = formatCount(likesCount)
    fun formattedComments(): String = formatCount(commentsCount)
    fun formattedBookmarks(): String = formatCount(bookmarksCount)
    fun formattedShares(): String = formatCount(sharesCount)

    companion object {
        fun formatCount(count: Long): String {
            return when {
                count >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", count / 1_000_000.0)
                count >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", count / 1_000.0)
                else -> count.toString()
            }
        }
    }
}

data class CommentItem(
    val id: String,
    val videoId: String,
    val username: String,
    val userAvatar: String,
    val commentText: String,
    val timestamp: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isCreator: Boolean = false
)

data class NotificationItem(
    val id: String,
    val avatarUrl: String,
    val title: String,
    val description: String,
    val timeAgo: String,
    val type: NotificationType,
    val thumbnailPreview: String? = null
)

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, SYSTEM, TREND
}
