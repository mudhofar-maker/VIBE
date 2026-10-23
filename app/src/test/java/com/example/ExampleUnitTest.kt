package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCoinConversionCalculationBusinessRule() {
    // 1 coin: nilai 8000, pembayaran 10000, owner margin 2000
    val singleCoin = com.example.data.model.CoinConversionCalculation(coinCount = 1L)
    assertEquals(8000L, singleCoin.totalCoinValue)
    assertEquals(10000L, singleCoin.totalUserPayment)
    assertEquals(2000L, singleCoin.totalOwnerProfit)

    // 15 coins: nilai 120.000, pembayaran 150.000, owner margin 30.000
    val bulkCoins = com.example.data.model.CoinConversionCalculation(coinCount = 15L)
    assertEquals(120_000L, bulkCoins.totalCoinValue)
    assertEquals(150_000L, bulkCoins.totalUserPayment)
    assertEquals(30_000L, bulkCoins.totalOwnerProfit)
  }

  @Test
  fun testFanInteractionEconomics() {
    // Rp 0.05 per aksi penggemar
    val pricePerAction = com.example.data.model.FanInteractionEconomics.PRICE_PER_INTERACTION_RUPIAH
    assertEquals(0.05, pricePerAction, 0.0001)

    val totalInteractions = 2000L
    val totalRupiah = com.example.data.model.FanInteractionEconomics.calculateRupiah(totalInteractions)
    assertEquals(100.0, totalRupiah, 0.0001)
  }

  @Test
  fun testCreatorEarningsSummaryFormatting() {
    val summary = com.example.data.model.CreatorEarningsSummary(
      totalEarningsRupiah = 245_000.0,
      directSawerRupiah = 175_000.0,
      fanRoyaltyRupiah = 30_000.0,
      coinGiftRupiah = 40_000.0,
      withdrawableRupiah = 175_000.0,
      growthRatePercent = 24.8,
      totalSawerTransactions = 14L
    )
    assertTrue(summary.formattedTotal().contains("245.000"))
    assertTrue(summary.formattedSawer().contains("175.000"))
    assertTrue(summary.formattedRoyalty().contains("30.000"))
    assertTrue(summary.formattedCoinGift().contains("40.000"))
    assertEquals(175_000.0, summary.withdrawableRupiah, 0.01)
  }

  @Test
  fun testCreatorPerformanceSummaryMetrics() {
    val performance = com.example.data.model.CreatorPerformanceSummary(
      totalViews = 100_000L,
      totalLikes = 8_500L,
      totalComments = 1_200L,
      totalShares = 300L,
      totalBookmarks = 500L,
      engagementRate = 10.5,
      avgWatchTimeSeconds = 54,
      totalFollowers = 1842L
    )
    assertEquals("100.0K", performance.formattedViews())
    assertEquals("8.5K", performance.formattedLikes())
    assertEquals("10.5%", performance.formattedEngagementRate())
    assertEquals("0m 54s", performance.formattedAvgWatchTime())
  }

  @Test
  fun testThirtyDayEarningsTrendGeneration() {
    val trend = com.example.data.model.ThirtyDayEarningsTrend.generateDefault(175_000.0)
    assertEquals(30, trend.points.size)
    assertTrue(trend.total30DaysEarnings > 0)
    assertTrue(trend.averageDailyEarnings > 0)
    assertNotNull(trend.peakEarningsPoint)
    assertTrue(trend.peakEarningsPoint.totalEarningsRupiah >= trend.lowestEarningsPoint.totalEarningsRupiah)
    assertTrue(trend.formattedTotal().startsWith("Rp "))
    assertTrue(trend.formattedAverage().startsWith("Rp "))
  }
}
