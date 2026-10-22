package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Official DANA Brand Palette
val DanaBlue = Color(0xFF118EEA)
val DanaDarkBlue = Color(0xFF0066B2)
val DanaLightBlue = Color(0xFFE8F4FD)

@Composable
fun DanaQrCodeCard(
    danaName: String,
    danaNumber: String,
    qrCustomImageUrl: String = "",
    creatorUsername: String = "",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // DANA Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DanaBlue)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "D",
                            color = DanaBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DANA P2P",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Dompet Digital Indonesia",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 10.sp
                        )
                    }
                }

                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified P2P",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Direct Tip",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Account Name & Number Banner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = danaName.uppercase(),
                    color = Color(0xFF1E293B),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                if (creatorUsername.isNotBlank()) {
                    Text(
                        text = creatorUsername,
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = danaNumber,
                    color = DanaDarkBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // QR Code Rendering Area
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(2.dp, DanaLightBlue, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (qrCustomImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = qrCustomImageUrl,
                        contentDescription = "QR DANA Kreator",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(190.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    // Crisp realistic DANA QR Code Canvas with corner alignment targets and center DANA badge
                    DanaGeneratedQrCanvas(
                        modifier = Modifier.size(190.dp),
                        seed = danaNumber + danaName
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer instructions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = DanaBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Scan QR via aplikasi DANA untuk transfer instan",
                    color = Color(0xFF475569),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DanaGeneratedQrCanvas(
    modifier: Modifier = Modifier,
    seed: String = "08123456789"
) {
    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val totalModules = 23
            val moduleSize = size.width / totalModules
            val hash = seed.hashCode()

            // Draw Finder Patterns (Top-Left, Top-Right, Bottom-Left)
            drawFinderPattern(Offset(0f, 0f), moduleSize, 7)
            drawFinderPattern(Offset((totalModules - 7) * moduleSize, 0f), moduleSize, 7)
            drawFinderPattern(Offset(0f, (totalModules - 7) * moduleSize), moduleSize, 7)

            // Draw pseudo-random data modules based on seed hash
            for (r in 0 until totalModules) {
                for (c in 0 until totalModules) {
                    val inFinderTL = r < 8 && c < 8
                    val inFinderTR = r < 8 && c >= totalModules - 8
                    val inFinderBL = r >= totalModules - 8 && c < 8
                    val inCenter = r in 9..13 && c in 9..13

                    if (!inFinderTL && !inFinderTR && !inFinderBL && !inCenter) {
                        val bit = ((hash xor (r * 31 + c * 17)) and 1) == 0
                        if (bit) {
                            drawRoundRect(
                                color = Color(0xFF0F172A),
                                topLeft = Offset(c * moduleSize + 0.6f, r * moduleSize + 0.6f),
                                size = Size(moduleSize - 1.2f, moduleSize - 1.2f),
                                cornerRadius = CornerRadius(moduleSize * 0.25f, moduleSize * 0.25f)
                            )
                        }
                    }
                }
            }
        }

        // Center DANA Logo Badge
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, DanaBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(DanaBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "D",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFinderPattern(
    topLeft: Offset,
    moduleSize: Float,
    modules: Int
) {
    val outerSize = modules * moduleSize
    // Outer black frame
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = topLeft,
        size = Size(outerSize, outerSize),
        cornerRadius = CornerRadius(moduleSize * 1.2f, moduleSize * 1.2f)
    )
    // Inner white frame
    val whiteOffset = topLeft + Offset(moduleSize, moduleSize)
    val whiteSize = (modules - 2) * moduleSize
    drawRoundRect(
        color = Color.White,
        topLeft = whiteOffset,
        size = Size(whiteSize, whiteSize),
        cornerRadius = CornerRadius(moduleSize * 0.8f, moduleSize * 0.8f)
    )
    // Center black box
    val centerOffset = topLeft + Offset(moduleSize * 2, moduleSize * 2)
    val centerSize = (modules - 4) * moduleSize
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = centerOffset,
        size = Size(centerSize, centerSize),
        cornerRadius = CornerRadius(moduleSize * 0.5f, moduleSize * 0.5f)
    )
}
