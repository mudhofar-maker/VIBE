package com.example.data.firestore

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

@IgnoreExtraProperties
data class UserProfileDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val displayName: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val followingCount: Long = 0L,
    val followersCount: Long = 0L,
    val likesReceivedCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class VideoDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val creatorUsername: String = "",
    val creatorName: String = "",
    val creatorAvatar: String = "",
    val caption: String = "",
    val tags: List<String> = emptyList(),
    val soundTitle: String = "",
    val soundAuthor: String = "",
    val soundCoverUrl: String = "",
    val category: String = "Untuk Anda",
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val bookmarksCount: Long = 0L,
    val sharesCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class InteractionDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val videoId: String = "",
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isShared: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CommentDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val videoId: String = "",
    val username: String = "",
    val userAvatar: String = "",
    val commentText: String = "",
    val likesCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FollowingDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val creatorUsername: String = "",
    val creatorName: String = "",
    val creatorAvatar: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)
