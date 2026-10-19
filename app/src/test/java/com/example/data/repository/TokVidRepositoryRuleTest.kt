package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.firestore.TokVidFirestoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TokVidRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createVideo_authenticatedUser_createsDocumentSuccessfully() = runBlocking {
        val uid = signInTestUser("creator@example.com")
        val repository = TokVidFirestoreRepository(firestore)

        val result = withTimeout(10000L) {
            repository.createVideo(
                videoUrl = "https://example.com/video1.mp4",
                thumbnailUrl = "https://example.com/thumb1.jpg",
                creatorUsername = "@test_creator",
                creatorName = "Test Creator",
                creatorAvatar = "https://example.com/avatar.jpg",
                caption = "Testing video rules",
                tags = listOf("viral", "tokvid"),
                soundTitle = "Sound Test",
                soundAuthor = "@artist",
                soundCoverUrl = "https://example.com/sound.jpg",
                category = "Komedi"
            )
        }

        assertTrue("Expected createVideo to succeed", result.isSuccess)
        val videoId = result.getOrNull()
        assertNotNull(videoId)

        val videos = withTimeout(10000L) {
            repository.observeUserVideos(uid).first { list -> list.any { it.id == videoId } }
        }
        assertTrue(videos.any { it.id == videoId })
    }

    @Test
    fun setInteraction_authenticatedUser_persistsInteractions() = runBlocking {
        val uid = signInTestUser("viewer@example.com")
        val repository = TokVidFirestoreRepository(firestore)

        val result = withTimeout(10000L) {
            repository.setInteraction("vid_test_123", isLiked = true, isBookmarked = true, isShared = false)
        }
        assertTrue("Expected setInteraction to succeed", result.isSuccess)

        val interactions = withTimeout(10000L) {
            repository.observeUserInteractions(uid).first { list -> list.any { it.videoId == "vid_test_123" } }
        }
        val target = interactions.firstOrNull { it.videoId == "vid_test_123" }
        assertNotNull(target)
        assertTrue(target!!.isLiked)
        assertTrue(target.isBookmarked)
    }

    @Test
    fun setFollowing_authenticatedUser_togglesFollow() = runBlocking {
        val uid = signInTestUser("follower@example.com")
        val repository = TokVidFirestoreRepository(firestore)

        val followResult = withTimeout(10000L) {
            repository.setFollowing("@famous_star", "Famous Star", "https://example.com/avatar.jpg", true)
        }
        assertTrue(followResult.isSuccess)

        val followingList = withTimeout(10000L) {
            repository.observeFollowing(uid).first { list -> list.any { it.creatorUsername == "@famous_star" } }
        }
        assertTrue(followingList.isNotEmpty())
    }
}
