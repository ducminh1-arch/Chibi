package com.chibidoll.dressup.ui.dressup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.PastelBlue
import com.chibidoll.dressup.ui.theme.PastelPurple
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextPrimary

@Composable
fun TopControlBar(
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onResetClick: () -> Unit,
    onRandomizeClick: () -> Unit,
    onBackgroundClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button (Top-left)
        CircleCuteButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            backgroundColor = SurfaceWhite,
            tint = TextPrimary,
            onClick = onBackClick
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Background Picker Button
            CircleCuteButton(
                icon = Icons.Default.Image,
                contentDescription = "Background",
                backgroundColor = PastelBlue,
                tint = TextPrimary,
                onClick = onBackgroundClick
            )

            // Randomize Outfit Button
            CircleCuteButton(
                icon = Icons.Default.AutoFixHigh,
                contentDescription = "Randomize",
                backgroundColor = PastelPurple,
                tint = TextPrimary,
                onClick = onRandomizeClick
            )

            // Reset Look Button
            CircleCuteButton(
                icon = Icons.Default.Refresh,
                contentDescription = "Reset",
                backgroundColor = SurfaceWhite,
                tint = TextPrimary,
                onClick = onResetClick
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Confirm Check Button (Top-right)
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(AccentPink)
                    .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                    .bounceClick(onClick = onConfirmClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun CircleCuteButton(
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .shadow(elevation = 4.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.5.dp, Color.White, CircleShape)
            .bounceClick(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}
