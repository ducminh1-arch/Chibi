package com.chibidoll.dressup.ui.dressup.components

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.Layer
import com.chibidoll.dressup.domain.model.RenderLayerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * High-performance on-screen layer compositor:
 * - Adaptive decode cap:
 *   - On devices that are not low-RAM (ActivityManager.isLowRamDevice() == false and memoryClass >= 256),
 *     composites cached base/overlay bitmaps at full 1024x1536 px.
 *   - On low-RAM devices, keeps 640x960 px to safeguard memory.
 * - Decodes each layer one at a time and recycles immediately after drawing onto the Canvas.
 * - Leaves dynamic eye layers (eyes_iris and eyes_line) separate so idle blinking (~120ms every ~3.8s)
 *   recomposes without touching or re-rendering the static layers.
 */
object DollCanvasCompositor {

    const val LOW_RAM_CANVAS_WIDTH = 640
    const val LOW_RAM_CANVAS_HEIGHT = 960
    const val HIGH_RES_CANVAS_WIDTH = 1024
    const val HIGH_RES_CANVAS_HEIGHT = 1536

    // Legacy fallback constants
    const val MAX_CANVAS_WIDTH = LOW_RAM_CANVAS_WIDTH
    const val MAX_CANVAS_HEIGHT = LOW_RAM_CANVAS_HEIGHT

    /**
     * Checks whether the current device is a high-end device with sufficient RAM:
     * ActivityManager.isLowRamDevice() == false and memoryClass >= 256.
     */
    fun isHighEndDevice(context: Context): Boolean {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            am != null && !am.isLowRamDevice && am.memoryClass >= 256
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Returns the maximum render dimensions for on-screen caching:
     * (1024, 1536) on high-end devices, (640, 960) on low-RAM devices.
     */
    fun getMaxCanvasDimensions(context: Context): Pair<Int, Int> {
        return if (isHighEndDevice(context)) {
            HIGH_RES_CANVAS_WIDTH to HIGH_RES_CANVAS_HEIGHT
        } else {
            LOW_RAM_CANVAS_WIDTH to LOW_RAM_CANVAS_HEIGHT
        }
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * Composites base layers (zIndex < 79, e.g. background, wings, body, clothes, blush, mouth)
     * into a single cached bitmap.
     */
    suspend fun compositeBaseLayers(
        context: Context,
        look: CharacterLook,
        targetWidth: Int = LOW_RAM_CANVAS_WIDTH,
        targetHeight: Int = LOW_RAM_CANVAS_HEIGHT
    ): Bitmap? = withContext(Dispatchers.IO) {
        val (maxWidth, maxHeight) = getMaxCanvasDimensions(context)
        val width = minOf(targetWidth, maxWidth).coerceAtLeast(1)
        val height = minOf(targetHeight, maxHeight).coerceAtLeast(1)

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val destRect = Rect(0, 0, width, height)

        // 1. Draw background
        if (look.backgroundItem != null && look.backgroundItem.file.isNotBlank()) {
            decodeAndDrawLayer(context, canvas, look.backgroundItem.file, destRect, null)
        }

        // 2. Draw static layers with zIndex < 79
        val allLayers = look.getDetailedRenderLayers()
        val baseLayers = allLayers.filter { it.zIndex < 79 }

        for (layerInfo in baseLayers) {
            val paint = if (layerInfo.isColorable && layerInfo.colorTint != null) {
                Paint().apply {
                    isAntiAlias = true
                    isFilterBitmap = true
                    color = layerInfo.colorTint.toInt()
                    colorFilter = PorterDuffColorFilter(
                        layerInfo.colorTint.toInt(),
                        PorterDuff.Mode.MULTIPLY
                    )
                }
            } else {
                null
            }
            decodeAndDrawLayer(context, canvas, layerInfo.filePath, destRect, paint)
        }

        output
    }

    /**
     * Composites overlay layers (zIndex > 82, e.g. eyebrows, glasses, ears, hair_front, hat, accessory)
     * into a single cached bitmap.
     */
    suspend fun compositeOverlayLayers(
        context: Context,
        look: CharacterLook,
        targetWidth: Int = LOW_RAM_CANVAS_WIDTH,
        targetHeight: Int = LOW_RAM_CANVAS_HEIGHT
    ): Bitmap? = withContext(Dispatchers.IO) {
        val allLayers = look.getDetailedRenderLayers()
        val overlayLayers = allLayers.filter { it.zIndex > 82 }

        if (overlayLayers.isEmpty()) {
            return@withContext null
        }

        val (maxWidth, maxHeight) = getMaxCanvasDimensions(context)
        val width = minOf(targetWidth, maxWidth).coerceAtLeast(1)
        val height = minOf(targetHeight, maxHeight).coerceAtLeast(1)

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val destRect = Rect(0, 0, width, height)

        for (layerInfo in overlayLayers) {
            val paint = if (layerInfo.isColorable && layerInfo.colorTint != null) {
                Paint().apply {
                    isAntiAlias = true
                    isFilterBitmap = true
                    color = layerInfo.colorTint.toInt()
                    colorFilter = PorterDuffColorFilter(
                        layerInfo.colorTint.toInt(),
                        PorterDuff.Mode.MULTIPLY
                    )
                }
            } else {
                null
            }
            decodeAndDrawLayer(context, canvas, layerInfo.filePath, destRect, paint)
        }

        output
    }

    private fun decodeAndDrawLayer(
        context: Context,
        canvas: Canvas,
        relativePath: String,
        destRect: Rect,
        paint: Paint?
    ) {
        if (relativePath.isBlank()) return
        try {
            // First decode bounds
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.assets.open(relativePath).use {
                BitmapFactory.decodeStream(it, null, options)
            }

            // Downsample on decode
            options.inJustDecodeBounds = false
            options.inSampleSize = calculateInSampleSize(options, destRect.width(), destRect.height())
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            val bmp = context.assets.open(relativePath).use {
                BitmapFactory.decodeStream(it, null, options)
            }

            if (bmp != null) {
                canvas.drawBitmap(bmp, null, destRect, paint)
                bmp.recycle() // Recycle immediately to maintain minimal peak memory
            }
        } catch (_: Exception) {
            // Missing or corrupted asset handled gracefully
        }
    }
}

