package com.chibidoll.dressup.ui.dressup.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidoll.dressup.R
import com.chibidoll.dressup.domain.model.ColorPalette
import com.chibidoll.dressup.domain.model.ColorSwatch
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.sound.SoundManager
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.PastelPurpleDark
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextPrimary
import com.chibidoll.dressup.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun ColorPalettePanel(
    isVisible: Boolean,
    palette: ColorPalette?,
    currentColorLong: Long?,
    onColorSelected: (Long) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVietnamese = remember { Locale.getDefault().language == "vi" }

    AnimatedVisibility(
        visible = isVisible && palette != null,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        modifier = modifier
    ) {
        if (palette != null) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 240.dp)
                    .shadow(10.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(20.dp)),
                color = SurfaceWhite
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header: Palette icon, title, close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = AccentPink,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = palette.getDisplayName(isVietnamese),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = {
                                SoundManager.playTap()
                                onClose()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.btn_close),
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.palette_hint),
                        fontSize = 10.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 4x4 Grid of Color Swatches
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(palette.swatches, key = { it.id }) { swatch ->
                            ColorSwatchItem(
                                swatch = swatch,
                                isSelected = currentColorLong != null && currentColorLong == swatch.colorLong,
                                onSelect = {
                                    SoundManager.playTap()
                                    onColorSelected(swatch.colorLong)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSwatchItem(
    swatch: ColorSwatch,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val swatchColor = Color(swatch.colorLong)
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .size(38.dp)
            .shadow(if (isSelected) 4.dp else 1.dp, shape = shape)
            .clip(shape)
            .background(swatchColor)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) AccentPink else Color(0x33000000),
                shape = shape
            )
            .bounceClick { onSelect() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.Black,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

