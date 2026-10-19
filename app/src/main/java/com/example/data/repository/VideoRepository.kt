package com.example.data.repository

import com.example.data.local.CreatedVideoEntity
import com.example.data.local.FollowedCreatorEntity
import com.example.data.local.TokVidDao
import com.example.data.local.UserCommentEntity
import com.example.data.local.VideoInteractionEntity
import com.example.data.model.CommentItem
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID

class VideoRepository(private val dao: TokVidDao) {

    private val seedVideos = listOf(
        VideoItem(
            id = "vid_1",
            videoUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1547153760-18fc86324498?w=800",
            creatorUsername = "@siti_dance",
            creatorName = "Siti Nurhaliza",
            creatorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
            isVerified = true,
            caption = "Trend dance lagu viral terbaru! Gerakannya seru banget, tag temen kalian yang mau ikutan challenge ini! 💃✨ #fyp #dance #trend #viral2026 #foryou",
            tags = listOf("fyp", "dance", "trend", "viral2026", "foryou"),
            soundTitle = "DJ Tabrak Masuk - Viral Remix 2026",
            soundAuthor = "@dj_nusantara",
            soundCoverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=200",
            likesCount = 284500,
            commentsCount = 4120,
            bookmarksCount = 19800,
            sharesCount = 8900,
            category = "Tari & Musik"
        ),
        VideoItem(
            id = "vid_2",
            videoUrl = "https://vjs.zencdn.net/v/oceans.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800",
            creatorUsername = "@dapur_nusantara",
            creatorName = "Chef Rendy",
            creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
            isVerified = true,
            caption = "Resep rahasia Nasi Goreng Kampung super wangi dan smoky! Jangan lupa simpan resepnya ya 🍳🔥 #kuliner #resepviral #masakmasak #fypindonesia #enak",
            tags = listOf("kuliner", "resepviral", "masakmasak", "fypindonesia", "enak"),
            soundTitle = "Suara Asli - Masak Enak Nusantara",
            soundAuthor = "@dapur_nusantara",
            soundCoverUrl = "https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=200",
            likesCount = 412800,
            commentsCount = 6830,
            bookmarksCount = 54200,
            sharesCount = 15300,
            category = "Kuliner"
        ),
        VideoItem(
            id = "vid_3",
            videoUrl = "https://media.w3.org/2010/05/sintel/trailer.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800",
            creatorUsername = "@andi_lawak",
            creatorName = "Andi & Friends",
            creatorAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200",
            isVerified = false,
            caption = "Tipe-tipe orang pas lagi nunggu jemputan temen! Yang nomor 3 relatable parah wkwk 😂🤣 #komedi #lucu #ngakak #sketsa #viral",
            tags = listOf("komedi", "lucu", "ngakak", "sketsa", "viral"),
            soundTitle = "Audio Lucu Kartun - Efek Suara Komedi",
            soundAuthor = "@soundfx_id",
            soundCoverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=200",
            likesCount = 630100,
            commentsCount = 11400,
            bookmarksCount = 38700,
            sharesCount = 29400,
            category = "Komedi"
        ),
        VideoItem(
            id = "vid_4",
            videoUrl = "https://raw.githubusercontent.com/intel-iot-devkit/sample-videos/master/face-demographics-walking.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
            creatorUsername = "@jelajah_negeri",
            creatorName = "Bima Traveler",
            creatorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200",
            isVerified = true,
            caption = "Hidden gem air terjun tersembunyi di Indonesia! Airnya jernih banget kayak kaca 🌊🏞️ Ada yang tau ini di mana? #travel #pesonaindonesia #nature #explore #foryoupage",
            tags = listOf("travel", "pesonaindonesia", "nature", "explore", "foryoupage"),
            soundTitle = "Aesthetic Chill Sunset Vibes - Acoustic Guitars",
            soundAuthor = "@chillwave_studio",
            soundCoverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=200",
            likesCount = 198300,
            commentsCount = 2750,
            bookmarksCount = 28100,
            sharesCount = 6700,
            category = "Wisata"
        ),
        VideoItem(
            id = "vid_5",
            videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800",
            creatorUsername = "@kucing_gemoy",
            creatorName = "Mochi & Oreo",
            creatorAvatar = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=200",
            isVerified = false,
            caption = "Kelakuan si Oren pas minta makan bikin gregetan banget 🐱🥺 Gemesh gak sih kalian liatnya? #kucinglucu #catsoftiktok #anabul #gemoy #fyp",
            tags = listOf("kucinglucu", "catsoftiktok", "anabul", "gemoy", "fyp"),
            soundTitle = "Cute Meow Beat - Happy Kitten",
            soundAuthor = "@meow_records",
            soundCoverUrl = "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=200",
            likesCount = 875400,
            commentsCount = 14200,
            bookmarksCount = 89300,
            sharesCount = 42100,
            category = "Hewan Peliharaan"
        ),
        VideoItem(
            id = "vid_6",
            videoUrl = "https://raw.githubusercontent.com/intel-iot-devkit/sample-videos/master/people-detection.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800",
            creatorUsername = "@rizky_tech",
            creatorName = "Rizky Gadget",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
            isVerified = true,
            caption = "Fitur rahasia Android yang wajib kalian aktifkan sekarang juga! Baterai jadi jauh lebih hemat 📱⚡ #trikandroid #teknologi #gadgethacks #tipsandtricks",
            tags = listOf("trikandroid", "teknologi", "gadgethacks", "tipsandtricks"),
            soundTitle = "Tech Modern Cyber Synth - Beat",
            soundAuthor = "@rizky_tech",
            soundCoverUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=200",
            likesCount = 320500,
            commentsCount = 5420,
            bookmarksCount = 72100,
            sharesCount = 18200,
            category = "Teknologi"
        )
    )

