package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserDanaProfile
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed

@Composable
fun DanaProfileEtalaseDialog(
    initialProfile: UserDanaProfile? = null,
    currentProfile: UserDanaProfile? = initialProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (phone: String, name: String, qrUrl: String?, notes: String, isActive: Boolean) -> Unit
) {
    val activeProfile = currentProfile ?: initialProfile ?: UserDanaProfile()
    val context = LocalContext.current
    var danaPhoneNumber by remember { mutableStateOf(activeProfile.danaPhoneNumber) }
    var danaAccountName by remember { mutableStateOf(activeProfile.danaAccountName) }
    var qrCustomImageUrl by remember { mutableStateOf<String?>(activeProfile.qrCustomImageUrl) }
    var notes by remember { mutableStateOf(activeProfile.notes) }
    var isDanaActive by remember { mutableStateOf(activeProfile.isDanaActive) }
    var isEditing by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            qrCustomImageUrl = uri.toString()
            Toast.makeText(context, "Gambar QR Code DANA dipilih!", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp)),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DanaBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("D", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Etalase Barcode DANA P2P",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Kelola QR Code pribadi untuk saweran langsung",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(30.dp).testTag("close_dana_etalase")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Active status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tampilkan Etalase di Profil Publik",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isDanaActive) "Aktif: Penonton dapat langsung scan & sawer" else "Nonaktif: Tombol sawer disembunyikan",
                            color = if (isDanaActive) TikTokCyan else TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isDanaActive,
                        onCheckedChange = { isDanaActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DanaBlue
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Preview of the DANA Barcode Card
                DanaQrCodeCard(
                    danaName = danaAccountName.ifBlank { "NAMA AKUN DANA" },
                    danaNumber = danaPhoneNumber.ifBlank { "0812-xxxx-xxxx" },
                    qrCustomImageUrl = qrCustomImageUrl ?: "",
                    creatorUsername = "@saya (Profil Anda)"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Upload custom screenshot button or remove
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch("image/*")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DanaBlue),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DanaBlue),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = DanaBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unggah Gambar QR", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (!qrCustomImageUrl.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { qrCustomImageUrl = null },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text("Reset ke Vector", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input fields
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // DANA Phone Number
                    OutlinedTextField(
                        value = danaPhoneNumber,
                        onValueChange = { danaPhoneNumber = it },
                        label = { Text("Nomor Akun DANA Anda") },
                        placeholder = { Text("contoh: 0812-3456-7890") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = DanaBlue,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("dana_phone_input")
                    )

                    // DANA Account Registered Name
                    OutlinedTextField(
                        value = danaAccountName,
                        onValueChange = { danaAccountName = it },
                        label = { Text("Nama Pemilik Akun DANA") },
                        placeholder = { Text("contoh: BUDI PRATAMA") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = DanaBlue,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("dana_name_input")
                    )

                    // Notes / Pesan Saweran
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Pesan Saweran untuk Penonton") },
                        placeholder = { Text("Saweran langsung P2P ke DANA tanpa potongan!") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = DanaBlue,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("dana_note_input")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Info note
                Surface(
                    color = Color(0xFF0C1929),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = DanaBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aplikasi VibeStream tidak memotong atau menahan sepeser pun uang. Semua saweran 100% langsung masuk ke akun DANA pribadi Anda.",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save button
                Button(
                    onClick = {
                        if (danaPhoneNumber.isBlank() || danaAccountName.isBlank()) {
                            Toast.makeText(context, "Mohon lengkapi nomor dan nama akun DANA", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onSaveProfile(
                            danaPhoneNumber,
                            danaAccountName,
                            qrCustomImageUrl,
                            notes,
                            isDanaActive
                        )
                        Toast.makeText(context, "Etalase Barcode DANA berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DanaBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_dana_profile_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simpan Etalase Barcode DANA",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
