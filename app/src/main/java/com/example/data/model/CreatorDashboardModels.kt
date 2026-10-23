package com.example.data.model

import java.text.NumberFormat
import java.util.Locale

enum class CreatorTimeRange(val label: String, val daysCount: Int) {
    LAST_7_DAYS("7 Hari Terakhir", 7),
    LAST_30_DAYS("30 Hari Terakhir", 30),
    ALL_TIME("Sepanjang Waktu", 90)
}

enum class ContentFilterType(val label: String) {
    ALL("Semua Konten"),
    SHORTS("Shorts (9:16)"),
    LONG_VIDEO("Video Panjang (16:9)")
}

data class DailyMetricPoint(
    val dayLabel: String,
    val views: Long,
    val earningsRupiah: Double,
    val engagement: Long
)

data class DailyEarningsPoint(
    val dayIndex: Int,
    val dateLabel: String,
    val fullDate: String,
    val totalEarningsRupiah: Double,
    val sawerDanaRupiah: Double,
    val fanRoyaltyRupiah: Double,
    val coinGiftsRupiah: Double
) {
    fun formattedTotal(): String = CreatorEarningsSummary.formatIdr(totalEarningsRupiah)
    fun formattedSawer(): String = CreatorEarningsSummary.formatIdr(sawerDanaRupiah)
    fun formattedRoyalty(): String = CreatorEarningsSummary.formatIdr(fanRoyaltyRupiah)
    fun formattedCoinGift(): String = CreatorEarningsSummary.formatIdr(coinGiftsRupiah)
}

data class ThirtyDayEarningsTrend(
    val points: List<DailyEarningsPoint>,
    val total30DaysEarnings: Double,
    val averageDailyEarnings: Double,
    val peakEarningsPoint: DailyEarningsPoint,
    val lowestEarningsPoint: DailyEarningsPoint
) {
    fun formattedTotal(): String = CreatorEarningsSummary.formatIdr(total30DaysEarnings)
    fun formattedAverage(): String = CreatorEarningsSummary.formatIdr(averageDailyEarnings)

    companion object {
        fun generateDefault(baseBalanceRupiah: Double = 175_000.0): ThirtyDayEarningsTrend {
            // Generate 30 points of daily earnings with realistic creator momentum
            val dailyPoints = (1..30).map { day ->
                val base = 12_000.0 + (day * 1_100.0)
                // Add weekend and viral spikes (e.g. day 7, 14, 21, 28)
                val spike = if (day % 7 == 0 || day == 23 || day == 27) 22_000.0 else if (day % 3 == 0) 8_000.0 else 0.0
                val variance = ((day * 739) % 9) * 1_200.0
                val total = base + spike + variance

                val sawerRatio = 0.65
                val fanRatio = 0.20
                val coinRatio = 0.15

                val sawer = (total * sawerRatio).coerceAtLeast(5_000.0)
                val fan = (total * fanRatio).coerceAtLeast(2_000.0)
                val coin = (total * coinRatio).coerceAtLeast(1_500.0)

                val dayNum = day.toString().padStart(2, '0')
                DailyEarningsPoint(
                    dayIndex = day,
                    dateLabel = "$day Okt",
                    fullDate = "Hari ke-$day • $dayNum Oktober 2026",
                    totalEarningsRupiah = sawer + fan + coin,
                    sawerDanaRupiah = sawer,
                    fanRoyaltyRupiah = fan,
                    coinGiftsRupiah = coin
                )
            }

            val total = dailyPoints.sumOf { it.totalEarningsRupiah }
            val avg = total / dailyPoints.size
            val peak = dailyPoints.maxByOrNull { it.totalEarningsRupiah } ?: dailyPoints.last()
            val lowest = dailyPoints.minByOrNull { it.totalEarningsRupiah } ?: dailyPoints.first()

            return ThirtyDayEarningsTrend(
                points = dailyPoints,
                total30DaysEarnings = total,
                averageDailyEarnings = avg,
                peakEarningsPoint = peak,
                lowestEarningsPoint = lowest
            )
        }
    }
}

data class CreatorEarningsSummary(
    val totalEarningsRupiah: Double,
    val directSawerRupiah: Double,
    val fanRoyaltyRupiah: Double,
    val coinGiftRupiah: Double,
    val withdrawableRupiah: Double,
    val growthRatePercent: Double = 18.5,
    val totalSawerTransactions: Long = 14L
) {
    fun formattedTotal(): String = formatIdr(totalEarningsRupiah)
    fun formattedSawer(): String = formatIdr(directSawerRupiah)
    fun formattedRoyalty(): String = formatIdr(fanRoyaltyRupiah)
    fun formattedCoinGift(): String = formatIdr(coinGiftRupiah)
    fun formattedWithdrawable(): String = formatIdr(withdrawableRupiah)

    companion object {
        fun formatIdr(amount: Double): String {
            return "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount.toLong())
        }
    }
}

data class CreatorPerformanceSummary(
    val totalViews: Long,
    val totalLikes: Long,
    val totalComments: Long,
    val totalShares: Long,
    val totalBookmarks: Long,
    val engagementRate: Double,
    val avgWatchTimeSeconds: Int = 48,
    val totalFollowers: Long = 1842L,
    val dailyTrends: List<DailyMetricPoint> = emptyList()
) {
    fun formattedViews(): String = VideoItem.formatCount(totalViews)
    fun formattedLikes(): String = VideoItem.formatCount(totalLikes)
    fun formattedComments(): String = VideoItem.formatCount(totalComments)
    fun formattedShares(): String = VideoItem.formatCount(totalShares)
    fun formattedBookmarks(): String = VideoItem.formatCount(totalBookmarks)
    fun formattedEngagementRate(): String = String.format(Locale.US, "%.1f%%", engagementRate)
    fun formattedAvgWatchTime(): String = "${avgWatchTimeSeconds / 60}m ${avgWatchTimeSeconds % 60}s"
}

data class VideoPerformanceDetail(
    val videoId: String,
    val title: String,
    val category: String,
    val isLongVideo: Boolean,
    val durationFormatted: String,
    val viewsCount: Long,
    val likesCount: Long,
    val commentsCount: Long,
    val sharesCount: Long,
    val bookmarksCount: Long,
    val estimatedEarningsRupiah: Double,
    val engagementRate: Double,
    val avgRetentionPercentage: Int = 68,
    val fypTrafficPercentage: Int = 82,
    val searchTrafficPercentage: Int = 11,
    val profileTrafficPercentage: Int = 7
) {
    fun formattedEarnings(): String = CreatorEarningsSummary.formatIdr(estimatedEarningsRupiah)
    fun formattedEngagement(): String = String.format(Locale.US, "%.1f%%", engagementRate)
}
