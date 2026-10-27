package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.text.input.KeyboardType
import com.example.data.local.CoinTransactionEntity
import com.example.data.model.CoinConversionCalculation
import com.example.data.model.SelfRechargePackage
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokYellow
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VibeCoinWalletDialog(
    currentCoins: Long,
    transactions: List<CoinTransactionEntity>,
    onRechargeCoins: (amount: Long, packageName: String) -> Unit,
    onConvertCoins: (coinCount: Long, onResult: (CoinConversionCalculation) -> Unit) -> Unit = { _, _ -> },
    onClaimDailyReward: () -> Unit,
    onRedeemVoucher: (code: String) -> Boolean,
    onOpenPolicy: () -> Unit,
    onOpenWithdrawal: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var voucherCodeInput by remember { mutableStateOf("") }
    var voucherMessage by remember { mutableStateOf<String?>(null) }
    var selectedPackageId by remember { mutableStateOf("pack_2") }

    val packages = remember {
        listOf(
            SelfRechargePackage("pack_1", 100L, 10L, "Paket Pemula", "Hemat"),
            SelfRechargePackage("pack_2", 300L, 50L, "Paket Hiburan", "Populer"),
            SelfRechargePackage("pack_3", 600L, 150L, "Paket Sultan", "Bonus +25%"),
            SelfRechargePackage("pack_4", 1200L, 400L, "Paket Vibe Ultimate", "Best Value"),
            SelfRechargePackage("pack_5", 2500L, 1000L, "Paket Komunitas P2P", "Mega Bonus")
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(24.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TikTokYellow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🪙", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Dompet VibeCoins Mandiri",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Koin Hiburan & Interaksi Internal P2P",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_wallet_dialog_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Balance Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF2B161B),
                                    Color(0xFF1F2430),
                                    Color(0xFF142426)
                                )
                            )
                        )
                        .border(1.dp, TikTokYellow.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SALDO KOIN AKTIF",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tidak Pernah Hangus",
                                    color = Color(0xFF10B981),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "🪙 $currentCoins",
                                color = TikTokYellow,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VibeCoins",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Digunakan untuk kirim gift, like booster, sticker animasi, dan interaksi seru sesama pengguna tanpa perantara.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Navigation
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = TikTokRed,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = TikTokRed,
                            height = 2.dp
                        )
                    },
                    divider = { HorizontalDivider(color = DarkBorder) }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "💱 Alat Konversi",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) TikTokYellow else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Isi Mandiri",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) TextPrimary else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "Klaim / Voucher",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 2) TextPrimary else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Text(
                                text = "Riwayat Koin",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 3) TextPrimary else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        text = {
                            Text(
                                text = "Prinsip P2P",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 4) TikTokCyan else TextMuted
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content
                Box(modifier = Modifier.weight(1f, fill = false).height(330.dp)) {
                    when (selectedTab) {
                        0 -> {
                            // Alat Konversi Koin: 1 Koin = Rp 8.000, Pembayaran = Rp 10.000, Margin Owner = Rp 2.000
                            var coinCountInput by remember { mutableStateOf("1") }
                            var isConverting by remember { mutableStateOf(false) }
                            val currentInputVal = coinCountInput.toLongOrNull() ?: 1L
                            val coinCount = currentInputVal.coerceAtLeast(1L)
                            val calc = remember(coinCount) { CoinConversionCalculation(coinCount = coinCount) }

                            fun formatIdr(amount: Long): String {
                                return "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                // Rate formula banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkCard)
                                        .border(1.dp, TikTokYellow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "⚖️", fontSize = 16.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Skema Konversi Koin Transparan",
                                                    color = TikTokYellow,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = "Margin Owner 20%",
                                                color = TikTokCyan,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(TikTokCyan.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "• Nilai 1 Koin Pengguna: Rp 8.000\n• Pembayaran Pengguna: Rp 10.000\n• Keuntungan Owner: Rp 2.000 / koin",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Quick chips
                                Text(
                                    text = "Pilih Jumlah Koin Cepat:",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(1L, 5L, 10L, 25L, 50L, 100L).forEach { qty ->
                                        val isQtySelected = coinCount == qty
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isQtySelected) TikTokRed else DarkSurfaceElevated)
                                                .border(1.dp, if (isQtySelected) TikTokRed else DarkBorder, RoundedCornerShape(8.dp))
                                                .clickable { coinCountInput = qty.toString() }
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$qty",
                                                color = if (isQtySelected) Color.White else TextPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Stepper & Input
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            val newVal = (coinCount - 1).coerceAtLeast(1L)
                                            coinCountInput = newVal.toString()
                                        },
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(DarkSurfaceElevated)
                                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                            .testTag("decrement_coin_input_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang", tint = TextPrimary)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    OutlinedTextField(
                                        value = coinCountInput,
                                        onValueChange = { input ->
                                            val filtered = input.filter { it.isDigit() }
                                            if (filtered.length <= 6) {
                                                coinCountInput = filtered
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("coin_conversion_input_field"),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = TikTokRed,
                                            unfocusedBorderColor = DarkBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        trailingIcon = {
                                            Text(
                                                text = "Koin",
                                                color = TikTokYellow,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = {
                                            val newVal = coinCount + 1
                                            coinCountInput = newVal.toString()
                                        },
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(DarkSurfaceElevated)
                                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                            .testTag("increment_coin_input_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = TextPrimary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Real-time Breakdown Summary Card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF131822))
                                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Jumlah Koin Diperoleh:", color = TextMuted, fontSize = 11.sp)
                                            Text("${calc.coinCount} Koin", color = TikTokYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Nilai Koin (@Rp 8.000):", color = TextMuted, fontSize = 11.sp)
                                            Text(formatIdr(calc.totalCoinValue), color = TikTokCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Pembayaran Pengguna (@Rp 10.000):", color = TextMuted, fontSize = 11.sp)
                                            Text(formatIdr(calc.totalUserPayment), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 2.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "💼", fontSize = 12.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Keuntungan Owner (@Rp 2.000):", color = TikTokYellow, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                            Text(
                                                text = "+${formatIdr(calc.totalOwnerProfit)}",
                                                color = TikTokYellow,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (calc.coinCount <= 0L) return@Button
                                        isConverting = true
                                        onConvertCoins(calc.coinCount) { res ->
                                            isConverting = false
                                            Toast.makeText(
                                                context,
                                                "Konversi Berhasil! +${res.coinCount} Koin (Owner Profit: ${formatIdr(res.totalOwnerProfit)})",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    },
                                    enabled = !isConverting,
                                    colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("execute_coin_conversion_button")
                                ) {
                                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Konversi ${calc.coinCount} Koin (${formatIdr(calc.totalUserPayment)})",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        onDismiss()
                                        onOpenWithdrawal()
                                    },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DanaBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("open_dana_withdrawal_from_wallet_button")
                                ) {
                                    Text(
                                        text = "💸 Tarik Saldo Koin / Hasil Sawer ke DANA (0% Potongan)",
                                        color = DanaBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Kalkulator & Informasi Tarif Interaksi Penggemar (@ Rp 0,05)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, TikTokCyan.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "💎", fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Interaksi Komunitas Penggemar",
                                                    color = TikTokCyan,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = "Organik",
                                                color = TikTokCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(TikTokCyan.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Aktivitas penggemar (Share, Like, Komen, Simpan) mendukung kreator secara organik tanpa nilai koin.",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(DarkCard)
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("❤️ Suka (Like) Penggemar:", color = TextMuted, fontSize = 10.sp)
                                                Text("Organik (Tanpa Koin)", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("💬 Komentar Penggemar:", color = TextMuted, fontSize = 10.sp)
                                                Text("Organik (Tanpa Koin)", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("↗️ Bagikan (Share) Penggemar:", color = TextMuted, fontSize = 10.sp)
                                                Text("Organik (Tanpa Koin)", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("🔖 Simpan (Bookmark) Penggemar:", color = TextMuted, fontSize = 10.sp)
                                                Text("Organik (Tanpa Koin)", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "💡 Monetisasi utama: Sawer DANA P2P langsung (0% potongan) & Gift Koin VibeStream",
                                            color = TikTokCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        1 -> {
                            // Konversi Mandiri / Self Recharge packages
                            Column {
                                Text(
                                    text = "Pilih Paket Konversi Mandiri (Bebas Payment Gateway):",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(packages) { pack ->
                                        val isSelected = selectedPackageId == pack.id
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSelected) DarkSurfaceElevated else DarkCard)
                                                .border(
                                                    1.5.dp,
                                                    if (isSelected) TikTokRed else DarkBorder,
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable { selectedPackageId = pack.id }
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .clip(CircleShape)
                                                        .background(TikTokYellow.copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(text = "🪙", fontSize = 16.sp)
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = "${pack.coins + pack.bonusCoins} Koin",
                                                            color = TextPrimary,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        if (pack.bonusCoins > 0) {
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = "+${pack.bonusCoins} Bonus",
                                                                color = TikTokCyan,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = pack.label,
                                                        color = TextMuted,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (pack.badge.isNotBlank()) {
                                                    Text(
                                                        text = pack.badge,
                                                        color = TikTokYellow,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(TikTokYellow.copy(alpha = 0.15f))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }
                                                Icon(
                                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.AddCard,
                                                    contentDescription = null,
                                                    tint = if (isSelected) TikTokRed else TextMuted,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                val chosen = packages.firstOrNull { it.id == selectedPackageId } ?: packages.first()
                                Button(
                                    onClick = {
                                        val total = chosen.coins + chosen.bonusCoins
                                        onRechargeCoins(total, chosen.label)
                                        Toast.makeText(
                                            context,
                                            "Berhasil konversi mandiri +$total VibeCoins!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("execute_recharge_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Konversi Mandiri Sekarang (+${chosen.coins + chosen.bonusCoins} Koin)",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        2 -> {
                            // Klaim Harian & Kode Hadiah Mandiri
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                            ) {
                                // Daily Claim Card
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(DarkCard)
                                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🎁", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Bonus Harian Mandiri",
                                                color = TextPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Klaim 150 Koin Gratis setiap hari",
                                                color = TextMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            onClaimDailyReward()
                                            Toast.makeText(context, "Bonus Harian +150 Koin berhasil diklaim!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("claim_daily_coin_button")
                                    ) {
                                        Text(text = "Klaim", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Voucher Code Box
                                Text(
                                    text = "Tukar Kode Voucher Komunitas:",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = voucherCodeInput,
                                        onValueChange = {
                                            voucherCodeInput = it.uppercase()
                                            voucherMessage = null
                                        },
                                        placeholder = { Text("Contoh: VIBE2026, SAWER100", color = TextMuted, fontSize = 12.sp) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("voucher_input_field"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = TikTokRed,
                                            unfocusedBorderColor = DarkBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (voucherCodeInput.isBlank()) {
                                                voucherMessage = "Masukkan kode voucher terlebih dahulu"
                                                return@Button
                                            }
                                            val success = onRedeemVoucher(voucherCodeInput.trim())
                                            if (success) {
                                                voucherMessage = "Kode '$voucherCodeInput' berhasil ditukarkan!"
                                                voucherCodeInput = ""
                                            } else {
                                                voucherMessage = "Kode tidak valid atau sudah digunakan"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TikTokRed),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.height(52.dp).testTag("redeem_voucher_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Redeem, contentDescription = null, tint = Color.White)
                                    }
                                }

                                if (voucherMessage != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = voucherMessage!!,
                                        color = if (voucherMessage!!.contains("berhasil")) TikTokCyan else TikTokRed,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick Tips
                                Text(
                                    text = "💡 Kode promosi resmi komunitas yang aktif: 'VIBE2026' (+500 koin) dan 'SAWER100' (+250 koin)",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        3 -> {
                            // History of Coin Transactions
                            if (transactions.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Belum ada riwayat transaksi koin", color = TextMuted, fontSize = 12.sp)
                                    }
                                }
                            } else {
                                val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(transactions) { tx ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(DarkCard)
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = tx.iconEmoji, fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = tx.title,
                                                        color = TextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = "${dateFormatter.format(Date(tx.timestamp))} • ${tx.description}",
                                                        color = TextMuted,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }

                                            Text(
                                                text = if (tx.isEarned) "+${tx.amount}" else "-${tx.amount}",
                                                color = if (tx.isEarned) TikTokCyan else TikTokRed,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        4 -> {
                            // Prinsip Operasional Bebas Birokrasi & P2P
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F172A))
                                        .border(1.dp, TikTokCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = TikTokCyan, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "3 Prinsip Bebas Birokrasi VibeStream",
                                                color = TikTokCyan,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "1. Tidak Menahan Rupiah: Aplikasi tidak memegang kas rupiah penonton maupun kreator.\n" +
                                                    "2. 100% P2P Langsung: Saweran uang riil langsung scan Barcode DANA antar akun pribadi Anda.\n" +
                                                    "3. Bebas Blokir Finansial: Tidak ada syarat penarikan (minimum withdrawal) & koin tidak akan pernah hangus.",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = onOpenPolicy,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(42.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = TikTokCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Buka Dokumen Kebijakan Bebas Birokrasi", color = TextPrimary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DarkBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Footer note
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VibeStream P2P Ecosystem",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "Tutup",
                        color = TikTokRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
