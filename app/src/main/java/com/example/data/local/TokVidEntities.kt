package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_interactions")
data class VideoInteractionEntity(
    @PrimaryKey val videoId: String,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val extraLikesDelta: Long = 0,
    val extraCommentsDelta: Long = 0
)

@Entity(tableName = "user_comments")
data class UserCommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val videoId: String,
    val username: String,
    val userAvatar: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "followed_creators")
data class FollowedCreatorEntity(
    @PrimaryKey val creatorUsername: String,
    val isFollowing: Boolean = true
)

@Entity(tableName = "created_videos")
data class CreatedVideoEntity(
    @PrimaryKey val id: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val caption: String,
    val tags: String,
    val soundTitle: String,
    val category: String,
    val isLongVideo: Boolean = false,
    val durationFormatted: String = "10:30",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_dana_profile")
data class UserDanaProfileEntity(
    @PrimaryKey val id: String = "primary_user_dana",
    val danaPhoneNumber: String = "0812-9876-5432",
    val danaAccountName: String = "ALDI PRASETYA",
    val qrCustomImageUrl: String? = null,
    val notes: String = "Saweran langsung P2P ke akun DANA saya tanpa potongan admin aplikasi!",
    val isDanaActive: Boolean = true,
    val totalDirectSawerCount: Long = 14L,
    val sawerBalanceRupiah: Double = 175_000.0
)

@Entity(tableName = "user_coin_wallet")
data class UserCoinWalletEntity(
    @PrimaryKey val id: String = "primary_coin_wallet",
    val balance: Long = 350L,
    val lastCheckInDate: String = ""
)

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Long,
    val isEarned: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String,
    val iconEmoji: String = "🪙"
)
