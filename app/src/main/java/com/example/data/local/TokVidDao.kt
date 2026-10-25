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

    @Query("DELETE FROM created_videos WHERE id = :videoId")
    suspend fun deleteCreatedVideo(videoId: String)

    @Query("UPDATE created_videos SET caption = :newCaption, tags = :newTags WHERE id = :videoId")
    suspend fun updateCreatedVideo(videoId: String, newCaption: String, newTags: String)

    // User DANA Profile
    @Query("SELECT * FROM user_dana_profile WHERE id = 'primary_user_dana' LIMIT 1")
    fun getUserDanaProfile(): Flow<UserDanaProfileEntity?>

    @Query("SELECT * FROM user_dana_profile WHERE id = 'primary_user_dana' LIMIT 1")
    suspend fun getUserDanaProfileSync(): UserDanaProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserDanaProfile(profile: UserDanaProfileEntity)

    // User Coin Wallet
    @Query("SELECT * FROM user_coin_wallet WHERE id = 'primary_coin_wallet' LIMIT 1")
    fun getUserCoinWallet(): Flow<UserCoinWalletEntity?>

    @Query("SELECT * FROM user_coin_wallet WHERE id = 'primary_coin_wallet' LIMIT 1")
    suspend fun getUserCoinWalletSync(): UserCoinWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserCoinWallet(wallet: UserCoinWalletEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCoinWallet(wallet: UserCoinWalletEntity)

    // Coin Transactions History
    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC LIMIT 100")
    fun getAllCoinTransactions(): Flow<List<CoinTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinTransaction(transaction: CoinTransactionEntity)
}
