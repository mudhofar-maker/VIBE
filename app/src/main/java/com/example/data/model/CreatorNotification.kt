package com.example.data.model

import java.text.NumberFormat
import java.util.Locale

enum class CreatorNotificationCategory(val label: String) {
    ALL("Semua"),
    GIFTS_AND_SAWER("Hadiah & Sawer"),
    MONETIZATION("Royalti & Cuan"),
    INTERACTIONS("Suka & Komen"),
    FOLLOWERS("Pengikut Baru")
}

enum class CreatorNotificationType {
    GIFT_RECEIVED,
    SAWER_DANA,
    LIKE_INTERACTION,
    COMMENT_INTERACTION,
    SHARE_INTERACTION,
    BOOKMARK_INTERACTION,
    NEW_FOLLOWER,
    WITHDRAWAL_PAID
}

data class CreatorNotification(
    val id: String,
    val title: String,
    val description: String,
    val senderName: String,
    val senderAvatar: String,
    val timestamp: Long = System.currentTimeMillis(),
    val timeFormatted: String = "Baru saja",
    val type: CreatorNotificationType,
    val monetizationRupiah: Double = 0.0,
    val coinsEarned: Long = 0L,
    val giftEmoji: String? = null,
    val isRead: Boolean = false,
    val targetVideoTitle: String? = null
) {
    val category: CreatorNotificationCategory
        get() = when (type) {
            CreatorNotificationType.GIFT_RECEIVED,
            CreatorNotificationType.SAWER_DANA -> CreatorNotificationCategory.GIFTS_AND_SAWER
            CreatorNotificationType.WITHDRAWAL_PAID -> CreatorNotificationCategory.MONETIZATION
            CreatorNotificationType.LIKE_INTERACTION,
            CreatorNotificationType.COMMENT_INTERACTION,
            CreatorNotificationType.SHARE_INTERACTION,
            CreatorNotificationType.BOOKMARK_INTERACTION -> CreatorNotificationCategory.INTERACTIONS
            CreatorNotificationType.NEW_FOLLOWER -> CreatorNotificationCategory.FOLLOWERS
        }

    fun formattedMonetization(): String? {
        if (monetizationRupiah <= 0.0) return null
        return if (monetizationRupiah < 1.0) {
            String.format(Locale("id", "ID"), "+Rp %.2f", monetizationRupiah)
        } else {
            "+Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(monetizationRupiah.toLong())
        }
    }
}
