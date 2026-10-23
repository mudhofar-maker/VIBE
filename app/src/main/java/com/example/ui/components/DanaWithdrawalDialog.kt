package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DanaWithdrawalResult
import com.example.data.model.UserDanaProfile
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
fun DanaWithdrawalDialog(
    userDanaProfile: UserDanaProfile,
    currentCoins: Long,
    onWithdrawSawerBalance: (amountRupiah: Double, targetPhone: String, targetName: String, onResult: (DanaWithdrawalResult) -> Unit) -> Unit,
    onWithdrawCoins: (coinCount: Long, targetPhone: String, targetName: String, onResult: (DanaWithdrawalResult) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Hasil Sawer/Apresiasi, 1: Saldo Koin

    // Destination DANA details
    var targetPhoneInput by remember { mutableStateOf(userDanaProfile.danaPhoneNumber) }
    var targetNameInput by remember { mutableStateOf(userDanaProfile.danaAccountName) }

    // Tab 0: Rupiah input
    var rupiahInputText by remember { mutableStateOf("50000") }
    val rupiahAmount = rupiahInputText.toDoubleOrNull() ?: 50000.0

    // Tab 1: Coin input
    var coinInputText by remember { mutableStateOf("10") }
    val coinCount = coinInputText.toLongOrNull() ?: 10L

    var isProcessing by remember { mutableStateOf(false) }
    var withdrawalReceipt by remember { mutableStateOf<DanaWithdrawalResult?>(null) }

    fun formatIdr(amount: Double): String {
        return "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount.toLong())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, DanaBlue.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Digital Transfer Receipt Overlay
                if (withdrawalReceipt != null) {
                    val receipt = withdrawalReceipt!!
                    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy, HH:mm 'WIB'", Locale("id", "ID")) }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00C853).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Sukses",
                                tint = Color(0xFF00C853),
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Penarikan ke DANA Berhasil!",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Dana ditransfer langsung ke akun DANA Anda",
                            color = TextMuted,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Receipt Summary Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkCard)
                                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Nominal Diterima", color = TextMuted, fontSize = 12.sp)
                                    Text(
                                        formatIdr(receipt.amountRupiah),
                                        color = Color(0xFF00C853),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("ID Transaksi", color = TextMuted, fontSize = 11.sp)
                                    Text(receipt.transactionId, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Akun Tujuan DANA", color = TextMuted, fontSize = 11.sp)
                                    Text(
                                        "${receipt.targetPhoneNumber} (${receipt.targetAccountName})",
                                        color = TikTokCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Biaya Layanan Admin", color = TextMuted, fontSize = 11.sp)
                                    Text("Rp 0 (GRATIS)", color = TikTokYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Waktu", color = TextMuted, fontSize = 11.sp)
                                    Text(dateFormatter.format(Date(receipt.timestamp)), color = TextSecondary, fontSize = 10.sp)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Status Penarikan", color = TextMuted, fontSize = 11.sp)
                                    Text("SELESAI (INSTAN)", color = Color(0xFF00C853), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = DanaBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("close_withdrawal_receipt_button")
                        ) {
                            Text("Selesai & Tutup", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    return@Surface
                }

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DanaBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💸", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Tarik Saldo ke DANA",
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "0% Admin",
                                    color = TikTokYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(TikTokYellow.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Pencairan Hasil Sawer & Apresiasi Penggemar",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_dana_withdrawal_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Destination DANA Account Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DanaBlue.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = DanaBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Rekening DANA Tujuan Pribadi",
                                    color = DanaBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Terverifikasi DANA",
                                color = Color(0xFF00C853),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = targetPhoneInput,
                                onValueChange = { targetPhoneInput = it },
                                label = { Text("Nomor HP DANA", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f).testTag("withdrawal_dana_phone_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DanaBlue,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            OutlinedTextField(
                                value = targetNameInput,
                                onValueChange = { targetNameInput = it },
                                label = { Text("Nama Pemilik DANA", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f).testTag("withdrawal_dana_name_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DanaBlue,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs for Balance Sources
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = DanaBlue,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = DanaBlue,
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
                                text = "💰 Hasil Sawer & Apresiasi",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) TextPrimary else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "🪙 Koin ke DANA (@Rp 8.000)",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) TikTokYellow else TextMuted
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // TAB 0: WITHDRAW FROM SAWER & FAN APPRECIATION EARNINGS
                if (selectedTab == 0) {
                    val availableSawerRupiah = userDanaProfile.sawerBalanceRupiah

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0D253F), Color(0xFF14243A))
                                )
                            )
                            .border(1.dp, DanaBlue.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("SALDO DAPAT DITARIK", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text("Apresiasi: Rp 0,05 / aksi", color = TikTokCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatIdr(availableSawerRupiah),
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Akumulasi saweran langsung dan royalti Like, Komen, Share, & Simpan (@Rp 0,05) dari penggemar.",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Pilih Nominal Cepat:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(10000.0, 25000.0, 50000.0, 100000.0).forEach { amt ->
                            val isChosen = rupiahAmount == amt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChosen) DanaBlue else DarkSurfaceElevated)
                                    .border(1.dp, if (isChosen) DanaBlue else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { rupiahInputText = amt.toLong().toString() }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${amt.toLong() / 1000}rb",
                                    color = if (isChosen) Color.White else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Tarik Semua button
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TikTokCyan.copy(alpha = 0.15f))
                                .border(1.dp, TikTokCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable {
                                rupiahInputText = availableSawerRupiah.toLong().toString()
                            }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Maksimal",
                                color = TikTokCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stepper Kurang / Tambah Perhitungan Realtime
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val newVal = (rupiahAmount - 5000.0).coerceAtLeast(10000.0)
                                rupiahInputText = newVal.toLong().toString()
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                .testTag("btn_minus_rupiah")
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang Rp 5rb", tint = TextPrimary)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = rupiahInputText,
                            onValueChange = { str ->
                                val clean = str.filter { it.isDigit() }
                                if (clean.length <= 9) rupiahInputText = clean
                            },
                            label = { Text("Jumlah Penarikan (Rp)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("input_rupiah_withdrawal"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DanaBlue,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            trailingIcon = {
                                Text(
                                    text = "IDR",
                                    color = DanaBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                val newVal = (rupiahAmount + 5000.0).coerceAtMost(availableSawerRupiah)
                                rupiahInputText = newVal.toLong().toString()
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                .testTag("btn_plus_rupiah")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Rp 5rb", tint = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Realtime Breakdown Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF131822))
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Jumlah Penarikan:", color = TextMuted, fontSize = 11.sp)
                                Text(formatIdr(rupiahAmount), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Biaya Admin Aplikasi:", color = TextMuted, fontSize = 11.sp)
                                Text("Rp 0 (0% Potongan)", color = Color(0xFF00C853), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Waktu Transfer:", color = TextMuted, fontSize = 11.sp)
                                Text("Instan (Real-Time)", color = TikTokCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Diterima di DANA:", color = DanaBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(formatIdr(rupiahAmount), color = DanaBlue, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val canWithdraw = rupiahAmount > 0 && rupiahAmount <= availableSawerRupiah && targetPhoneInput.isNotBlank()
                    Button(
                        onClick = {
                            if (!canWithdraw) {
                                Toast.makeText(context, "Saldo tidak mencukupi atau nomor DANA kosong", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isProcessing = true
                            onWithdrawSawerBalance(rupiahAmount, targetPhoneInput.trim(), targetNameInput.trim()) { res ->
                                isProcessing = false
                                if (res.isSuccess) {
                                    withdrawalReceipt = res
                                } else {
                                    Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        enabled = canWithdraw && !isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = DanaBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("execute_sawer_withdrawal_button")
                    ) {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isProcessing) "Memproses Transfer..." else "Tarik ${formatIdr(rupiahAmount)} ke DANA",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // TAB 1: WITHDRAW FROM VIRTUAL COINS (@ Rp 8.000 / coin)
                if (selectedTab == 1) {
                    val coinPricePerUnit = 8000.0 // Sesuai aturan: 1 coin bernilai 8000
                    val totalRupiahFromCoins = coinCount * coinPricePerUnit

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF281E15), Color(0xFF1E2430))
                                )
                            )
                            .border(1.dp, TikTokYellow.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("SALDO KOIN AKTIF", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text("Kurs: 1 Koin = Rp 8.000", color = TikTokYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🪙", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$currentCoins Koin (${formatIdr(currentCoins * coinPricePerUnit)})",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = "Pencairan koin langsung ke saldo DANA pribadi Anda dengan kurs resmi Rp 8.000 / koin tanpa biaya admin.",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Pilih Jumlah Koin Cepat:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(5L, 10L, 25L, 50L).forEach { q ->
                            val isChosen = coinCount == q
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChosen) TikTokYellow else DarkSurfaceElevated)
                                    .border(1.dp, if (isChosen) TikTokYellow else DarkBorder, RoundedCornerShape(8.dp))
                                    .clickable { coinInputText = q.toString() }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$q Koin",
                                    color = if (isChosen) Color.Black else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Semua koin
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TikTokYellow.copy(alpha = 0.15f))
                                .border(1.dp, TikTokYellow.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { coinInputText = currentCoins.toString() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Semua Koin",
                                color = TikTokYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stepper Kurang (-) dan Tambah (+) Koin Realtime
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val newVal = (coinCount - 1L).coerceAtLeast(1L)
                                coinInputText = newVal.toString()
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                .testTag("btn_minus_coins")
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang 1 Koin", tint = TextPrimary)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = coinInputText,
                            onValueChange = { str ->
                                val clean = str.filter { it.isDigit() }
                                if (clean.length <= 6) coinInputText = clean
                            },
                            label = { Text("Jumlah Koin Ditarik", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("input_coin_withdrawal"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TikTokYellow,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            trailingIcon = {
                                Text(
                                    text = "🪙 Koin",
                                    color = TikTokYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                val newVal = (coinCount + 1L).coerceAtMost(currentCoins)
                                coinInputText = newVal.toString()
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceElevated)
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                .testTag("btn_plus_coins")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah 1 Koin", tint = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Realtime Coin Conversion to DANA Breakdown
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF131822))
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Jumlah Koin Ditukar:", color = TextMuted, fontSize = 11.sp)
                                Text("$coinCount Koin", color = TikTokYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Kurs Resmi (@Rp 8.000 / koin):", color = TextMuted, fontSize = 11.sp)
                                Text(formatIdr(totalRupiahFromCoins), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Biaya Admin Aplikasi:", color = TextMuted, fontSize = 11.sp)
                                Text("Rp 0 (0% Potongan)", color = Color(0xFF00C853), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Ditransfer ke DANA:", color = DanaBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(formatIdr(totalRupiahFromCoins), color = DanaBlue, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val canWithdrawCoins = coinCount > 0 && coinCount <= currentCoins && targetPhoneInput.isNotBlank()
                    Button(
                        onClick = {
                            if (!canWithdrawCoins) {
                                Toast.makeText(context, "Jumlah koin tidak mencukupi atau nomor DANA kosong", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isProcessing = true
                            onWithdrawCoins(coinCount, targetPhoneInput.trim(), targetNameInput.trim()) { res ->
                                isProcessing = false
                                if (res.isSuccess) {
                                    withdrawalReceipt = res
                                } else {
                                    Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        enabled = canWithdrawCoins && !isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = DanaBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("execute_coin_withdrawal_button")
                    ) {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isProcessing) "Memproses Transfer..." else "Cairkan $coinCount Koin (${formatIdr(totalRupiahFromCoins)}) ke DANA",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer security note
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = TikTokCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% P2P Otomatis • Tanpa Biaya Tersembunyi • Bebas Potongan",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
