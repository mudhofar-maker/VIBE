package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.UserDanaProfile
import com.example.data.model.VideoItem
import com.example.ui.components.DanaBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import com.example.ui.theme.TikTokYellow

@Composable
fun ProfileScreen(
    videos: List<VideoItem>,
    userDanaProfile: UserDanaProfile? = null,
    userCoins: Long = 500L,
    onVideoClick: (VideoItem) -> Unit,
    onOpenDanaEtalase: () -> Unit = {},
    onOpenWallet: () -> Unit = {},
    onOpenPolicy: () -> Unit = {},
    onOpenWithdrawal: () -> Unit = {},
    onOpenCreatorDashboard: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Semua", "Shorts", "Panjang", "Disukai")

    val userVideos = videos.filter { it.isUserCreated }
    val likedVideos = videos.filter { it.isLiked }

    val currentTabVideos = when (selectedTabIndex) {
        0 -> if (userVideos.isNotEmpty()) userVideos else videos.take(6)
        1 -> {
            val s = userVideos.filter { !it.isLongVideo }
            if (s.isNotEmpty()) s else videos.filter { !it.isLongVideo }
        }
        2 -> {
            val l = userVideos.filter { it.isLongVideo }
            if (l.isNotEmpty()) l else videos.filter { it.isLongVideo }
        }
        else -> likedVideos
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Top app bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    Toast.makeText(context, "Cari teman baru...", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Tambah Teman",
                    tint = TextPrimary
                )
            }

            Text(
                text = userDanaProfile?.danaAccountName ?: "Aldi Prasetya",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = {
                    Toast.makeText(context, "Pengaturan & Privasi VibeStream", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("profile_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = TextPrimary
                )
            }
        }

        // Profile Header Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, TikTokCyan, CircleShape)
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                    contentDescription = "Foto Profil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(90.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@saya",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = "Terverifikasi",
                    tint = TikTokCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Followers / Following / Likes stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(label = "Mengikuti", value = "42")
                ProfileStat(label = "Pengikut", value = "1.8K")
                val totalLikesTally = (likedVideos.size * 340) + (userVideos.size * 12) + 120
                ProfileStat(label = "Suka", value = VideoItem.formatCount(totalLikesTally.toLong()))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons (Edit profile & Share profile)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Edit Profil", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Edit Profil", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Tautan profil disalin!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Bagikan Profil", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bio
            Text(
                text = "Kreator konten di VibeStream 🇮🇩 Suka video kuliner nusantara, dance, dan seru-seruan! Klik tombol + untuk video baru.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // --- DASHBOARD KREATOR STUDIO BANNER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(
                                Color(0xFF2C1930),
                                Color(0xFF132238)
                            )
                        )
                    )
                    .border(1.dp, TikTokCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onOpenCreatorDashboard() }
                    .padding(14.dp)
                    .testTag("profile_creator_dashboard_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TikTokCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎨", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Studio & Dashboard Kreator",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PRO",
                                    color = TikTokYellow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(TikTokYellow.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = "Pantau saldo, analisis performa, & kelola konten",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Buka Dashboard",
                        tint = TikTokCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- ETALASE BARCODE DANA PRIBADI (P2P DIRECT) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.5.dp, DanaBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onOpenDanaEtalase() }
                    .padding(14.dp)
                    .testTag("profile_dana_etalase_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DanaBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Etalase Barcode DANA Pribadi",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "P2P",
                                        color = DanaBlue,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DanaBlue.copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text(
                                    text = "${userDanaProfile?.danaPhoneNumber ?: "0812-9876-5432"} • ${userDanaProfile?.danaAccountName ?: "ALDI PRASETYA"}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = onOpenDanaEtalase,
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DanaBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("manage_dana_barcode_button")
                            ) {
                                Text("Atur Barcode", color = DanaBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onOpenWithdrawal,
                                colors = ButtonDefaults.buttonColors(containerColor = DanaBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("profile_withdraw_dana_button")
                            ) {
                                Text("💸 Tarik Saldo", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Saldo Sawer & Apresiasi Terkumpul
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkBackground.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Saldo Sawer Siap Tarik:", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    "Rp " + java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format((userDanaProfile?.sawerBalanceRupiah ?: 175_000.0).toLong()),
                                    color = Color(0xFF00C853),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Text(
                                text = "Bebas Biaya Admin (0%)",
                                color = TikTokYellow,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TikTokYellow.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ 100% saweran langsung masuk ke akun DANA Anda",
                            color = TikTokCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${userDanaProfile?.totalDirectSawerCount ?: 14} Saweran Diterima",
                            color = TikTokYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- DOMPET VIBECOINS & PRINSIP BEBAS BIROKRASI ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Coin Balance Card
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, TikTokYellow.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clickable { onOpenWallet() }
                        .padding(12.dp)
                        .testTag("profile_coin_wallet_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🪙 VibeCoins", color = TikTokYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Isi Mandiri >", color = TikTokCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$userCoins Koin",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Koin hiburan & gift internal",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                // Bureaucracy Free Policy Button Card
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                        .clickable { onOpenPolicy() }
                        .padding(12.dp)
                        .testTag("profile_policy_card")
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = TikTokCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Bebas Birokrasi", color = TikTokCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "0% Potongan", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Tanpa tahan uang", color = TextMuted, fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- ROYALTI INTERAKSI PENGGEMAR CARD (@ Rp 0,05) ---
            val totalUserInteractions = userVideos.sumOf { it.totalFanInteractionsCount } + 1850L
            val totalUserInteractionsRupiah = totalUserInteractions * 0.05

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF131A26))
                    .border(1.dp, TikTokCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { onOpenWallet() }
                    .padding(14.dp)
                    .testTag("profile_fan_interactions_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💎", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Interaksi Komunitas Penggemar",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Like, Komen, Share, & Simpan",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Text(
                            text = "Organik",
                            color = TikTokCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TikTokCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Aksi Penggemar",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "${VideoItem.formatCount(totalUserInteractions)} Aksi",
                                color = TikTokCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Estimasi Nilai Apresiasi",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = String.format(java.util.Locale("id", "ID"), "Rp %.2f", totalUserInteractionsRupiah),
                                color = TikTokYellow,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onOpenWithdrawal,
                        colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TikTokCyan.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .testTag("withdraw_fan_interactions_button")
                    ) {
                        Text(
                            text = "💸 Cairkan Apresiasi ke DANA (0% Potongan)",
                            color = TikTokCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Tabs (Videos, Liked, Bookmarked)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkBackground,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color.White,
                    height = 2.dp
                )
            },
            divider = { HorizontalDivider(color = DarkBorder, thickness = 0.5.dp) }
        ) {
            tabTitles.forEachIndexed { index, title ->
                val icon = when (index) {
                    0 -> Icons.Default.GridView
                    1 -> Icons.Default.Bolt
                    2 -> Icons.Default.VideoLibrary
                    else -> Icons.Default.Favorite
                }
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (selectedTabIndex == index) {
                                when (index) {
                                    1 -> TikTokCyan
                                    2 -> TikTokRed
                                    3 -> TikTokRed
                                    else -> Color.White
                                }
                            } else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) Color.White else TextMuted
                        )
                    }
                )
            }
        }

        // Videos Grid (3 columns)
        if (currentTabVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (selectedTabIndex) {
                        0 -> "Belum ada video yang diunggah. Ketuk tombol '+' untuk mulai berkarya!"
                        1 -> "Belum ada video yang disukai. Ketuk suka di video yang Anda tonton!"
                        else -> "Belum ada video yang disimpan ke favorit."
                    },
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentTabVideos, key = { it.id }) { video ->
                    Box(
                        modifier = Modifier
                            .height(150.dp)
                            .clickable { onVideoClick(video) }
                    ) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Play count in corner
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (video.isLongVideo) video.formattedViews() else video.formattedLikes(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Duration badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.Black.copy(alpha = 0.8f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = video.durationFormatted,
                                color = if (video.isLongVideo) TikTokCyan else Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp
        )
    }
}
