package com.chibidoll.dressup.ui.dressup.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.Layer
import com.chibidoll.dressup.ui.common.SparkleBurst
import com.chibidoll.dressup.ui.theme.PastelPeach
import com.chibidoll.dressup.ui.theme.PastelPink
import com.chibidoll.dressup.ui.theme.PastelPurple
import kotlinx.coroutines.delay

private const val ASSET_PREFIX = "file:///android_asset/"

@Composable
fun CharacterDollCanvas(
    look: CharacterLook,
    modifier: Modifier = Modifier,
    enableBlink: Boolean = true,
    sparkleTrigger: Long = 0L
) {
    val context = LocalContext.current
    val roundedShape = RoundedCornerShape(24.dp)

    // Idle blink: swap eyes to alt_file (closed) for ~120ms every ~3.8s
    var isBlinking by remember { mutableStateOf(false) }

    LaunchedEffect(enableBlink) {
        if (!enableBlink) {
            isBlinking = false
            return@LaunchedEffect
        }
        while (true) {
            delay(3800L) // Wait ~3.8 seconds between blinks
            isBlinking = true // Close eyes
            delay(120L) // Keep closed for ~120ms
            isBlinking = false // Re-open eyes
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1024f / 1536f)
            .clip(roundedShape),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val targetWidthPx = with(density) { maxWidth.roundToPx() }.coerceAtLeast(1)
        val targetHeightPx = with(density) { maxHeight.roundToPx() }.coerceAtLeast(1)

        // Adaptive memory fix:
        // On non-low-RAM devices with memoryClass >= 256, render at full 1024x1536
        // On low-RAM devices, cap on-screen decode dimensions to 640x960 px
        val (maxAllowedWidth, maxAllowedHeight) = remember(context) {
            DollCanvasCompositor.getMaxCanvasDimensions(context)
        }
        val cappedWidthPx = minOf(targetWidthPx, maxAllowedWidth)
        val cappedHeightPx = minOf(targetHeightPx, maxAllowedHeight)

        // Memory fix: Composite static layers below and above eyes into cached Bitmaps
        // Rebuilt only when 'look' or screen dimensions change
        val baseBitmapState = produceState<Bitmap?>(initialValue = null, key1 = look, key2 = cappedWidthPx) {
            value = DollCanvasCompositor.compositeBaseLayers(context, look, cappedWidthPx, cappedHeightPx)
        }

        val overlayBitmapState = produceState<Bitmap?>(initialValue = null, key1 = look, key2 = cappedWidthPx) {
            value = DollCanvasCompositor.compositeOverlayLayers(context, look, cappedWidthPx, cappedHeightPx)
        }

        // 1. Base static layers (background + body + clothes + blush + mouth)
        val baseBmp = baseBitmapState.value
        if (baseBmp != null) {
            Image(
                bitmap = baseBmp.asImageBitmap(),
                contentDescription = "Base Composite",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            // Instant fallback while base bitmap composites in background
            if (look.backgroundItem != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(ASSET_PREFIX + look.backgroundItem.file)
                        .size(cappedWidthPx, cappedHeightPx)
                        .precision(Precision.INEXACT)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    PastelPurple.copy(alpha = 0.25f),
                                    PastelPink.copy(alpha = 0.20f),
                                    PastelPeach.copy(alpha = 0.25f)
                                )
                            )
                        )
                )
            }
            // Fallback base render
            val baseLayers = look.getDetailedRenderLayers().filter { it.zIndex < 79 }
            baseLayers.forEach { layerInfo ->
                val colorFilter = if (layerInfo.isColorable && layerInfo.colorTint != null) {
                    ColorFilter.tint(Color(layerInfo.colorTint), BlendMode.Modulate)
                } else null
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(ASSET_PREFIX + layerInfo.filePath)
                        .size(cappedWidthPx, cappedHeightPx)
                        .precision(Precision.INEXACT)
                        .crossfade(false)
                        .build(),
                    contentDescription = layerInfo.layer.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    colorFilter = colorFilter
                )
            }
        }

        // 2. Dynamic Eyes Layers (Iris at 79, Line at 82)
        // Kept separate so blink animation doesn't recompose or invalidate static layers
        val renderLayers = look.getDetailedRenderLayers()
        val eyeLayers = renderLayers.filter { it.zIndex in 79..82 }
        eyeLayers.forEach { layerInfo ->
            val layer = layerInfo.layer
            val isEyeLayer = layer == Layer.EYES || layer == Layer.EYES_LINE || layer == Layer.EYES_IRIS
            val imagePath = if (isEyeLayer && enableBlink && isBlinking && !layerInfo.altFilePath.isNullOrBlank()) {
                layerInfo.altFilePath
            } else {
                layerInfo.filePath
            }

            val colorFilter = if (layerInfo.isColorable && layerInfo.colorTint != null) {
                ColorFilter.tint(Color(layerInfo.colorTint), BlendMode.Modulate)
            } else {
                null
            }

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(ASSET_PREFIX + imagePath)
                    .size(cappedWidthPx, cappedHeightPx)
                    .precision(Precision.INEXACT)
                    .crossfade(false)
                    .build(),
                contentDescription = layer.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                colorFilter = colorFilter
            )
        }

        // 3. Overlay static layers (eyebrows, glasses, ears, hair_front, hat, accessory)
        val overlayBmp = overlayBitmapState.value
        if (overlayBmp != null) {
            Image(
                bitmap = overlayBmp.asImageBitmap(),
                contentDescription = "Overlay Composite",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else if (baseBmp == null) {
            // Fallback overlay render while compositing
            val overlayLayers = renderLayers.filter { it.zIndex > 82 }
            overlayLayers.forEach { layerInfo ->
                val colorFilter = if (layerInfo.isColorable && layerInfo.colorTint != null) {
                    ColorFilter.tint(Color(layerInfo.colorTint), BlendMode.Modulate)
                } else null
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(ASSET_PREFIX + layerInfo.filePath)
                        .size(cappedWidthPx, cappedHeightPx)
                        .precision(Precision.INEXACT)
                        .crossfade(false)
                        .build(),
                    contentDescription = layerInfo.layer.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    colorFilter = colorFilter
                )
            }
        }

        // 4. Sparkle Particle Burst on equip
        SparkleBurst(
            trigger = sparkleTrigger,
            modifier = Modifier.fillMaxSize()
        )
    }
}
