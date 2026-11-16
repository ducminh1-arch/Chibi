package com.chibidoll.dressup.ui.collection

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.SubcomposeAsyncImage
import com.chibidoll.dressup.R
import com.chibidoll.dressup.data.local.SavedLookEntity
import com.chibidoll.dressup.data.local.SavedLookMapper
import com.chibidoll.dressup.data.repository.SavedLooksRepository
import com.chibidoll.dressup.domain.export.DollImageExporter
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.ui.common.bounceClick
import com.chibidoll.dressup.ui.dressup.components.CircleCuteButton
import com.chibidoll.dressup.ui.sound.SoundManager
import com.chibidoll.dressup.ui.theme.AccentPink
import com.chibidoll.dressup.ui.theme.BackgroundPastel
import com.chibidoll.dressup.ui.theme.BorderPastel
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.PastelPurple
import com.chibidoll.dressup.ui.theme.SurfaceWhite
import com.chibidoll.dressup.ui.theme.TextPrimary
import com.chibidoll.dressup.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyCollectionScreen(
    savedLooksRepository: SavedLooksRepository,
    catalog: List<CatalogItem>,
    onBackClick: () -> Unit,
    onCreateLookClick: () -> Unit,
    onEditLook: (CharacterLook, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val savedLooks by savedLooksRepository.getAllLooks().collectAsState(initial = emptyList())
    var selectedLookForDetail by remember { mutableStateOf<SavedLookEntity?>(null) }
    var lookToDelete by remember { mutableStateOf<SavedLookEntity?>(null) }

    var isSharingLook by remember { mutableStateOf(false) }

    fun shareSavedLook(entity: SavedLookEntity) {
        if (isSharingLook) return
        scope.launch {
            isSharingLook = true
            try {
                // Reconstruct full CharacterLook from saved look entity and catalog
                val look = SavedLookMapper.toCharacterLook(entity, catalog)

                // Composite full 1024x1536 master image off the main thread
                val master = DollImageExporter.renderMasterBitmap(context, look)

                // Share full 1024x1536 PNG via FileProvider and Android share sheet
                val shareResult = DollImageExporter.shareLook(
                    context = context,
                    bitmap = master,
                    chooserTitle = context.getString(R.string.share_look_title)
                )
                master.recycle()

                if (shareResult.isFailure) {
                    snackbarHostState.showSnackbar(context.getString(R.string.share_failed))
                }
            } catch (_: Exception) {
                snackbarHostState.showSnackbar(context.getString(R.string.share_failed))
            } finally {
                isSharingLook = false
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
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleCuteButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    backgroundColor = SurfaceWhite,
                    tint = TextPrimary,
                    onClick = {
                        SoundManager.playTap()
                        onBackClick()
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.btn_my_collection),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.weight(1f))

                if (savedLooks.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PastelPink.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${savedLooks.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentPink
                        )
                    }
                }
            }

            if (savedLooks.isEmpty()) {
                // Empty Closet Placeholder
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .shadow(8.dp, shape = CircleShape)
                                .clip(CircleShape)
                                .background(PastelPurple.copy(alpha = 0.35f))
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DryCleaning,
                                contentDescription = null,
                                tint = AccentPink,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = stringResource(R.string.collection_empty_title),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.empty_collection),
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // "Dress Up Now" Button
                        Box(
                            modifier = Modifier
                                .height(52.dp)
                                .padding(horizontal = 16.dp)
                                .shadow(6.dp, shape = RoundedCornerShape(26.dp))
                                .clip(RoundedCornerShape(26.dp))
                                .background(PastelPink)
                                .border(2.dp, Color.White, RoundedCornerShape(26.dp))
                                .bounceClick {
                                    SoundManager.playTap()
                                    onCreateLookClick()
                                }
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AccentPink,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.btn_create_look),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            } else {
                // 2-Column Grid of Saved Looks
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(savedLooks, key = { it.id }) { lookItem ->
                        SavedLookCard(
                            entity = lookItem,
                            onClick = {
                                SoundManager.playTap()
                                selectedLookForDetail = lookItem
                            }
                        )
                    }
                }
            }
        }
    }

    // Detail Dialog on tap
    selectedLookForDetail?.let { detailLook ->
        Dialog(onDismissRequest = { selectedLookForDetail = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(28.dp),
                color = SurfaceWhite,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Dialog Header with Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.look_detail_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        CircleCuteButton(
                            icon = Icons.Default.Close,
                            contentDescription = stringResource(R.string.btn_close),
                            backgroundColor = BackgroundPastel,
                            tint = TextSecondary,
                            onClick = { selectedLookForDetail = null },
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Large Thumbnail Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(BackgroundPastel)
                            .border(2.dp, BorderPastel, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = File(detailLook.thumbnailPath),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = AccentPink, modifier = Modifier.size(32.dp))
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date label
                    val dateFormatted = remember(detailLook.createdAt) {
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(detailLook.createdAt))
                    }
                    Text(
                        text = dateFormatted,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons: Edit, Share, Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Edit button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(PastelPink)
                                .bounceClick {
                                    SoundManager.playTap()
                                    val restoredLook = SavedLookMapper.toCharacterLook(detailLook, catalog)
                                    selectedLookForDetail = null
                                    onEditLook(restoredLook, detailLook.id)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = AccentPink, modifier = Modifier.size(18.dp))
                                Text(
                                    text = stringResource(R.string.btn_edit),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        // Share button (renders and shares 1024x1536 PNG)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(PastelPurple)
                                .bounceClick(enabled = !isSharingLook) {
                                    SoundManager.playTap()
                                    shareSavedLook(detailLook)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSharingLook) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = stringResource(R.string.btn_share),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Delete button
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFEBEE))
                                .bounceClick {
                                    SoundManager.playTap()
                                    lookToDelete = detailLook
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    lookToDelete?.let { look ->
        AlertDialog(
            onDismissRequest = { lookToDelete = null },
            title = {
                Text(
                    text = stringResource(R.string.confirm_delete_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = stringResource(R.string.confirm_delete_msg))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            savedLooksRepository.deleteLook(look)
                            lookToDelete = null
                            selectedLookForDetail = null
                            snackbarHostState.showSnackbar(context.getString(R.string.delete_success))
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.btn_delete),
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { lookToDelete = null }) {
                    Text(text = stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
fun SavedLookCard(
    entity: SavedLookEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateString = remember(entity.createdAt) {
        SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(entity.createdAt))
    }

    Box(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .border(2.dp, BorderPastel, RoundedCornerShape(20.dp))
            .bounceClick(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(BackgroundPastel)
            ) {
                SubcomposeAsyncImage(
                    model = File(entity.thumbnailPath),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AccentPink, modifier = Modifier.size(24.dp))
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateString,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AccentPink.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
