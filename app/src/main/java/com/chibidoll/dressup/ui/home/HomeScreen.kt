package com.chibidoll.dressup.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidoll.dressup.R
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.dressup.components.CharacterDollCanvas
import com.chibidoll.dressup.ui.sound.SoundManager
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.BackgroundPastel
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.PastelPurple
import com.chibidoll.dressup.ui.theme.PastelPurpleDark
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextPrimary
import com.chibidoll.dressup.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    currentLook: CharacterLook,
    onPlayClick: () -> Unit,
    onCollectionClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Floating gentle animation for character preview
    val infiniteTransition = rememberInfiniteTransition(label = "homeFloat")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BackgroundPastel
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header / App Title + Settings Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentPink,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentPink,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.tagline),
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Settings icon button
                IconButton(
                    onClick = {
                        SoundManager.playTap()
                        onSettingsClick()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(42.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceWhite)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings_title),
                        tint = PastelPurpleDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Doll Preview Frame (Floating)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .offset(y = floatOffset.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.92f)
                        .aspectRatio(2f / 3f)
                        .shadow(12.dp, shape = RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(SurfaceWhite)
                        .border(3.dp, Color.White, RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CharacterDollCanvas(
                        look = currentLook,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Action Buttons: Play & My Collection
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Play Button (Primary Large Pill)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(60.dp)
                        .shadow(8.dp, shape = RoundedCornerShape(30.dp))
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFF758C),
                                    Color(0xFFFF7EB3)
                                )
                            )
                        )
                        .border(2.dp, Color.White, RoundedCornerShape(30.dp))
                        .bounceClick {
                            SoundManager.playTap()
                            onPlayClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_play),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Create Character (Coming Soon Placeholder)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .shadow(2.dp, shape = RoundedCornerShape(26.dp))
                        .clip(RoundedCornerShape(26.dp))
                        .background(SurfaceWhite.copy(alpha = 0.85f))
                        .border(1.5.dp, PastelPurple.copy(alpha = 0.6f), RoundedCornerShape(26.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PastelPurpleDark.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.btn_create_character),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }

                        // Cute Coming Soon Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PastelPink.copy(alpha = 0.85f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.coming_soon),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentPink
                            )
                        }
                    }
                }

                // My Collection Button (Secondary Cute Card)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .shadow(4.dp, shape = RoundedCornerShape(26.dp))
                        .clip(RoundedCornerShape(26.dp))
                        .background(SurfaceWhite)
                        .border(2.dp, PastelPurple, RoundedCornerShape(26.dp))
                        .bounceClick {
                            SoundManager.playTap()
                            onCollectionClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = PastelPurpleDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_my_collection),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

