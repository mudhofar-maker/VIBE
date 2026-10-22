package com.example.data.model

data class VibeGift(
    val id: String,
    val name: String,
    val coinCost: Long,
    val emoji: String,
    val description: String,
    val colorHex: Long = 0xFFFF4B72
)

object VibeGiftCatalog {
    val gifts = listOf(
        VibeGift("gift_rose", "Mawar Segar", 5L, "🌹", "Apresiasi manis untuk kreator", 0xFFFF3B5C),
        VibeGift("gift_coffee", "Kopi Santai", 15L, "☕", "Semangat ngonten kreator", 0xFFD4885A),
        VibeGift("gift_fireworks", "Kembang Api", 50L, "🎆", "Semarakkan video pilihanmu", 0xFFFF8A00),
        VibeGift("gift_heart_gem", "Hati Berlian", 100L, "💖", "Dukungan penuh cinta", 0xFFFF458A),
        VibeGift("gift_rocket", "Roket Vibe", 250L, "🚀", "Video terbang ke FYP!", 0xFF00E5FF),
        VibeGift("gift_crown", "Mahkota Sultan", 500L, "👑", "Apresiasi level Sultan", 0xFFFFD700),
        VibeGift("gift_golden_dragon", "Naga Emas", 1000L, "🐉", "Hadiah spektakuler tiada tanding", 0xFFFFC107),
        VibeGift("gift_supercar", "Supercar Vibe", 2000L, "🏎️", "Kemewahan interaksi sejati", 0xFFE040FB)
    )
}

data class CoinTransaction(
    val id: String,
    val title: String,
    val amount: Long,
    val isEarned: Boolean, // true = masuk, false = keluar
    val timestamp: Long = System.currentTimeMillis(),
    val description: String,
    val iconEmoji: String = "🪙"
)

data class UserDanaProfile(
    val id: String = "primary_user_dana",
    val danaPhoneNumber: String = "0812-9876-5432",
    val danaAccountName: String = "ALDI PRASETYA",
    val qrCustomImageUrl: String? = null,
    val notes: String = "Saweran langsung P2P ke akun DANA saya tanpa potongan!",
    val isDanaActive: Boolean = true,
    val totalDirectSawerCount: Long = 14
)

data class CreatorDanaProfile(
    val creatorUsername: String = "@kreator",
    val creatorName: String = "Kreator",
    val danaPhoneNumber: String = "0812-3456-7890",
    val danaAccountName: String = "Kreator VibeStream",
    val qrCustomImageUrl: String = "",
    val notes: String = "Saweran langsung P2P ke akun DANA saya tanpa potongan!",
    val isDanaActive: Boolean = true,
    val totalDirectSawerCount: Long = 12
)

data class VibeGiftAnimation(
    val id: String,
    val gift: VibeGift,
    val senderName: String = "@saya",
    val receiverName: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SelfRechargePackage(
    val id: String,
    val coins: Long,
    val bonusCoins: Long = 0,
    val label: String,
    val badge: String = ""
)
