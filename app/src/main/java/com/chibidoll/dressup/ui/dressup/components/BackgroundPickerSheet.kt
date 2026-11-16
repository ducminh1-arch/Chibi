package com.chibidoll.dressup.ui.dressup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chibidoll.dressup.R
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.sound.SoundManager
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.PastelPurple
import com.chibidoll.dressup.ui.theme.PastelPurpleDark
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextPrimary
import com.chibidoll.dressup.ui.theme.TextSecondary

private const val ASSET_PREFIX = "file:///android_asset/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundPickerSheet(
    isVisible: Boolean,
    backgrounds: List<CatalogItem>,
    currentLook: CharacterLook,
    onBackgroundSelected: (CatalogItem?) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.background_picker_title),
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PastelPurple.copy(alpha = 0.3f))
                        .bounceClick {
                            SoundManager.playTap()
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.btn_close),
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grid of Backgrounds (Including "No Background")
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                // Option 1: "No Background"
                item {
                    val isNoneSelected = currentLook.backgroundItem == null
                    val cardShape = RoundedCornerShape(18.dp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3f)
                                .shadow(if (isNoneSelected) 6.dp else 2.dp, shape = cardShape)
                                .clip(cardShape)
                                .background(if (isNoneSelected) PastelPink.copy(alpha = 0.3f) else Color(0xFFF3EDF7))
                                .border(
                                    width = if (isNoneSelected) 2.5.dp else 1.dp,
                                    color = if (isNoneSelected) AccentPink else Color(0xFFE2D6EE),
                                    shape = cardShape
                                )
                                .bounceClick {
                                    SoundManager.playTap()
                                    onBackgroundSelected(null)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = stringResource(R.string.no_background),
                                    tint = if (isNoneSelected) AccentPink else TextSecondary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            if (isNoneSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(AccentPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.no_background),
                            fontSize = 12.sp,
                            fontWeight = if (isNoneSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isNoneSelected) AccentPink else TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // 8 Background Options from Catalog
                items(backgrounds, key = { it.id }) { bg ->
                    val isSelected = currentLook.backgroundItem?.id == bg.id
                    val cardShape = RoundedCornerShape(18.dp)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3f)
                                .shadow(if (isSelected) 6.dp else 2.dp, shape = cardShape)
                                .clip(cardShape)
                                .background(SurfaceWhite)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) AccentPink else Color(0xFFE2D6EE),
                                    shape = cardShape
                                )
                                .bounceClick {
                                    SoundManager.playTap()
                                    onBackgroundSelected(bg)
                                }
                        ) {
                            // Thumbnail
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(ASSET_PREFIX + bg.thumbnail)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = bg.nameEn,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Selected indicator
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(AccentPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = bg.getDisplayName(false),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) AccentPink else TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

