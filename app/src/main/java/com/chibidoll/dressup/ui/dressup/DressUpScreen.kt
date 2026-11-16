package com.chibidoll.dressup.ui.dressup

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chibidoll.dressup.R
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.dressup.components.BackgroundPickerSheet
import com.chibidoll.dressup.ui.dressup.components.CharacterDollCanvas
import com.chibidoll.dressup.ui.dressup.components.ColorPalettePanel
import com.chibidoll.dressup.ui.dressup.components.ItemGridView
import com.chibidoll.dressup.ui.dressup.components.TwoLevelCategoryBar
import com.chibidoll.dressup.ui.sound.SoundManager
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.BackgroundPastel
import com.chibidoll.dressup.ui.theme.PastelBlue
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.PastelPurple
import com.chibidoll.dressup.ui.theme.PastelPurpleDark
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextPrimary
import com.chibidoll.dressup.ui.theme.TextSecondary

@Composable
fun DressUpScreen(
    viewModel: DressUpViewModel,
    onNavigateBack: () -> Unit,
    onConfirmLook: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var sparkleTrigger by remember { mutableStateOf(0L) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BackgroundPastel
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PastelPurpleDark)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main Doll Arena with Left and Right Floating Side Columns
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Center Doll Canvas
                    CharacterDollCanvas(
                        look = uiState.currentLook,
                        enableBlink = true,
                        sparkleTrigger = sparkleTrigger,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 46.dp)
                    )

                    // LEFT Side Column (Back, Reset, Randomize)
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Back button
                        SideRoundButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_back),
                            backgroundColor = SurfaceWhite,
                            iconTint = TextPrimary,
                            onClick = {
                                SoundManager.playTap()
                                onNavigateBack()
                            }
                        )

                        // 2. Reset outfit button (with confirmation dialog)
                        SideRoundButton(
                            icon = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.btn_reset),
                            backgroundColor = SurfaceWhite,
                            iconTint = Color(0xFFFF5252),
                            onClick = {
                                SoundManager.playTap()
                                viewModel.showResetConfirmation()
                            }
                        )

                        // 3. Randomize outfit button
                        SideRoundButton(
                            icon = Icons.Default.AutoFixHigh,
                            contentDescription = stringResource(R.string.btn_random),
                            backgroundColor = PastelPurple,
                            iconTint = Color.White,
                            onClick = {
                                sparkleTrigger = System.currentTimeMillis()
                                SoundManager.playSparkle()
                                viewModel.onRandomizeClicked()
                            }
                        )
                    }

                    // RIGHT Side Column (Confirm, Palette Toggle, Background Picker)
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Confirm Check button (Large prominent)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(elevation = 6.dp, shape = CircleShape)
                                .clip(CircleShape)
                                .background(AccentPink)
                                .border(2.dp, Color.White, CircleShape)
                                .bounceClick {
                                    SoundManager.playSave()
                                    onConfirmLook()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.btn_confirm),
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // 2. Color Palette Toggle button
                        SideRoundButton(
                            icon = Icons.Default.Palette,
                            contentDescription = stringResource(R.string.palette_title),
                            backgroundColor = if (uiState.isPaletteOpen) AccentPink else SurfaceWhite,
                            iconTint = if (uiState.isPaletteOpen) Color.White else TextPrimary,
                            onClick = {
                                SoundManager.playTap()
                                viewModel.togglePalette()
                            }
                        )

                        // 3. Background Picker button
                        SideRoundButton(
                            icon = Icons.Default.Image,
                            contentDescription = stringResource(R.string.btn_background),
                            backgroundColor = PastelBlue,
                            iconTint = TextPrimary,
                            onClick = {
                                SoundManager.playTap()
                                viewModel.openBackgroundPicker()
                            }
                        )
                    }

                    // Floating Color Palette Panel (Right-aligned overlay)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 56.dp, end = 56.dp)
                    ) {
                        val activeLayer = uiState.activeColorableLayer
                        val currentColor = if (activeLayer != null) {
                            uiState.currentLook.getColorForLayer(activeLayer)
                        } else null

                        ColorPalettePanel(
                            isVisible = uiState.isPaletteOpen,
                            palette = uiState.activePalette,
                            currentColorLong = currentColor,
                            onColorSelected = { colorLong ->
                                viewModel.onColorSwatchSelected(colorLong)
                            },
                            onClose = {
                                viewModel.closePalette()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Bottom Dock: Two-Level Category Tabs + Multi-Row 5-Column Grid
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 14.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 12.dp)
                    ) {
                        // Two-Level Category Bar (Groups on top, Subcategories below)
                        TwoLevelCategoryBar(
                            groups = uiState.categoryGroups,
                            selectedGroup = uiState.selectedGroup,
                            selectedSubcategory = uiState.selectedSubcategory,
                            onGroupSelected = { group ->
                                SoundManager.playTap()
                                viewModel.onCategoryGroupSelected(group)
                            },
                            onSubcategorySelected = { subcat ->
                                SoundManager.playTap()
                                viewModel.onSubcategorySelected(subcat)
                            }
                        )

                        // Multi-row 5-Column Item Grid
                        ItemGridView(
                            items = uiState.itemsInSelectedSubcategory,
                            currentLook = uiState.currentLook,
                            onItemClick = { item ->
                                sparkleTrigger = System.currentTimeMillis()
                                SoundManager.playEquip()
                                viewModel.onItemClicked(item)
                            }
                        )
                    }
                }
            }

            // Modal Background Picker Sheet
            BackgroundPickerSheet(
                isVisible = uiState.isBackgroundPickerVisible,
                backgrounds = uiState.allBackgrounds,
                currentLook = uiState.currentLook,
                onBackgroundSelected = { bg ->
                    SoundManager.playTap()
                    viewModel.onBackgroundSelected(bg)
                },
                onDismiss = {
                    SoundManager.playTap()
                    viewModel.closeBackgroundPicker()
                }
            )

            // Reset Confirmation Dialog
            if (uiState.isResetDialogVisible) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissResetConfirmation() },
                    title = {
                        Text(
                            text = stringResource(R.string.reset_confirm_title),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.reset_confirm_message),
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                SoundManager.playTap()
                                viewModel.confirmResetOutfit()
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF5252))
                        ) {
                            Text(
                                text = stringResource(R.string.btn_reset),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                SoundManager.playTap()
                                viewModel.dismissResetConfirmation()
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.btn_cancel),
                                color = TextSecondary
                            )
                        }
                    },
                    containerColor = SurfaceWhite,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SideRoundButton(
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color,
    iconTint: Color,
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
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}
