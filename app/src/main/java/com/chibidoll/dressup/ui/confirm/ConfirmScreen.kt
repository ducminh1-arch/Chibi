package com.chibidoll.dressup.ui.confirm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.chibidoll.dressup.R
import com.chibidoll.dressup.data.repository.SavedLooksRepository
import com.chibidoll.dressup.domain.export.DollImageExporter
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.dressup.components.CharacterDollCanvas
import com.chibidoll.dressup.ui.dressup.components.CircleCuteButton
import com.chibidoll.dressup.ui.sound.SoundManager
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.BackgroundPastel
import com.chibidoll.dressup.ui.theme.BorderPastel
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.PastelPurple
import com.chibidoll.dressup.ui.theme.PastelYellow
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextPrimary
import com.chibidoll.dressup.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ConfirmScreen(
    currentLook: CharacterLook,
    editingLookId: Long?,
    savedLooksRepository: SavedLooksRepository,
    onBackToEdit: () -> Unit,
    onLookSaved: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isSaving by remember { mutableStateOf(false) }
    var isSharing by remember { mutableStateOf(false) }

    fun executeSave() {
        if (isSaving) return
        scope.launch {
            isSaving = true
            try {
                // 1. Render master 1024x1536 bitmap off-thread
                val master = DollImageExporter.renderMasterBitmap(context, currentLook)

                // 2. Save to MediaStore Gallery
                val galleryResult = DollImageExporter.saveToGallery(context, master)
                if (galleryResult.isFailure) {
                    throw galleryResult.exceptionOrNull() ?: Exception("Gallery save failed")
                }

                // 3. Create thumbnail and store in Room idempotently
                val thumbnail = DollImageExporter.createThumbnail(master)
                val roomResult = savedLooksRepository.saveLook(currentLook, thumbnail, editingLookId)

                roomResult.onSuccess { savedEntity ->
                    SoundManager.playSave()
                    onLookSaved(savedEntity.id)
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.save_gallery_success)
                    )
                }.onFailure {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.save_failed)
                    )
                }

                master.recycle()
                thumbnail.recycle()
            } catch (_: Exception) {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.save_failed)
                )
            } finally {
                isSaving = false
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            executeSave()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.permission_storage_rationale)
                )
            }
        }
    }

    fun handleSaveClick() {
        SoundManager.playTap()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) {
                executeSave()
            } else {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            executeSave()
        }
    }

    fun handleShareClick() {
        SoundManager.playTap()
        if (isSharing) return
        scope.launch {
            isSharing = true
            try {
                val master = DollImageExporter.renderMasterBitmap(context, currentLook)
                DollImageExporter.shareLook(
                    context = context,
                    bitmap = master,
                    chooserTitle = context.getString(R.string.share_look_title)
                )
                master.recycle()
            } catch (_: Exception) {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.share_failed)
                )
            } finally {
                isSharing = false
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundPastel,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CircleCuteButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back_to_edit),
                    backgroundColor = SurfaceWhite,
                    tint = TextPrimary,
                    onClick = onBackToEdit
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PastelYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.confirm_screen_title),
                        fontSize = 20.dp.value.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PastelYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Balance spacer with same size as CircleCuteButton
                Spacer(modifier = Modifier.size(44.dp))
            }

            // Doll Preview Frame
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurfaceWhite)
                    .border(3.dp, BorderPastel, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                CharacterDollCanvas(
                    look = currentLook,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )

                if (isSaving || isSharing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceWhite,
                            shadowElevation = 8.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = AccentPink,
                                    strokeWidth = 3.dp
                                )
                                Text(
                                    text = stringResource(R.string.saving_in_progress),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Action Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Share Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(PastelPurple)
                            .border(2.dp, Color.White, RoundedCornerShape(20.dp))
                            .bounceClick(enabled = !isSaving && !isSharing, onClick = { handleShareClick() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.btn_share),
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(R.string.btn_share),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Save to Gallery Button
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(listOf(AccentPink, PastelPink))
                            )
                            .border(2.dp, Color.White, RoundedCornerShape(20.dp))
                            .bounceClick(enabled = !isSaving && !isSharing, onClick = { handleSaveClick() }),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = stringResource(R.string.btn_save_gallery),
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = stringResource(R.string.btn_save_gallery),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Back to Edit Link / Button
                Row(
                    modifier = Modifier
                        .bounceClick(onClick = onBackToEdit)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.btn_back_to_edit),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

