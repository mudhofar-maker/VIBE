package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TikTokCyan
import com.example.ui.theme.TikTokRed
import java.util.Locale

enum class CaptionFontStyle(val label: String, val family: FontFamily, val weight: FontWeight, val fontStyle: FontStyle = FontStyle.Normal) {
    BOLD_MODERN("Tebal", FontFamily.SansSerif, FontWeight.ExtraBold),
    SERIF_ELEGANT("Elegan", FontFamily.Serif, FontWeight.Bold),
    MONO_CYBER("Cyber", FontFamily.Monospace, FontWeight.SemiBold),
    COMIC_FUNKY("Komik", FontFamily.SansSerif, FontWeight.Black),
    ITALIC_CHILL("Italic", FontFamily.SansSerif, FontWeight.Bold, FontStyle.Italic)
}

enum class CaptionBackgroundMode(val label: String) {
    TRANSPARENT("Polos"),
    SOLID_PILL("Blok Warna"),
    NEON_OUTLINE("Garis Neon"),
    FROSTED_DARK("Gelap Transparan")
}

data class VideoCaptionItem(
    val id: String = "caption_${System.currentTimeMillis()}",
    var text: String = "Ketik teks di sini ✨",
    var textColor: Color = Color(0xFF00E5FF),
    var fontStyle: CaptionFontStyle = CaptionFontStyle.BOLD_MODERN,
    var bgMode: CaptionBackgroundMode = CaptionBackgroundMode.SOLID_PILL,
    var scale: Float = 1.2f,
    var panOffset: Offset = Offset.Zero,
    var rotation: Float = 0f
)

val FULL_COLOR_PALETTE = listOf(
    Color(0xFF00E5FF), // Neon Cyan
    Color(0xFFFE2C55), // TikTok Red
    Color(0xFFFFD700), // Gold Yellow
    Color(0xFF00E676), // Emerald Green
    Color(0xFFA855F7), // Purple
    Color(0xFFFF6D00), // Electric Orange
    Color(0xFFFF1493), // Hot Pink
    Color(0xFFFFFFFF), // White
    Color(0xFF0F172A), // Midnight Dark
    Color(0xFFFF5252)  // Crimson
)