    private val defaultComments = mapOf(
        "vid_1" to listOf(
            CommentItem("c1", "vid_1", "bella_poarch", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200", "Keren banget dancenya smooth abis! 🔥❤️", "2j lalu", 420),
            CommentItem("c2", "vid_1", "fahri_danz", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200", "Udah coba 10 kali masih belibet kak wkwk 😂", "4j lalu", 112),
            CommentItem("c3", "vid_1", "clarissa.m", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200", "Lagu remixnya auto nempel di kepala 🎶", "6j lalu", 85)
        ),
        "vid_2" to listOf(
            CommentItem("c4", "vid_2", "maya_foodie", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200", "Langsung laper tengah malem liat ini 🤤🍳", "1j lalu", 310),
            CommentItem("c5", "vid_2", "budi_santoso", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200", "Kecap manisnya merk apa chef? Warnanya pas bgt", "3j lalu", 94)
        ),
        "vid_3" to listOf(
            CommentItem("c6", "vid_3", "dimas_indra", "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=200", "WKWKWK relate banget sama temen yang bilang 'otw' padahal baru mandi 😭", "30m lalu", 650),
            CommentItem("c7", "vid_3", "nana_cute", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200", "Ekspresinya dapet banget ya ampun ngakak 😂", "1j lalu", 220)
        )
    )

    val allVideosFlow: Flow<List<VideoItem>> = combine(
        dao.getAllInteractions(),
        dao.getAllFollowedCreators(),
        dao.getAllCreatedVideos()
    ) { interactions, followed, createdVideos ->
        val interactionMap = interactions.associateBy { it.videoId }
        val followSet = followed.map { it.creatorUsername }.toSet()

        val customItems = createdVideos.map { created ->
            val inter = interactionMap[created.id]
            VideoItem(
                id = created.id,
                videoUrl = created.videoUrl,
                thumbnailUrl = created.thumbnailUrl,
                creatorUsername = "@saya",
                creatorName = "Pengguna MyFlix",
                creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                isVerified = true,
                caption = created.caption,
                tags = created.tags.split(" ").filter { it.isNotBlank() },
                soundTitle = created.soundTitle,
                soundAuthor = "@saya",
                soundCoverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=200",
                likesCount = 12 + (inter?.extraLikesDelta ?: 0),
                commentsCount = 0 + (inter?.extraCommentsDelta ?: 0),
                bookmarksCount = 3,
                sharesCount = 1,
                isLiked = inter?.isLiked ?: false,
                isBookmarked = inter?.isBookmarked ?: false,
                isFollowing = false,
                category = created.category,
                isUserCreated = true
            )
        }

        val baseItems = seedVideos.map { seed ->
            val inter = interactionMap[seed.id]
            val isLiked = inter?.isLiked ?: false
            val isBookmarked = inter?.isBookmarked ?: false
            val extraLikes = inter?.extraLikesDelta ?: 0
            val extraComments = inter?.extraCommentsDelta ?: 0
            val isFollowing = followSet.contains(seed.creatorUsername)

            seed.copy(
                isLiked = isLiked,
                isBookmarked = isBookmarked,
                likesCount = seed.likesCount + extraLikes,
                commentsCount = seed.commentsCount + extraComments,
                isFollowing = isFollowing
            )
        }

        // Newly created videos at the front
        customItems + baseItems
    }

    suspend fun toggleLike(video: VideoItem) {
        val current = dao.getInteraction(video.id)
        val wasLiked = current?.isLiked ?: video.isLiked
        val newLiked = !wasLiked
        val currentDelta = current?.extraLikesDelta ?: 0
        val newDelta = if (newLiked) currentDelta + 1 else currentDelta - 1

        dao.upsertInteraction(
            VideoInteractionEntity(
                videoId = video.id,
                isLiked = newLiked,
                isBookmarked = current?.isBookmarked ?: video.isBookmarked,
                extraLikesDelta = newDelta,
                extraCommentsDelta = current?.extraCommentsDelta ?: 0
            )
        )
    }

    suspend fun toggleBookmark(video: VideoItem) {
        val current = dao.getInteraction(video.id)
        val wasBookmarked = current?.isBookmarked ?: video.isBookmarked
        val newBookmarked = !wasBookmarked

        dao.upsertInteraction(
            VideoInteractionEntity(
                videoId = video.id,
                isLiked = current?.isLiked ?: video.isLiked,
                isBookmarked = newBookmarked,
                extraLikesDelta = current?.extraLikesDelta ?: 0,
                extraCommentsDelta = current?.extraCommentsDelta ?: 0
            )
        )
    }

    suspend fun toggleFollow(creatorUsername: String, currentStatus: Boolean) {
        if (currentStatus) {
            dao.removeFollow(creatorUsername)
        } else {
            dao.upsertFollow(FollowedCreatorEntity(creatorUsername, true))
        }
    }

    fun getCommentsForVideo(videoId: String): Flow<List<CommentItem>> {
        val defaultList = defaultComments[videoId] ?: emptyList()
        return dao.getCommentsForVideo(videoId).map { userComments ->
            val userItems = userComments.map {
                CommentItem(
                    id = it.id.toString(),
                    videoId = it.videoId,
                    username = it.username,
                    userAvatar = it.userAvatar,
                    commentText = it.text,
                    timestamp = "Baru saja",
                    likesCount = 0,
                    isLiked = false,
                    isCreator = false
                )
            }
            userItems + defaultList
        }
    }

    suspend fun addComment(videoId: String, commentText: String) {
        if (commentText.isBlank()) return
        dao.insertComment(
            UserCommentEntity(
                videoId = videoId,
                username = "@saya",
                userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                text = commentText.trim()
            )
        )
        val current = dao.getInteraction(videoId)
        dao.upsertInteraction(
            VideoInteractionEntity(
                videoId = videoId,
                isLiked = current?.isLiked ?: false,
                isBookmarked = current?.isBookmarked ?: false,
                extraLikesDelta = current?.extraLikesDelta ?: 0,
                extraCommentsDelta = (current?.extraCommentsDelta ?: 0) + 1
            )
        )
    }

    suspend fun createVideo(
        caption: String,
        tags: String,
        soundTitle: String,
        category: String
    ) {
        val id = "created_${UUID.randomUUID().toString().take(8)}"
        // Pick one of the lively clips as source
        val sampleClips = listOf(
            "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
            "https://vjs.zencdn.net/v/oceans.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        )
        val chosenVideo = sampleClips.random()
        val chosenThumb = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=800"

        dao.insertCreatedVideo(
            CreatedVideoEntity(
                id = id,
                videoUrl = chosenVideo,
                thumbnailUrl = chosenThumb,
                caption = caption,
                tags = tags,
                soundTitle = if (soundTitle.isNotBlank()) soundTitle else "Suara Asli - @saya",
                category = category
            )
        )
    }

    fun getNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(
                id = "n1",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                title = "Siti Nurhaliza menyukai video Anda",
                description = "\"Dance challenge seru bareng teman...\"",
                timeAgo = "10 menit lalu",
                type = NotificationType.LIKE,
                thumbnailPreview = "https://images.unsplash.com/photo-1547153760-18fc86324498?w=200"
            ),
            NotificationItem(
                id = "n2",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                title = "Chef Rendy mulai mengikuti Anda",
                description = "Ikuti balik untuk melihat video resep terbaru",
                timeAgo = "1 jam lalu",
                type = NotificationType.FOLLOW
            ),
            NotificationItem(
                id = "n3",
                avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200",
                title = "Andi berkomentar di video Anda",
                description = "\"Ngakak parah pas bagian akhir wkwk\"",
                timeAgo = "3 jam lalu",
                type = NotificationType.COMMENT,
                thumbnailPreview = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=200"
            ),
            NotificationItem(
                id = "n4",
                avatarUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=200",
                title = "Trending Alert! 🔥",
                description = "Hashtag #fyp2026 sedang naik daun di Indonesia. Buat video sekarang!",
                timeAgo = "Kemarin",
                type = NotificationType.TREND
            )
        )
    }
}
