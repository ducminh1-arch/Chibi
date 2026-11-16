package com.chibidoll.dressup.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidoll.dressup.R
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.sound.SoundManager
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.BackgroundPastel
import com.chibidoll.dressup.ui.theme.BorderPastel
import com.chibidoll.dressup.ui.theme.PastelBlue
import com.chibidoll.dressup.ui.theme.PastelPeach
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.PastelPurple
import com.chibidoll.dressup.ui.theme.PastelPurpleDark
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextPrimary
import com.chibidoll.dressup.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private data class OnboardingPageData(
    val titleRes: Int,
    val descRes: Int,
    val icon: ImageVector,
    val secondaryIcon: ImageVector,
    val gradientColors: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPageData(
            titleRes = R.string.onboarding_page1_title,
            descRes = R.string.onboarding_page1_desc,
            icon = Icons.Default.Face,
            secondaryIcon = Icons.Default.AutoAwesome,
            gradientColors = listOf(Color(0xFFFFB3C6), Color(0xFFFF8DAA))
        ),
        OnboardingPageData(
            titleRes = R.string.onboarding_page2_title,
            descRes = R.string.onboarding_page2_desc,
            icon = Icons.Default.Checkroom,
            secondaryIcon = Icons.Default.Palette,
            gradientColors = listOf(Color(0xFFC7B8EA), Color(0xFF9D84D6))
        ),
        OnboardingPageData(
            titleRes = R.string.onboarding_page3_title,
            descRes = R.string.onboarding_page3_desc,
            icon = Icons.Default.Collections,
            secondaryIcon = Icons.Default.Share,
            gradientColors = listOf(Color(0xFFFFC6A5), Color(0xFFFF9E79))
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BackgroundPastel
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(
                        onClick = {
                            SoundManager.playTap()
                            onFinishOnboarding()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_skip),
                            color = TextSecondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                val page = pages[pageIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Kawaii Illustration Circle
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .shadow(16.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(page.gradientColors))
                            .border(4.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = page.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(96.dp)
                            )
                            // Small floating badge icon
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = page.secondaryIcon,
                                    contentDescription = null,
                                    tint = page.gradientColors.last(),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = stringResource(page.titleRes),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stringResource(page.descRes),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            // Bottom Navigation & Indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val widthAnim = animateDpAsState(
                            targetValue = if (isSelected) 26.dp else 10.dp,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "indicatorWidth"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(10.dp)
                                .width(widthAnim.value)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) AccentPink else BorderPastel
                                )
                        )
                    }
                }

                // Action Button: Next or Get Started
                val isLastPage = pagerState.currentPage == pages.size - 1
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp)
                        .shadow(6.dp, RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (isLastPage) {
                                    listOf(Color(0xFFFF758C), Color(0xFFFF7EB3))
                                } else {
                                    listOf(PastelPurpleDark, PastelPurple)
                                }
                            )
                        )
                        .bounceClick {
                            SoundManager.playTap()
                            if (isLastPage) {
                                onFinishOnboarding()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLastPage) {
                                stringResource(R.string.onboarding_get_started)
                            } else {
                                stringResource(R.string.onboarding_next)
                            },
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isLastPage) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

