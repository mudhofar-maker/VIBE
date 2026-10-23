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
    val isUserCreated: Boolean = false,
    val isLongVideo: Boolean = false,
    val durationFormatted: String = if (isLongVideo) "15:20" else "0:45",
    val viewsCount: Long = 125_000L,
    val description: String = caption,
    val resolution: String = "1080p HD",
    val creatorDanaNumber: String = "0812-3456-7890",
    val creatorDanaName: String = creatorName,
    val creatorDanaQrUrl: String = "",
    val creatorDanaNote: String = "Saweran langsung masuk akun DANA saya 100% P2P tanpa potongan",
    val isDanaVerified: Boolean = true
) {
    fun formattedLikes(): String = formatCount(likesCount)
    fun formattedComments(): String = formatCount(commentsCount)
    fun formattedBookmarks(): String = formatCount(bookmarksCount)
    fun formattedShares(): String = formatCount(sharesCount)
    fun formattedViews(): String = formatCount(viewsCount)

    val totalFanInteractionsCount: Long
        get() = likesCount + commentsCount + bookmarksCount + sharesCount

    val totalFanInteractionsRupiahValue: Double
        get() = totalFanInteractionsCount * FanInteractionEconomics.PRICE_PER_INTERACTION_RUPIAH

    fun formattedFanInteractionValue(): String =
        FanInteractionEconomics.formatRupiah(totalFanInteractionsRupiahValue)

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
