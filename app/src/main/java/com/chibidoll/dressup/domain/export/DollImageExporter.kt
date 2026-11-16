package com.chibidoll.dressup.domain.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Shader
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.chibidoll.dressup.domain.model.CharacterLook
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object DollImageExporter {

    const val MASTER_WIDTH = 1024
    const val MASTER_HEIGHT = 1536
    const val THUMBNAIL_WIDTH = 341
    const val THUMBNAIL_HEIGHT = 512

    /**
     * Composites master 1024x1536 Bitmap using the provided bitmapLoader.
     * Composites background first, then equipped layers in strict z-order ascending.
     * Can be tested with synthetic in-memory bitmaps.
     */
    fun renderMasterBitmapWithLoader(
        look: CharacterLook,
        bitmapLoader: (filePath: String) -> Bitmap?,
        recycleLoadedBitmaps: Boolean = false
    ): Bitmap {
        val master = Bitmap.createBitmap(MASTER_WIDTH, MASTER_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(master)

        // 1. Render Background
        if (look.backgroundItem != null && look.backgroundItem.file.isNotBlank()) {
            var bgDrawn = false
            try {
                val bgBitmap = bitmapLoader(look.backgroundItem.file)
                if (bgBitmap != null) {
                    canvas.drawBitmap(bgBitmap, 0f, 0f, null)
                    if (recycleLoadedBitmaps) {
                        bgBitmap.recycle()
                    }
                    bgDrawn = true
                }
            } catch (_: Exception) {
                // Fallback to default pastel background
            }
            if (!bgDrawn) {
                drawDefaultBackground(canvas)
            }
        } else {
            drawDefaultBackground(canvas)
        }

        // 2. Render Layers in strict z-order with multiply tint support
        val detailedLayers = look.getDetailedRenderLayers()
        for (layerInfo in detailedLayers) {
            val filePath = layerInfo.filePath
            if (filePath.isBlank()) continue
            try {
                val layerBitmap = bitmapLoader(filePath)
                if (layerBitmap != null) {
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
                    canvas.drawBitmap(layerBitmap, 0f, 0f, paint)
                    if (recycleLoadedBitmaps) {
                        layerBitmap.recycle()
                    }
                }
            } catch (_: Exception) {
                // Gracefully continue rendering next layer if an asset is missing or corrupted
            }
        }

        return master
    }

    /**
     * Renders character + background into a crisp 1024x1536 Bitmap from asset files.
     * Composites background first, followed by equipped layers ordered by z-order.
     * Runs off the main thread.
     */
    suspend fun renderMasterBitmap(
        context: Context,
        look: CharacterLook
    ): Bitmap = withContext(Dispatchers.IO) {
        renderMasterBitmapWithLoader(
            look = look,
            bitmapLoader = { relativePath ->
                try {
                    context.assets.open(relativePath).use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                } catch (_: Exception) {
                    null
                }
            },
            recycleLoadedBitmaps = true
        )
    }

    private fun drawDefaultBackground(canvas: Canvas) {
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, MASTER_HEIGHT.toFloat(),
                android.graphics.Color.parseColor("#FFF2F6"),
                android.graphics.Color.parseColor("#EBF3FA"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, MASTER_WIDTH.toFloat(), MASTER_HEIGHT.toFloat(), bgPaint)
    }

    /**
     * Creates a high-quality 341x512 thumbnail from the master bitmap.
     */
    fun createThumbnail(masterBitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(masterBitmap, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, true)
    }

    /**
     * Saves master bitmap to Gallery under Pictures/[appName].
     * Uses MediaStore on API 29+ without requiring permissions.
     * Handles API 26-28 with direct file writing and MediaScanner.
     */
    suspend fun saveToGallery(
        context: Context,
        bitmap: Bitmap,
        appName: String = "ChibiDoll"
    ): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            val filename = "Chibi_${System.currentTimeMillis()}.png"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$appName")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val uri = resolver.insert(collection, values)
                    ?: throw IOException("Failed to create MediaStore entry")

                resolver.openOutputStream(uri)?.use { out ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        throw IOException("Failed to compress PNG into MediaStore output stream")
                    }
                }

                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                uri
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(picturesDir, appName).apply {
                    if (!exists() && !mkdirs()) {
                        throw IOException("Could not create directory: $absolutePath")
                    }
                }
                val file = File(appDir, filename)
                FileOutputStream(file).use { out ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        throw IOException("Failed to compress PNG to storage")
                    }
                }

                val deferredUri = CompletableDeferred<Uri>()
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    arrayOf("image/png")
                ) { _, uri ->
                    if (uri != null) {
                        deferredUri.complete(uri)
                    } else {
                        deferredUri.complete(Uri.fromFile(file))
                    }
                }
                deferredUri.await()
            }
        }
    }

    /**
     * Caches image in app cache and opens Android ACTION_SEND share sheet via FileProvider.
     */
    suspend fun shareLook(
        context: Context,
        bitmap: Bitmap,
        chooserTitle: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val cacheDir = File(context.cacheDir, "shared_looks").apply {
                if (!exists()) mkdirs()
            }
            val shareFile = File(cacheDir, "chibi_look_${System.currentTimeMillis()}.png")

            FileOutputStream(shareFile).use { out ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    throw IOException("Failed to compress PNG for share")
                }
            }

            val authority = "${context.packageName}.fileprovider"
            val contentUri = FileProvider.getUriForFile(context, authority, shareFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    }

    /**
     * Deletes cached shared PNGs older than the specified maxAgeMillis (default 24 hours).
     */
    fun cleanOldSharedLooks(
        context: Context,
        maxAgeMillis: Long = 24 * 60 * 60 * 1000L
    ) {
        try {
            val cacheDir = File(context.cacheDir, "shared_looks")
            if (cacheDir.exists() && cacheDir.isDirectory) {
                val threshold = System.currentTimeMillis() - maxAgeMillis
                cacheDir.listFiles()?.forEach { file ->
                    if (file.isFile && file.lastModified() < threshold) {
                        file.delete()
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore cleanup failures silently
        }
    }
}
