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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.CreatorNotification
import com.example.data.model.CreatorNotificationCategory
import com.example.data.model.CreatorNotificationType
import com.example.ui.theme.DarkBackground
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

@Composable
fun CreatorNotificationCenterDialog(
    notifications: List<CreatorNotification>,
    onMarkAllAsRead: () -> Unit,
    onSimulateAlert: (onFinished: (CreatorNotification) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(CreatorNotificationCategory.ALL) }
    var latestLiveAlert by remember { mutableStateOf<CreatorNotification?>(null) }

    val filteredNotifications = remember(notifications, selectedCategory) {
        if (selectedCategory == CreatorNotificationCategory.ALL) {
            notifications
        } else {
            notifications.filter { it.category == selectedCategory }
        }
    }

    val unreadCount = notifications.count { !it.isRead }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(650.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.2.dp, TikTokCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("creator_notification_center_dialog"),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TikTokCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (unreadCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = null,
                                tint = if (unreadCount > 0) TikTokCyan else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Pusat Notifikasi",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (unreadCount > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(TikTokRed)
                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "$unreadCount Baru",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Pemberitahuan Hadiah & Royalti Real-time",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_notification_center_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                // Actions Bar: Mark Read & Test Live Alert
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onMarkAllAsRead()
                            Toast.makeText(context, "Semua notifikasi ditandai dibaca", Toast.LENGTH_SHORT).show()
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("mark_all_read_button")
                    ) {
                        Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, tint = TikTokCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tandai Dibaca", color = TikTokCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            onSimulateAlert { newNotif ->
                                latestLiveAlert = newNotif
                                Toast.makeText(context, "⚡ Notifikasi & Monetisasi Real-time Masuk!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TikTokYellow),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp).testTag("simulate_realtime_alert_button")
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Uji Notifikasi Real-time", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Live Toast Banner when event simulated
                AnimatedVisibility(visible = latestLiveAlert != null) {
                    val alert = latestLiveAlert
                    if (alert != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF00C853).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFF00C853), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = alert.giftEmoji ?: "🎉", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(alert.title, color = Color(0xFF00C853), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(alert.description, color = TextPrimary, fontSize = 10.sp, maxLines = 1)
                                    }
                                }
                                Text(
                                    text = alert.formattedMonetization() ?: "+Rp",
                                    color = TikTokYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Tabs
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(CreatorNotificationCategory.values()) { category ->
                        val isSelected = category == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) TikTokCyan else DarkSurfaceElevated)
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("notif_filter_${category.name}")
                        ) {
                            Text(
                                text = category.label,
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)

                // Notification Items List
                if (filteredNotifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔔", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Belum Ada Notifikasi",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Semua aktivitas hadiah & royalti akan muncul di sini.",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredNotifications, key = { it.id }) { notif ->
                            CreatorNotificationCard(notification = notif)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorNotificationCard(notification: CreatorNotification) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (notification.isRead) DarkSurfaceElevated else DarkCard)
            .border(
                1.dp,
                if (!notification.isRead) TikTokCyan.copy(alpha = 0.4f) else DarkBorder,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
            .testTag("notif_card_${notification.id}")
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Sender Avatar with badge
            Box(modifier = Modifier.size(44.dp)) {
                AsyncImage(
                    model = notification.senderAvatar,
                    contentDescription = notification.senderName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )

                // Type Icon Badge
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            when (notification.type) {
                                CreatorNotificationType.GIFT_RECEIVED -> TikTokYellow
                                CreatorNotificationType.SAWER_DANA -> DanaBlue
                                CreatorNotificationType.LIKE_INTERACTION -> TikTokRed
                                CreatorNotificationType.COMMENT_INTERACTION -> Color(0xFF00C853)
                                CreatorNotificationType.SHARE_INTERACTION -> TikTokCyan
                                CreatorNotificationType.BOOKMARK_INTERACTION -> TikTokYellow
                                CreatorNotificationType.NEW_FOLLOWER -> TikTokCyan
                                CreatorNotificationType.WITHDRAWAL_PAID -> DanaBlue
                            }
                        )
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    if (notification.giftEmoji != null) {
                        Text(text = notification.giftEmoji, fontSize = 10.sp)
                    } else {
                        Icon(
                            imageVector = when (notification.type) {
                                CreatorNotificationType.SAWER_DANA -> Icons.Default.Payment
                                CreatorNotificationType.LIKE_INTERACTION -> Icons.Default.Favorite
                                CreatorNotificationType.COMMENT_INTERACTION -> Icons.Default.RecordVoiceOver
                                CreatorNotificationType.NEW_FOLLOWER -> Icons.Default.PersonAdd
                                else -> Icons.Default.MonetizationOn
                            },
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold
                    )

                    Text(
                        text = notification.timeFormatted,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = notification.description,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                // Monetization Pill & Video reference
                if (notification.monetizationRupiah > 0.0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF00C853).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "💰 ${notification.formattedMonetization()}",
                                color = Color(0xFF00C853),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (notification.coinsEarned > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TikTokYellow.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🪙 +${notification.coinsEarned} Koin",
                                    color = TikTokYellow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (!notification.targetVideoTitle.isNullOrBlank()) {
                            Text(
                                text = "• ${notification.targetVideoTitle}",
                                color = TextMuted,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
