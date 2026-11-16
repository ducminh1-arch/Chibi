package com.chibidoll.dressup

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.chibidoll.dressup.domain.export.DollImageExporter
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@Implements(Bitmap::class)
class ShadowTestBitmap {
    val pixels = mutableMapOf<Long, Int>()
    var baseColor: Int = 0

    @get:Implementation
    var width: Int = 0

    @get:Implementation
    var height: Int = 0

    @get:Implementation
    var config: Bitmap.Config = Bitmap.Config.ARGB_8888

    @Implementation
    fun setPixel(x: Int, y: Int, color: Int) {
        pixels[(y.toLong() shl 32) or (x.toLong() and 0xFFFFFFFFL)] = color
    }

    @Implementation
    fun getPixel(x: Int, y: Int): Int {
        return pixels[(y.toLong() shl 32) or (x.toLong() and 0xFFFFFFFFL)] ?: baseColor
    }

    @Implementation
    fun eraseColor(c: Int) {
        baseColor = c
        pixels.clear()
    }

    companion object {
        @Implementation
        @JvmStatic
        fun createBitmap(w: Int, h: Int, conf: Bitmap.Config): Bitmap {
            val bitmap = Shadow.newInstanceOf(Bitmap::class.java)
            val shadow = Shadow.extract<ShadowTestBitmap>(bitmap)
            shadow.width = w
            shadow.height = h
            shadow.config = conf
            return bitmap
        }
    }
}

@Implements(Canvas::class)
class ShadowTestCanvas {
    private var targetBitmap: Bitmap? = null

    @Implementation
    fun __constructor__(bitmap: Bitmap) {
        targetBitmap = bitmap
    }

    @Implementation
    fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        targetBitmap?.let { bmp ->
            val color = if (paint.shader != null) {
                Color.parseColor("#FFF2F6")
            } else {
                paint.color
            }
            val shadowDst = Shadow.extract<ShadowTestBitmap>(bmp)
            if (left <= 0f && top <= 0f && right >= DollImageExporter.MASTER_WIDTH && bottom >= DollImageExporter.MASTER_HEIGHT) {
                shadowDst.eraseColor(color)
            } else {
                for (y in top.toInt() until bottom.toInt()) {
                    for (x in left.toInt() until right.toInt()) {
                        bmp.setPixel(x, y, color)
                    }
                }
            }
        }
    }

    @Implementation
    fun drawRect(rect: Rect, paint: Paint) {
        drawRect(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), paint)
    }

    @Implementation
    fun drawBitmap(bitmap: Bitmap, left: Float, top: Float, paint: Paint?) {
        targetBitmap?.let { dst ->
            val shadowSrc = Shadow.extract<ShadowTestBitmap>(bitmap)
            val shadowDst = Shadow.extract<ShadowTestBitmap>(dst)

            if (shadowSrc.baseColor != 0) {
                shadowDst.eraseColor(shadowSrc.baseColor)
            }
            for ((key, color) in shadowSrc.pixels) {
                val x = (key and 0xFFFFFFFFL).toInt() + left.toInt()
                val y = (key ushr 32).toInt() + top.toInt()
                if (color != 0) {
                    shadowDst.setPixel(x, y, color)
                }
            }
        }
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowTestBitmap::class, ShadowTestCanvas::class])
class CompositingPixelTest {

    @Test
    fun compositing_assertPixelColorsAtCoordinates_higherZOrderCoversLowerAndBackgroundShowsThrough() {
        val width = DollImageExporter.MASTER_WIDTH
        val height = DollImageExporter.MASTER_HEIGHT

        // 1. Synthetic Solid Green Background (1024x1536)
        val bgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.GREEN)
        }

        // 2. Synthetic Body Layer: Transparent canvas with Solid Blue rect at (100, 100, 500, 500)
        val bodyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(bodyBitmap).drawRect(
            Rect(100, 100, 500, 500),
            Paint().apply { color = Color.BLUE }
        )

        // 3. Synthetic Top Layer: Transparent canvas with Solid Red rect at (300, 300, 700, 700)
        val topBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(topBitmap).drawRect(
            Rect(300, 300, 700, 700),
            Paint().apply { color = Color.RED }
        )

        val bgItem = CatalogItem(
            id = "test_bg",
            category = "background",
            layer = "background",
            file = "synthetic/bg.png",
            thumbnail = ""
        )
        val bodyItem = CatalogItem(
            id = "test_body",
            category = "body",
            layer = "body",
            file = "synthetic/body.png",
            thumbnail = ""
        )
        val topItem = CatalogItem(
            id = "test_top",
            category = "top",
            layer = "top",
            file = "synthetic/top.png",
            thumbnail = ""
        )

        // Equips body (zIndex 20) and top (zIndex 50) over background
        val look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.TOP.name to topItem
            ),
            backgroundItem = bgItem
        )

        val exportedBitmap = DollImageExporter.renderMasterBitmapWithLoader(
            look = look,
            bitmapLoader = { path ->
                when (path) {
                    "synthetic/bg.png" -> bgBitmap
                    "synthetic/body.png" -> bodyBitmap
                    "synthetic/top.png" -> topBitmap
                    else -> null
                }
            },
            recycleLoadedBitmaps = false
        )

        // Point (50, 50): Outside any clothes -> must be Green background
        assertEquals(
            "Background should be visible where layers are transparent",
            Color.GREEN,
            exportedBitmap.getPixel(50, 50)
        )

        // Point (200, 200): Inside body, outside top -> must be Blue body
        assertEquals(
            "Lower z-order layer should be visible where not covered",
            Color.BLUE,
            exportedBitmap.getPixel(200, 200)
        )

        // Point (400, 400): In overlap region (both body and top) -> top (zIndex 50) must cover body (zIndex 20)
        assertEquals(
            "Higher z-order layer (Top, zIndex 50) must cover lower layer (Body, zIndex 20)",
            Color.RED,
            exportedBitmap.getPixel(400, 400)
        )

        // Point (600, 600): Inside top, outside body -> must be Red top
        assertEquals(
            "Top layer should be visible in its bounds",
            Color.RED,
            exportedBitmap.getPixel(600, 600)
        )

        // Point (800, 800): Outside any clothes -> must be Green background
        assertEquals(
            "Background should be visible outside layer bounds",
            Color.GREEN,
            exportedBitmap.getPixel(800, 800)
        )
    }

    @Test
    fun compositing_withNoBackground_paintsDefaultPastelCanvas() {
        val width = DollImageExporter.MASTER_WIDTH
        val height = DollImageExporter.MASTER_HEIGHT

        val bodyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(bodyBitmap).drawRect(
            Rect(100, 100, 500, 500),
            Paint().apply { color = Color.MAGENTA }
        )

        val bodyItem = CatalogItem(
            id = "test_body",
            category = "body",
            layer = "body",
            file = "synthetic/body.png",
            thumbnail = ""
        )

        val lookWithoutBg = CharacterLook(
            equippedItems = mapOf(Layer.BODY.name to bodyItem),
            backgroundItem = null
        )

        val exportedBitmap = DollImageExporter.renderMasterBitmapWithLoader(
            look = lookWithoutBg,
            bitmapLoader = { path ->
                if (path == "synthetic/body.png") bodyBitmap else null
            },
            recycleLoadedBitmaps = false
        )

        // Inside body rect -> Magenta
        assertEquals(Color.MAGENTA, exportedBitmap.getPixel(200, 200))

        // Outside body rect -> Non-transparent default pastel background
        val bgPixel = exportedBitmap.getPixel(50, 50)
        assertNotEquals(0, Color.alpha(bgPixel))
    }
}