@Composable
fun DraggableZoomableCaptionText(
    caption: VideoCaptionItem,
    onUpdate: (VideoCaptionItem) -> Unit,
    onDelete: () -> Unit,
    onEditRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSelected by remember { mutableStateOf(false) }

    val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        val newScale = (caption.scale * zoomChange).coerceIn(0.5f, 4.5f)
        val newRotation = (caption.rotation + rotationChange) % 360f
        val newPan = Offset(caption.panOffset.x + panChange.x, caption.panOffset.y + panChange.y)
        onUpdate(
            caption.copy(
                scale = newScale,
                rotation = newRotation,
                panOffset = newPan
            )
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = caption.panOffset.x
                translationY = caption.panOffset.y
                scaleX = caption.scale
                scaleY = caption.scale
                rotationZ = caption.rotation
            }
            .transformable(state = transformableState)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { isSelected = !isSelected },
                    onDoubleTap = { onEditRequested() }
                )
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Caption text layout with chosen background style
        val backgroundModifier = when (caption.bgMode) {
            CaptionBackgroundMode.TRANSPARENT -> Modifier
            CaptionBackgroundMode.SOLID_PILL -> Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(caption.textColor.copy(alpha = 0.85f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
            CaptionBackgroundMode.NEON_OUTLINE -> Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xBB000000))
                .border(2.dp, caption.textColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
            CaptionBackgroundMode.FROSTED_DARK -> Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x990A0A0E))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        }

        val renderedTextColor = if (caption.bgMode == CaptionBackgroundMode.SOLID_PILL) {
            // Determine contrasting text color (black on light colors, white on dark)
            if (caption.textColor == Color(0xFF0F172A)) Color.White else Color.Black
        } else {
            caption.textColor
        }

        Box(
            modifier = backgroundModifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = caption.text,
                color = renderedTextColor,
                fontSize = 20.sp,
                fontWeight = caption.fontStyle.weight,
                fontFamily = caption.fontStyle.family,
                fontStyle = caption.fontStyle.fontStyle,
                textAlign = TextAlign.Center,
                modifier = if (caption.bgMode == CaptionBackgroundMode.TRANSPARENT) {
                    Modifier.shadow(4.dp, RoundedCornerShape(4.dp))
                } else Modifier
            )
        }

        // Active selection boundary & quick zoom / delete tools
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(1.5.dp, TikTokCyan.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
            )
        }
    }

    // Floating On-Screen Zoom & Edit Controller for Selected Caption
    AnimatedVisibility(
        visible = isSelected,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xEE0F172A))
                .border(1.dp, TikTokCyan.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ZoomIn,
                contentDescription = null,
                tint = TikTokCyan,
                modifier = Modifier.size(16.dp)
            )

            // Current Scale Display
            Text(
                text = "${String.format(Locale.US, "%.1f", caption.scale)}x",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            // Zoom In (+) Button for Mouse/Emulator Ease
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0x3300E5FF))
                    .clickable {
                        val newScale = (caption.scale + 0.3f).coerceAtMost(4.5f)
                        onUpdate(caption.copy(scale = newScale))
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Zoom In",
                    tint = TikTokCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Zoom Out (-) Button for Mouse/Emulator Ease
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0x3300E5FF))
                    .clickable {
                        val newScale = (caption.scale - 0.3f).coerceAtLeast(0.5f)
                        onUpdate(caption.copy(scale = newScale))
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Zoom Out",
                    tint = TikTokCyan,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Reset Scale & Rotation
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
                    .clickable {
                        onUpdate(caption.copy(scale = 1.0f, rotation = 0f, panOffset = Offset.Zero))
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Ukuran",
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Edit Text & Colors Button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(TikTokCyan)
                    .clickable { onEditRequested() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Teks",
                    tint = Color.Black,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Delete Caption Button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(TikTokRed.copy(alpha = 0.2f))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = TikTokRed,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun EditCaptionDialog(
    initialCaption: VideoCaptionItem,
    onDismiss: () -> Unit,
    onConfirm: (VideoCaptionItem) -> Unit
) {
    var textInput by remember { mutableStateOf(initialCaption.text) }
    var selectedColor by remember { mutableStateOf(initialCaption.textColor) }
    var selectedFontStyle by remember { mutableStateOf(initialCaption.fontStyle) }
    var selectedBgMode by remember { mutableStateOf(initialCaption.bgMode) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .border(1.dp, TikTokCyan.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tambah & Edit Teks Captions",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Text Input Field
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ketik kata-kata viral di sini...", color = TextMuted, fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("caption_text_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = TikTokCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Live Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0A0A0C))
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val previewBg = when (selectedBgMode) {
                        CaptionBackgroundMode.TRANSPARENT -> Modifier
                        CaptionBackgroundMode.SOLID_PILL -> Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(selectedColor.copy(alpha = 0.85f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                        CaptionBackgroundMode.NEON_OUTLINE -> Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                            .border(1.5.dp, selectedColor, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                        CaptionBackgroundMode.FROSTED_DARK -> Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x990A0A0E))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    }
                    val textColor = if (selectedBgMode == CaptionBackgroundMode.SOLID_PILL) {
                        if (selectedColor == Color(0xFF0F172A)) Color.White else Color.Black
                    } else selectedColor

                    Box(modifier = previewBg) {
                        Text(
                            text = if (textInput.isNotBlank()) textInput else "Pratinjau Teks",
                            color = textColor,
                            fontSize = 18.sp,
                            fontWeight = selectedFontStyle.weight,
                            fontFamily = selectedFontStyle.family,
                            fontStyle = selectedFontStyle.fontStyle
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Font Family Selector Chips
                Text(
                    text = "Gaya Font:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CaptionFontStyle.values()) { font ->
                        val isSelected = selectedFontStyle == font
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) TikTokCyan else DarkSurfaceElevated)
                                .border(1.dp, if (isSelected) TikTokCyan else DarkBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedFontStyle = font }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = font.label,
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Full Color Palette Selector
                Text(
                    text = "Palet Warna Full Color:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(FULL_COLOR_PALETTE) { color ->
                        val isColorSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isColorSelected) 3.dp else 1.dp,
                                    color = if (isColorSelected) Color.White else Color.Gray.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isColorSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (color == Color.White) Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Background Style Selector
                Text(
                    text = "Gaya Latar Teks:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CaptionBackgroundMode.values()) { mode ->
                        val isSelected = selectedBgMode == mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF1E2838) else DarkSurfaceElevated)
                                .border(1.dp, if (isSelected) TikTokCyan else DarkBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedBgMode = mode }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = mode.label,
                                color = if (isSelected) TikTokCyan else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Apply Button
                Button(
                    onClick = {
                        val cleanText = if (textInput.isBlank()) "VibeStream ✨" else textInput.trim()
                        onConfirm(
                            initialCaption.copy(
                                text = cleanText,
                                textColor = selectedColor,
                                fontStyle = selectedFontStyle,
                                bgMode = selectedBgMode
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_caption_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TikTokCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Terapkan Teks Captions",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
