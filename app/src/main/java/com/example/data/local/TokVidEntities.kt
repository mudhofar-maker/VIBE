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
    val createdAt: Long = System.currentTimeMillis()
)
