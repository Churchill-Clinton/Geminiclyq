package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AvatarColors
import com.example.ui.theme.BgDark
import com.example.ui.theme.StatusGreen

@Composable
fun AvatarBadge(
    letter: String,
    colorIndex: Int = 0,
    size: Dp = 42.dp,
    fontSize: Int = 16,
    isOnline: Boolean = false,
    hasGlowRing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = AvatarColors.getOrElse(colorIndex.coerceAtLeast(0) % AvatarColors.size) { AvatarColors[0] }
    val brush = Brush.linearGradient(listOf(colors.first.copy(alpha = 0.85f), colors.second.copy(alpha = 0.85f)))

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val ringModifier = if (hasGlowRing) {
            Modifier
                .size(size)
                .border(1.5.dp, AccentBlue, CircleShape)
        } else {
            Modifier.size(size)
        }

        Box(
            modifier = ringModifier
                .clip(CircleShape)
                .background(brush),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize.sp
            )
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .size((size.value * 0.28f).coerceAtLeast(10f).dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .border(2.dp, BgDark, CircleShape)
                    .background(StatusGreen, CircleShape)
            )
        }
    }
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier, size: Dp = 14.dp) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Verified Account",
        tint = AccentBlue,
        modifier = modifier.size(size)
    )
}
