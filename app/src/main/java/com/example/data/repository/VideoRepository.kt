package com.example.data.repository

import com.example.data.local.CoinTransactionEntity
import com.example.data.local.CreatedVideoEntity
import com.example.data.local.FollowedCreatorEntity
import com.example.data.local.TokVidDao
import com.example.data.local.UserCoinWalletEntity
import com.example.data.local.UserCommentEntity
import com.example.data.local.UserDanaProfileEntity
import com.example.data.local.VideoInteractionEntity
import com.example.data.model.CoinConversionCalculation
import com.example.data.model.CommentItem
import com.example.data.model.DanaWithdrawalResult
import com.example.data.model.CreatorNotification
import com.example.data.model.CreatorNotificationCategory
import com.example.data.model.CreatorNotificationType
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.UserDanaProfile
import com.example.data.model.VibeGift
import com.example.data.model.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Locale
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
            category = "Teknologi",
            isLongVideo = false,
            durationFormatted = "0:52",
            viewsCount = 320500L
        ),
        VideoItem(
            id = "long_1",
            videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=1000",
            creatorUsername = "@tech_academy",
            creatorName = "Tech Academy Indonesia",
            creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
            isVerified = true,
            caption = "Tutorial Lengkap Membuat Aplikasi Web & Mobile Modern 2026: Arsitektur Scalable, Cloud Integration, dan Clean UI",
            tags = listOf("tutorial", "coding", "android", "web", "teknologi"),
            soundTitle = "Audio Asli - Tech Academy",
            soundAuthor = "@tech_academy",
            soundCoverUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=200",
            likesCount = 89400,
            commentsCount = 2150,
            bookmarksCount = 34200,
            sharesCount = 12400,
            category = "Teknologi",
            isLongVideo = true,
            durationFormatted = "24:15",
            viewsCount = 380000L,
            description = "Pelajari langkah demi langkah cara membuat aplikasi video modern berkinerja tinggi. Di video berdurasi 24 menit ini kita membahas UI/UX interaktif, optimalisasi media player, pengelolaan cache, hingga deployment otomatis. Jangan lupa like, tinggalkan komentar, dan subscribe!",
            resolution = "4K 60FPS"
        ),
        VideoItem(
            id = "long_2",
            videoUrl = "https://vjs.zencdn.net/v/oceans.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1000",
            creatorUsername = "@nature_documentary",
            creatorName = "Ekspedisi Nusantara",
            creatorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200",
            isVerified = true,
            caption = "Dokumenter 4K: Keajaiban Samudra Indonesia & Surga Tersembunyi Bawah Laut Raja Ampat",
            tags = listOf("dokumenter", "alam", "rajaampat", "laut", "pesonaindonesia"),
            soundTitle = "Ocean Symphony - Epic Wildlife",
            soundAuthor = "@nature_sound",
            soundCoverUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=200",
            likesCount = 215000,
            commentsCount = 6420,
            bookmarksCount = 89000,
            sharesCount = 34500,
            category = "Wisata",
            isLongVideo = true,
            durationFormatted = "18:40",
            viewsCount = 920000L,
            description = "Menyelami kedalaman terumbu karang terindah di dunia. Kami menghabiskan 30 hari di kepulauan Raja Ampat untuk merekam biota laut langka, paus biru, dan kearifan lokal masyarakat pesisir.",
            resolution = "4K Ultra HD"
        ),
        VideoItem(
            id = "long_3",
            videoUrl = "https://media.w3.org/2010/05/sintel/trailer.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=1000",
            creatorUsername = "@dapur_nusantara",
            creatorName = "Chef Rendy",
            creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
            isVerified = true,
            caption = "Resep Rahasia Rendang Daging Sapi Otentik Padang: Teknik Memasak 8 Jam Agar Hitam Sempurna & Bumbu Meresap",
            tags = listOf("kuliner", "resep", "rendang", "masak", "indonesia"),
            soundTitle = "Suara Dapur Tradisional - Chef Rendy",
            soundAuthor = "@dapur_nusantara",
            soundCoverUrl = "https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=200",
            likesCount = 340000,
            commentsCount = 8200,
            bookmarksCount = 120000,
            sharesCount = 45000,
            category = "Kuliner",
            isLongVideo = true,
            durationFormatted = "15:30",
            viewsCount = 1250000L,
            description = "Panduan lengkap memasak Rendang Daging Sapi khas Minang asli dari rempah pilihan hingga menjadi kalio lalu rendang hitam pekat yang lezat dan tahan berbulan-bulan tanpa pengawet.",
            resolution = "1080p HD"
        ),
        VideoItem(
            id = "long_4",
            videoUrl = "https://raw.githubusercontent.com/intel-iot-devkit/sample-videos/master/people-detection.mp4",
            thumbnailUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=1000",
            creatorUsername = "@rizky_tech",
            creatorName = "Rizky Gadget",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
            isVerified = true,
            caption = "Kompilasi Gadget Masa Depan 2026: AI Devices, Smart Glasses, dan Teknologi Layar Gulung",
            tags = listOf("gadget", "teknologi", "future", "review", "ai"),
            soundTitle = "Synthwave Tech Drive",
            soundAuthor = "@rizky_tech",
            soundCoverUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=200",
            likesCount = 145000,
            commentsCount = 4300,
            bookmarksCount = 52000,
            sharesCount = 18000,
            category = "Teknologi",
            isLongVideo = true,
            durationFormatted = "12:10",
            viewsCount = 540000L,
            description = "Tinjauan mendalam berbagai inovasi perangkat pintar terbaru tahun 2026. Simak pengujian performa, ketahanan baterai, dan integrasi kecerdasan buatan dalam kehidupan sehari-hari.",
            resolution = "1080p60"
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

    private val deletedVideoIdsFlow = MutableStateFlow<Set<String>>(emptySet())
    private val editedVideoCaptionsFlow = MutableStateFlow<Map<String, Pair<String, List<String>>>>(emptyMap())

    private val _creatorNotificationsFlow = MutableStateFlow<List<CreatorNotification>>(
        listOf(
            CreatorNotification(
                id = "notif_seed_1",
                title = "Menerima Hadiah VibeGift!",
                description = "Bella Poarch mengirim Paus Megah (100 Koin) di video Anda",
                senderName = "Bella Poarch",
                senderAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                type = CreatorNotificationType.GIFT_RECEIVED,
                monetizationRupiah = 800_000.0,
                coinsEarned = 100L,
                giftEmoji = "🐋",
                timeFormatted = "2 menit lalu",
                targetVideoTitle = "Trend dance viral 2026",
                isRead = false
            ),
            CreatorNotification(
                id = "notif_seed_2",
                title = "Saweran DANA P2P Masuk!",
                description = "Fahri Danz menyawer Rp 50.000 langsung via Barcode DANA Anda",
                senderName = "Fahri Danz",
                senderAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200",
                type = CreatorNotificationType.SAWER_DANA,
                monetizationRupiah = 50_000.0,
                timeFormatted = "15 menit lalu",
                isRead = false
            ),
            CreatorNotification(
                id = "notif_seed_3",
                title = "Royalti Suka Penggemar",
                description = "Clarissa menyukai video kuliner Anda",
                senderName = "Clarissa M",
                senderAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                type = CreatorNotificationType.LIKE_INTERACTION,
                monetizationRupiah = 0.05,
                timeFormatted = "32 menit lalu",
                targetVideoTitle = "Resep Nasi Goreng Kampung",
                isRead = true
            ),
            CreatorNotification(
                id = "notif_seed_4",
                title = "Pengikut Baru Bergabung",
                description = "Budi Santoso mulai mengikuti saluran Anda",
                senderName = "Budi Santoso",
                senderAvatar = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200",
                type = CreatorNotificationType.NEW_FOLLOWER,
                monetizationRupiah = 0.05,
                timeFormatted = "1 jam lalu",
                isRead = true
            ),
            CreatorNotification(
                id = "notif_seed_5",
                title = "Royalti Komentar Penggemar",
                description = "Maya menulis: 'Resepnya juara banget chef!'",
                senderName = "Maya Foodie",
                senderAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200",
                type = CreatorNotificationType.COMMENT_INTERACTION,
                monetizationRupiah = 0.05,
                timeFormatted = "2 jam lalu",
                targetVideoTitle = "Resep Nasi Goreng Kampung",
                isRead = true
            )
        )
    )
    val creatorNotificationsFlow: Flow<List<CreatorNotification>> = _creatorNotificationsFlow

    val allVideosFlow: Flow<List<VideoItem>> = combine(
        dao.getAllInteractions(),
        dao.getAllFollowedCreators(),
        dao.getAllCreatedVideos(),
        deletedVideoIdsFlow,
        editedVideoCaptionsFlow
    ) { interactions, followed, createdVideos, deletedIds, editedCaptions ->
        val interactionMap = interactions.associateBy { it.videoId }
        val followSet = followed.map { it.creatorUsername }.toSet()

        val customItems = createdVideos
            .filter { it.id !in deletedIds }
            .map { created ->
                val inter = interactionMap[created.id]
                val edited = editedCaptions[created.id]
                val caption = edited?.first ?: created.caption
                val tags = edited?.second ?: created.tags.split(" ").filter { it.isNotBlank() }
                VideoItem(
                    id = created.id,
                    videoUrl = created.videoUrl,
                    thumbnailUrl = created.thumbnailUrl,
                    creatorUsername = "@saya",
                    creatorName = "Aldi Prasetya",
                    creatorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                    isVerified = true,
                    caption = caption,
                    tags = tags,
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
                    isUserCreated = true,
                    isLongVideo = created.isLongVideo,
                    durationFormatted = created.durationFormatted,
                    viewsCount = 1420L,
                    description = caption
                )
            }

        val baseItems = seedVideos
            .filter { it.id !in deletedIds }
            .map { seed ->
                val inter = interactionMap[seed.id]
                val edited = editedCaptions[seed.id]
                val caption = edited?.first ?: seed.caption
                val tags = edited?.second ?: seed.tags
                val isLiked = inter?.isLiked ?: false
                val isBookmarked = inter?.isBookmarked ?: false
                val extraLikes = inter?.extraLikesDelta ?: 0
                val extraComments = inter?.extraCommentsDelta ?: 0
                val isFollowing = followSet.contains(seed.creatorUsername)

                seed.copy(
                    caption = caption,
                    tags = tags,
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
        category: String,
        isLongVideo: Boolean = false,
        durationFormatted: String = if (isLongVideo) "14:20" else "0:45",
        videoUrl: String? = null,
        thumbnailUrl: String? = null
    ) {
        val id = "created_${UUID.randomUUID().toString().take(8)}"
        // Pick one of the lively clips as source if not provided
        val sampleClips = listOf(
            "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4",
            "https://vjs.zencdn.net/v/oceans.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        )
        val chosenVideo = if (!videoUrl.isNullOrBlank()) videoUrl else sampleClips.random()
        val chosenThumb = if (!thumbnailUrl.isNullOrBlank()) {
            thumbnailUrl
        } else if (isLongVideo) {
            "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=1000"
        } else {
            "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=800"
        }

        dao.insertCreatedVideo(
            CreatedVideoEntity(
                id = id,
                videoUrl = chosenVideo,
                thumbnailUrl = chosenThumb,
                caption = caption,
                tags = tags,
                soundTitle = if (soundTitle.isNotBlank()) soundTitle else "Suara Asli - @saya",
                category = category,
                isLongVideo = isLongVideo,
                durationFormatted = durationFormatted
            )
        )
    }

    suspend fun deleteVideo(videoId: String) {
        dao.deleteCreatedVideo(videoId)
        deletedVideoIdsFlow.value = deletedVideoIdsFlow.value + videoId
    }

    suspend fun updateVideo(videoId: String, newCaption: String, newTags: List<String>) {
        dao.updateCreatedVideo(videoId, newCaption, newTags.joinToString(" "))
        editedVideoCaptionsFlow.value = editedVideoCaptionsFlow.value + (videoId to Pair(newCaption, newTags))
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

    // --- P2P DANA Showcase Profile ---
    fun getUserDanaProfile(): Flow<UserDanaProfile> {
        return dao.getUserDanaProfile().map { entity ->
            if (entity != null) {
                UserDanaProfile(
                    id = entity.id,
                    danaPhoneNumber = entity.danaPhoneNumber,
                    danaAccountName = entity.danaAccountName,
                    qrCustomImageUrl = entity.qrCustomImageUrl,
                    notes = entity.notes,
                    isDanaActive = entity.isDanaActive,
                    totalDirectSawerCount = entity.totalDirectSawerCount,
                    sawerBalanceRupiah = entity.sawerBalanceRupiah
                )
            } else {
                UserDanaProfile(
                    id = "primary_user_dana",
                    danaPhoneNumber = "0812-9876-5432",
                    danaAccountName = "ALDI PRASETYA",
                    qrCustomImageUrl = null,
                    notes = "Dukung karya konten saya! Saweran langsung P2P via DANA 100% tanpa potongan.",
                    isDanaActive = true,
                    totalDirectSawerCount = 14,
                    sawerBalanceRupiah = 175_000.0
                )
            }
        }
    }

    suspend fun saveUserDanaProfile(
        phoneNumber: String,
        accountName: String,
        qrImageUrl: String?,
        notes: String,
        isActive: Boolean
    ) {
        val current = dao.getUserDanaProfileSync()
        val count = current?.totalDirectSawerCount ?: 14L
        val balance = current?.sawerBalanceRupiah ?: 175_000.0
        dao.upsertUserDanaProfile(
            UserDanaProfileEntity(
                id = "primary_user_dana",
                danaPhoneNumber = phoneNumber,
                danaAccountName = accountName,
                qrCustomImageUrl = qrImageUrl,
                notes = notes,
                isDanaActive = isActive,
                totalDirectSawerCount = count,
                sawerBalanceRupiah = balance
            )
        )
    }

    suspend fun incrementDirectSawerCount(addedNominalRupiah: Double = 10_000.0) {
        val current = dao.getUserDanaProfileSync()
        if (current != null) {
            dao.upsertUserDanaProfile(
                current.copy(
                    totalDirectSawerCount = current.totalDirectSawerCount + 1,
                    sawerBalanceRupiah = current.sawerBalanceRupiah + addedNominalRupiah
                )
            )
        }
    }

    suspend fun withdrawFromSawerBalanceToDana(
        amountRupiah: Double,
        targetPhone: String,
        targetName: String
    ): DanaWithdrawalResult {
        val current = dao.getUserDanaProfileSync()
        val currentBalance = current?.sawerBalanceRupiah ?: 175_000.0
        if (amountRupiah <= 0 || amountRupiah > currentBalance) {
            return DanaWithdrawalResult(
                isSuccess = false,
                amountRupiah = amountRupiah,
                targetPhoneNumber = targetPhone,
                targetAccountName = targetName,
                remainingBalanceRupiah = currentBalance,
                transactionId = "",
                message = "Saldo hasil sawer & apresiasi tidak mencukupi untuk penarikan ini."
            )
        }

        val newBalance = currentBalance - amountRupiah
        val updatedProfile = (current ?: UserDanaProfileEntity()).copy(
            danaPhoneNumber = targetPhone,
            danaAccountName = targetName,
            sawerBalanceRupiah = newBalance
        )
        dao.upsertUserDanaProfile(updatedProfile)

        val txId = "WD-DANA-${System.currentTimeMillis()}"
        val formattedRupiah = String.format(Locale("id", "ID"), "Rp %,.0f", amountRupiah)

        dao.insertCoinTransaction(
            CoinTransactionEntity(
                id = "tx_wd_${System.currentTimeMillis()}",
                title = "Tarik ke DANA: $formattedRupiah",
                amount = (amountRupiah / 8000.0).toLong().coerceAtLeast(1L),
                isEarned = false,
                description = "Pencairan hasil sawer & royalti apresiasi ke DANA $targetPhone a.n. $targetName (Biaya Admin: Rp 0)",
                iconEmoji = "💸"
            )
        )

        return DanaWithdrawalResult(
            isSuccess = true,
            amountRupiah = amountRupiah,
            targetPhoneNumber = targetPhone,
            targetAccountName = targetName,
            remainingBalanceRupiah = newBalance,
            transactionId = txId,
            message = "Penarikan $formattedRupiah berhasil ditransfer ke akun DANA $targetPhone a.n. $targetName secara instan tanpa potongan."
        )
    }

    suspend fun withdrawCoinsToDana(
        coinCount: Long,
        targetPhone: String,
        targetName: String
    ): DanaWithdrawalResult {
        val wallet = dao.getUserCoinWalletSync()
        val currentCoins = wallet?.balance ?: 500L
        if (coinCount <= 0 || coinCount > currentCoins) {
            return DanaWithdrawalResult(
                isSuccess = false,
                amountRupiah = (coinCount * 8000L).toDouble(),
                targetPhoneNumber = targetPhone,
                targetAccountName = targetName,
                remainingBalanceRupiah = 0.0,
                transactionId = "",
                message = "Saldo koin tidak mencukupi untuk penarikan."
            )
        }

        val amountRupiah = coinCount * 8000.0
        val newCoinBalance = currentCoins - coinCount
        dao.upsertCoinWallet(UserCoinWalletEntity("primary_coin_wallet", newCoinBalance))

        val txId = "WD-COIN-${System.currentTimeMillis()}"
        val formattedRupiah = String.format(Locale("id", "ID"), "Rp %,.0f", amountRupiah)

        dao.insertCoinTransaction(
            CoinTransactionEntity(
                id = "tx_wd_coin_${System.currentTimeMillis()}",
                title = "Tarik Koin ke DANA: -$coinCount Koin ($formattedRupiah)",
                amount = coinCount,
                isEarned = false,
                description = "Pencairan $coinCount koin (@Rp 8.000) ke DANA $targetPhone a.n. $targetName (Biaya Admin: Rp 0)",
                iconEmoji = "💸"
            )
        )

        return DanaWithdrawalResult(
            isSuccess = true,
            amountRupiah = amountRupiah,
            targetPhoneNumber = targetPhone,
            targetAccountName = targetName,
            remainingBalanceRupiah = newCoinBalance.toDouble(),
            transactionId = txId,
            message = "Pencairan $coinCount Koin ($formattedRupiah) berhasil ditransfer ke DANA $targetPhone secara instan!"
        )
    }

    // --- VibeCoins Internal Currency System ---
    fun getUserCoinWallet(): Flow<Long> {
        return dao.getUserCoinWallet().map { wallet ->
            if (wallet == null) {
                // Initialize with 500 bonus coins
                dao.upsertCoinWallet(UserCoinWalletEntity("primary_user_wallet", 500L))
                dao.insertCoinTransaction(
                    CoinTransactionEntity(
                        id = "tx_welcome_${System.currentTimeMillis()}",
                        title = "Bonus Selamat Datang",
                        amount = 500L,
                        isEarned = true,
                        description = "Modal awal koin interaksi & gift P2P",
                        iconEmoji = "🪙"
                    )
                )
                500L
            } else {
                wallet.balance
            }
        }
    }

    fun getAllCoinTransactions(): Flow<List<CoinTransactionEntity>> {
        return dao.getAllCoinTransactions()
    }

    suspend fun rechargeCoins(amount: Long, packageName: String) {
        val current = dao.getUserCoinWalletSync()
        val currentBalance = current?.balance ?: 500L
        val newBalance = currentBalance + amount

        dao.upsertCoinWallet(UserCoinWalletEntity("primary_user_wallet", newBalance))
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                id = "tx_recharge_${System.currentTimeMillis()}",
                title = "Konversi Mandiri: $packageName",
                amount = amount,
                isEarned = true,
                description = "Konversi mandiri tanpa gateway pihak ketiga",
                iconEmoji = "⚡"
            )
        )
    }

    suspend fun claimDailyReward(): Boolean {
        val current = dao.getUserCoinWalletSync()
        val currentBalance = current?.balance ?: 500L
        val newBalance = currentBalance + 150L

        dao.upsertCoinWallet(UserCoinWalletEntity("primary_user_wallet", newBalance))
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                id = "tx_daily_${System.currentTimeMillis()}",
                title = "Klaim Bonus Harian",
                amount = 150L,
                isEarned = true,
                description = "Bonus harian interaksi komunitas",
                iconEmoji = "🎁"
            )
        )
        return true
    }

    suspend fun redeemVoucher(code: String): Boolean {
        val upper = code.trim().uppercase()
        val (amount, label) = when (upper) {
            "VIBE2026" -> Pair(500L, "Voucher Komunitas Vibe 2026")
            "SAWER100" -> Pair(250L, "Voucher Spesial Sawer P2P")
            "CREATOR50" -> Pair(150L, "Voucher Semangat Kreator")
            "MERDEKA" -> Pair(300L, "Voucher Bebas Birokrasi")
            else -> return false
        }

        val current = dao.getUserCoinWalletSync()
        val currentBalance = current?.balance ?: 500L
        val newBalance = currentBalance + amount

        dao.upsertCoinWallet(UserCoinWalletEntity("primary_user_wallet", newBalance))
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                id = "tx_voucher_${System.currentTimeMillis()}",
                title = label,
                amount = amount,
                isEarned = true,
                description = "Penukaran kode voucher '$upper'",
                iconEmoji = "🎟️"
            )
        )
        return true
    }

    suspend fun spendCoinsForGift(gift: VibeGift, creatorName: String): Boolean {
        val current = dao.getUserCoinWalletSync()
        val currentBalance = current?.balance ?: 500L
        if (currentBalance < gift.coinCost) {
            return false
        }

        val newBalance = currentBalance - gift.coinCost
        dao.upsertCoinWallet(UserCoinWalletEntity("primary_user_wallet", newBalance))
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                id = "tx_gift_${System.currentTimeMillis()}",
                title = "Kirim Gift: ${gift.emoji} ${gift.name}",
                amount = gift.coinCost,
                isEarned = false,
                description = "Dikirim untuk kreator $creatorName",
                iconEmoji = gift.emoji
            )
        )
        return true
    }

    suspend fun rewardCoinForInteraction(reason: String, amount: Long = 5L) {
        val current = dao.getUserCoinWalletSync()
        val currentBalance = current?.balance ?: 500L
        val newBalance = currentBalance + amount

        dao.upsertCoinWallet(UserCoinWalletEntity("primary_user_wallet", newBalance))
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                id = "tx_reward_${System.currentTimeMillis()}",
                title = "Reward Interaksi",
                amount = amount,
                isEarned = true,
                description = reason,
                iconEmoji = "✨"
            )
        )
    }

    suspend fun convertCoinsWithPlatformFee(coinCount: Long): CoinConversionCalculation {
        val calc = CoinConversionCalculation(coinCount = coinCount)
        val current = dao.getUserCoinWalletSync()
        val currentBalance = current?.balance ?: 500L
        val newBalance = currentBalance + coinCount

        dao.upsertCoinWallet(UserCoinWalletEntity("primary_user_wallet", newBalance))
        dao.insertCoinTransaction(
            CoinTransactionEntity(
                id = "tx_conv_${System.currentTimeMillis()}",
                title = "Konversi Koin: +$coinCount Koin",
                amount = coinCount,
                isEarned = true,
                description = "Nilai: Rp ${calc.totalCoinValue} | Bayar: Rp ${calc.totalUserPayment} | Profit Owner: Rp ${calc.totalOwnerProfit}",
                iconEmoji = "💱"
            )
        )
        return calc
    }

    suspend fun incrementCoinsForLike(creatorUsername: String, amount: Long = 5L) {
        rewardCoinForInteraction("Like Penggemar (Tarif: Rp 0,05) ke $creatorUsername", amount)
    }

    suspend fun incrementCoinsForComment(amount: Long = 10L) {
        rewardCoinForInteraction("Komentar Penggemar (Tarif: Rp 0,05) di video", amount)
    }

    suspend fun incrementCoinsForShare(targetOrCreator: String, amount: Long = 15L) {
        rewardCoinForInteraction("Share Penggemar (Tarif: Rp 0,05) video $targetOrCreator", amount)
    }

    suspend fun incrementCoinsForBookmark(amount: Long = 5L) {
        rewardCoinForInteraction("Simpan Penggemar (Tarif: Rp 0,05) ke favorit", amount)
    }

    suspend fun incrementCoinsForFollow(creatorUsername: String, amount: Long = 5L) {
        rewardCoinForInteraction("Follow Penggemar (Tarif: Rp 0,05) ke $creatorUsername", amount)
    }

    suspend fun decrementCoinsForGift(gift: VibeGift, creatorName: String): Boolean {
        val result = spendCoinsForGift(gift, creatorName)
        if (result) {
            val notif = CreatorNotification(
                id = "gift_${System.currentTimeMillis()}",
                title = "Hadiah VibeGift Diterima!",
                description = "@saya mengirim ${gift.emoji} ${gift.name} (${gift.coinCost} Koin) untuk $creatorName",
                senderName = "@saya",
                senderAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                type = CreatorNotificationType.GIFT_RECEIVED,
                monetizationRupiah = gift.coinCost * 8000.0,
                coinsEarned = gift.coinCost,
                giftEmoji = gift.emoji,
                timeFormatted = "Baru saja"
            )
            addCreatorNotification(notif)
        }
        return result
    }

    fun addCreatorNotification(notification: CreatorNotification) {
        _creatorNotificationsFlow.value = listOf(notification) + _creatorNotificationsFlow.value
    }

    fun markAllNotificationsAsRead() {
        _creatorNotificationsFlow.value = _creatorNotificationsFlow.value.map { it.copy(isRead = true) }
    }

    suspend fun simulateRealtimeFanAlert(): CreatorNotification {
        val samples = listOf(
            Triple("Sultan Jakarta", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200", "Singa Emas"),
            Triple("Rania Cantika", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200", "Mawar Merah"),
            Triple("Gita Gamer", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200", "Roket"),
            Triple("Dimas Foodie", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200", "Sawer DANA")
        )
        val pick = samples.random()
        val notif = when (pick.third) {
            "Sawer DANA" -> {
                val amount = 25_000.0
                incrementDirectSawerCount(amount)
                CreatorNotification(
                    id = "alert_${System.currentTimeMillis()}",
                    title = "Saweran DANA P2P Masuk!",
                    description = "${pick.first} menyawer Rp 25.000 ke Barcode DANA Anda",
                    senderName = pick.first,
                    senderAvatar = pick.second,
                    type = CreatorNotificationType.SAWER_DANA,
                    monetizationRupiah = amount,
                    timeFormatted = "Baru saja"
                )
            }
            "Singa Emas" -> {
                val coinVal = 500L
                val rupiahVal = coinVal * 8000.0
                rewardCoinForInteraction("Menerima Gift Singa Emas dari ${pick.first}", coinVal)
                CreatorNotification(
                    id = "alert_${System.currentTimeMillis()}",
                    title = "Hadiah Akbar Diterima!",
                    description = "${pick.first} mengirim Singa Emas ($coinVal Koin) di video Anda",
                    senderName = pick.first,
                    senderAvatar = pick.second,
                    type = CreatorNotificationType.GIFT_RECEIVED,
                    monetizationRupiah = rupiahVal,
                    coinsEarned = coinVal,
                    giftEmoji = "🦁",
                    timeFormatted = "Baru saja"
                )
            }
            "Roket" -> {
                val coinVal = 250L
                val rupiahVal = coinVal * 8000.0
                rewardCoinForInteraction("Menerima Gift Roket dari ${pick.first}", coinVal)
                CreatorNotification(
                    id = "alert_${System.currentTimeMillis()}",
                    title = "Hadiah Roket Diterima!",
                    description = "${pick.first} mengirim Roket Luar Angkasa ($coinVal Koin)",
                    senderName = pick.first,
                    senderAvatar = pick.second,
                    type = CreatorNotificationType.GIFT_RECEIVED,
                    monetizationRupiah = rupiahVal,
                    coinsEarned = coinVal,
                    giftEmoji = "🚀",
                    timeFormatted = "Baru saja"
                )
            }
            else -> {
                val coinVal = 10L
                val rupiahVal = coinVal * 8000.0
                rewardCoinForInteraction("Menerima Gift Mawar dari ${pick.first}", coinVal)
                CreatorNotification(
                    id = "alert_${System.currentTimeMillis()}",
                    title = "Hadiah Mawar Diterima!",
                    description = "${pick.first} mengirim Mawar Merah ($coinVal Koin)",
                    senderName = pick.first,
                    senderAvatar = pick.second,
                    type = CreatorNotificationType.GIFT_RECEIVED,
                    monetizationRupiah = rupiahVal,
                    coinsEarned = coinVal,
                    giftEmoji = "🌹",
                    timeFormatted = "Baru saja"
                )
            }
        }
        addCreatorNotification(notif)
        return notif
    }
}
