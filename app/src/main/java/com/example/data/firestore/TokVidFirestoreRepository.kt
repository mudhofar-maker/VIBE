package com.example.data.firestore

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TokVidFirestoreRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserVideos(userId: String): Flow<List<VideoDoc>> = flow {
        val path = "users/$userId/videos"
        db.collection("users").document(userId).collection("videos")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(VideoDoc::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun createVideo(
        videoUrl: String,
        thumbnailUrl: String,
        creatorUsername: String,
        creatorName: String,
        creatorAvatar: String,
        caption: String,
        tags: List<String>,
        soundTitle: String,
        soundAuthor: String,
        soundCoverUrl: String,
        category: String
    ): Result<String> {
        val uid = requireUserId()
        val videoId = "vid_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docRef = db.collection("users").document(uid).collection("videos").document(videoId)
        val data = mapOf(
            "userId" to uid,
            "videoUrl" to videoUrl,
            "thumbnailUrl" to thumbnailUrl,
            "creatorUsername" to creatorUsername,
            "creatorName" to creatorName,
            "creatorAvatar" to creatorAvatar,
            "caption" to caption,
            "tags" to tags,
            "soundTitle" to soundTitle,
            "soundAuthor" to soundAuthor,
            "soundCoverUrl" to soundCoverUrl,
            "category" to category,
            "likesCount" to 0L,
            "commentsCount" to 0L,
            "bookmarksCount" to 0L,
            "sharesCount" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        return try {
            docRef.set(data).await()
            Result.success(videoId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeUserInteractions(userId: String): Flow<List<InteractionDoc>> = flow {
        val path = "users/$userId/interactions"
        db.collection("users").document(userId).collection("interactions")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(InteractionDoc::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun setInteraction(
        videoId: String,
        isLiked: Boolean,
        isBookmarked: Boolean,
        isShared: Boolean
    ): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("interactions").document(videoId)
        val data = mapOf(
            "userId" to uid,
            "videoId" to videoId,
            "isLiked" to isLiked,
            "isBookmarked" to isBookmarked,
            "isShared" to isShared,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        return try {
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeUserComments(userId: String): Flow<List<CommentDoc>> = flow {
        val path = "users/$userId/comments"
        db.collection("users").document(userId).collection("comments")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(CommentDoc::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun addComment(
        videoId: String,
        commentText: String,
        username: String,
        userAvatar: String
    ): Result<String> {
        val uid = requireUserId()
        val commentId = "cmt_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docRef = db.collection("users").document(uid).collection("comments").document(commentId)
        val data = mapOf(
            "userId" to uid,
            "videoId" to videoId,
            "username" to username,
            "userAvatar" to userAvatar,
            "commentText" to commentText,
            "likesCount" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        return try {
            docRef.set(data).await()
            Result.success(commentId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeFollowing(userId: String): Flow<List<FollowingDoc>> = flow {
        val path = "users/$userId/following"
        db.collection("users").document(userId).collection("following")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(FollowingDoc::class.java) }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
            .collect { emit(it) }
    }

    suspend fun setFollowing(
        creatorUsername: String,
        creatorName: String,
        creatorAvatar: String,
        follow: Boolean
    ): Result<Unit> {
        val uid = requireUserId()
        val docId = creatorUsername.replace("@", "").replace(".", "_")
        val docRef = db.collection("users").document(uid).collection("following").document(docId)

        return try {
            if (follow) {
                val data = mapOf(
                    "userId" to uid,
                    "creatorUsername" to creatorUsername,
                    "creatorName" to creatorName,
                    "creatorAvatar" to creatorAvatar,
                    "createdAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(data).await()
            } else {
                docRef.delete().await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, if (follow) OperationType.CREATE else OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun saveUserProfile(
        username: String,
        displayName: String,
        bio: String,
        avatarUrl: String
    ): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val data = mapOf(
            "userId" to uid,
            "username" to username,
            "displayName" to displayName,
            "bio" to bio,
            "avatarUrl" to avatarUrl,
            "followingCount" to 0L,
            "followersCount" to 0L,
            "likesReceivedCount" to 0L,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        return try {
            docRef.set(data).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }
}
