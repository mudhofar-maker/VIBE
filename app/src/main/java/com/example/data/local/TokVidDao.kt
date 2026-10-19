package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TokVidDao {
    @Query("SELECT * FROM video_interactions")
    fun getAllInteractions(): Flow<List<VideoInteractionEntity>>

    @Query("SELECT * FROM video_interactions WHERE videoId = :videoId LIMIT 1")
    suspend fun getInteraction(videoId: String): VideoInteractionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertInteraction(interaction: VideoInteractionEntity)

    @Query("SELECT * FROM user_comments WHERE videoId = :videoId ORDER BY timestamp DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<UserCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: UserCommentEntity)

    @Query("SELECT * FROM followed_creators")
    fun getAllFollowedCreators(): Flow<List<FollowedCreatorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFollow(follow: FollowedCreatorEntity)

    @Query("DELETE FROM followed_creators WHERE creatorUsername = :username")
    suspend fun removeFollow(username: String)

    @Query("SELECT * FROM created_videos ORDER BY createdAt DESC")
    fun getAllCreatedVideos(): Flow<List<CreatedVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreatedVideo(video: CreatedVideoEntity)
}
