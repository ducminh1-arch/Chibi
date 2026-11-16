package com.chibidoll.dressup

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import com.chibidoll.dressup.domain.export.DollImageExporter
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@Implements(Canvas::class)
class ShadowTintTestCanvas {
    private var targetBitmap: Bitmap? = null

    @Implementation
    fun __constructor__(bitmap: Bitmap) {
        targetBitmap = bitmap
    }

    @Implementation
    fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        targetBitmap?.let { bmp ->
            val color = paint.color
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

            val filter = paint?.colorFilter
            val filterColor: Int? = if (filter is PorterDuffColorFilter) {
                paint.color
            } else null

            if (shadowSrc.baseColor != 0) {
                val finalBase = if (filterColor != null) applyMultiply(shadowSrc.baseColor, filterColor) else shadowSrc.baseColor
                shadowDst.eraseColor(finalBase)
            }
            for ((key, color) in shadowSrc.pixels) {
                val x = (key and 0xFFFFFFFFL).toInt() + left.toInt()
                val y = (key ushr 32).toInt() + top.toInt()
                if (color != 0) {
                    val finalColor = if (filterColor != null) applyMultiply(color, filterColor) else color
                    shadowDst.setPixel(x, y, finalColor)
                }
            }
        }
    }

    private fun applyMultiply(src: Int, filter: Int): Int {
        val sa = Color.alpha(src)
        val sr = Color.red(src)
        val sg = Color.green(src)
        val sb = Color.blue(src)

        val fa = Color.alpha(filter)
        val fr = Color.red(filter)
        val fg = Color.green(filter)
        val fb = Color.blue(filter)

        val outA = (sa * fa) / 255
        val outR = (sr * fr) / 255
        val outG = (sg * fg) / 255
        val outB = (sb * fb) / 255

        return Color.argb(outA, outR, outG, outB)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [ShadowTestBitmap::class, ShadowTintTestCanvas::class])
class TintCompositingPixelTest {

    @Test
    fun tintRendering_whiteFillBecomesSwatchColor_darkOutlineStaysDark_transparentShowsBackground() {
        val width = DollImageExporter.MASTER_WIDTH
        val height = DollImageExporter.MASTER_HEIGHT

        // 1. Synthetic Solid Sky Blue Background (1024x1536)
        val bgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.CYAN)
        }

        // 2. Synthetic Colorable Dress Layer:
        // - Pure white fill (0xFFFFFFFF) in region (200, 200, 600, 600)
        // - Dark outline (0xFF101010) around the perimeter
        // - Transparent everywhere else
        val dressBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val dressCanvas = Canvas(dressBitmap)

        // Fill region with Pure White
        dressCanvas.drawRect(
            Rect(200, 200, 600, 600),
            Paint().apply { color = Color.WHITE }
        )

        // Dark Outline stroke at (200, 200, 600, 210)
        dressCanvas.drawRect(
            Rect(200, 200, 600, 210),
            Paint().apply { color = Color.BLACK }
        )

        val bgItem = CatalogItem(
            id = "test_bg",
            category = "background",
            layer = "background",
            file = "synthetic/bg.png",
            thumbnail = ""
        )

        val dressItem = CatalogItem(
            id = "test_dress_colorable",
            category = "dress",
            layer = "dress",
            file = "synthetic/dress.png",
            thumbnail = "",
            colorable = true,
            palette = "general"
        )

        // Swatch Color: Bubblegum Pink 0xFFFF8DAA
        val swatchColor = 0xFFFF8DAAL

        val look = CharacterLook(
            equippedItems = mapOf(Layer.DRESS.name to dressItem),
            backgroundItem = bgItem,
            layerColors = mapOf(Layer.DRESS.name to swatchColor)
        )

        // Verify CharacterLook detailed render layers correctly extracts tint
        val detailedLayers = look.getDetailedRenderLayers()
        assertEquals(1, detailedLayers.size)
        assertTrue(detailedLayers[0].isColorable)
        assertEquals(swatchColor, detailedLayers[0].colorTint)

        // Export composited bitmap
        val exportedBitmap = DollImageExporter.renderMasterBitmapWithLoader(
            look = look,
            bitmapLoader = { path ->
                when (path) {
                    "synthetic/bg.png" -> bgBitmap
                    "synthetic/dress.png" -> dressBitmap
                    else -> null
                }
            },
            recycleLoadedBitmaps = false
        )

        // 1. Outside dress bounds (50, 50) -> background cyan shows through
        assertEquals(
            "Background should show where layer is transparent",
            Color.CYAN,
            exportedBitmap.getPixel(50, 50)
        )

        // 2. Inside white fill region (400, 400) -> multiply tint converts White (1.0) into exact swatch color
        val fillPixel = exportedBitmap.getPixel(400, 400)
        val expectedFill = swatchColor.toInt()
        assertEquals(
            "White fill must be multiplied to the exact swatch color",
            expectedFill,
            fillPixel
        )

        // 3. On black outline (400, 205) -> multiply tint on 0 remains 0 (black stays black)
        val outlinePixel = exportedBitmap.getPixel(400, 205)
        assertEquals(
            "Dark outline must remain dark after multiply tinting",
            Color.BLACK,
            outlinePixel
        )
    }

    @Test
    fun tintRendering_dualLayerItem_hairFrontAndHairBack_bothTintedIdentically() {
        val width = DollImageExporter.MASTER_WIDTH
        val height = DollImageExporter.MASTER_HEIGHT

        // Hair Back: White rect at (100, 100, 300, 300)
        val hairBackBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(hairBackBitmap).drawRect(
            Rect(100, 100, 300, 300),
            Paint().apply { color = Color.WHITE }
        )

        // Hair Front: White rect at (400, 400, 600, 600)
        val hairFrontBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(hairFrontBitmap).drawRect(
            Rect(400, 400, 600, 600),
            Paint().apply { color = Color.WHITE }
        )

        val dualLayerHair = CatalogItem(
            id = "hair_lilac_twin",
            category = "hair",
            layer = "hair_front",
            file = "synthetic/hair_front.png",
            thumbnail = "",
            secondaryLayer = "hair_back",
            secondaryFile = "synthetic/hair_back.png",
            colorable = true,
            palette = "hair"
        )

        // Swatch Color: Lilac Purple 0xFFC7B8EA
        val lilacSwatch = 0xFFC7B8EAL

        val initialLook = CharacterLook(
            equippedItems = mapOf(Layer.HAIR_FRONT.name to dualLayerHair),
            backgroundItem = null
        )

        // Dye hair with swatch color
        val tintedLook = initialLook.withLayerColor(Layer.HAIR_FRONT, lilacSwatch)

        val detailedLayers = tintedLook.getDetailedRenderLayers()
        assertEquals(2, detailedLayers.size)

        val backLayerInfo = detailedLayers.first { it.layer == Layer.HAIR_BACK }
        val frontLayerInfo = detailedLayers.first { it.layer == Layer.HAIR_FRONT }

        // Both layers in CharacterLook must have identical colorTint
        assertEquals("hair_back must receive swatch color", lilacSwatch, backLayerInfo.colorTint)
        assertEquals("hair_front must receive swatch color", lilacSwatch, frontLayerInfo.colorTint)
        assertTrue("hair_back must be colorable", backLayerInfo.isColorable)
        assertTrue("hair_front must be colorable", frontLayerInfo.isColorable)

        // Render both layers via exporter
        val exported = DollImageExporter.renderMasterBitmapWithLoader(
            look = tintedLook,
            bitmapLoader = { path ->
                when (path) {
                    "synthetic/hair_back.png" -> hairBackBitmap
                    "synthetic/hair_front.png" -> hairFrontBitmap
                    else -> null
                }
            },
            recycleLoadedBitmaps = false
        )

        val expectedPixel = lilacSwatch.toInt()
        val backPixel = exported.getPixel(200, 200)
        val frontPixel = exported.getPixel(500, 500)

        assertEquals("hair_back rendered pixel must equal swatch color", expectedPixel, backPixel)
        assertEquals("hair_front rendered pixel must equal swatch color", expectedPixel, frontPixel)
        assertEquals("Both hair layers must be tinted identically", backPixel, frontPixel)
    }

    @Test
    fun tintRendering_exportedBitmapEqualsOnScreenMultiplyFormula() {
        val width = DollImageExporter.MASTER_WIDTH
        val height = DollImageExporter.MASTER_HEIGHT

        // Light gray placeholder fill (RGB 200, 200, 200)
        val grayVal = 200
        val grayColor = Color.rgb(grayVal, grayVal, grayVal)

        val dressBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(dressBitmap).drawRect(
            Rect(150, 150, 450, 450),
            Paint().apply { color = grayColor }
        )

        val dressItem = CatalogItem(
            id = "test_dress_gray",
            category = "dress",
            layer = "dress",
            file = "synthetic/dress_gray.png",
            thumbnail = "",
            colorable = true,
            palette = "general"
        )

        // Swatch Color: Bubblegum Pink 0xFFFF8DAA
        val swatchColor = 0xFFFF8DAAL
        val swatchInt = swatchColor.toInt()

        // Formula for Compose BlendMode.Modulate: out = (src * filter) / 255
        val expectedR = (grayVal * Color.red(swatchInt)) / 255
        val expectedG = (grayVal * Color.green(swatchInt)) / 255
        val expectedB = (grayVal * Color.blue(swatchInt)) / 255
        val expectedPixel = Color.argb(255, expectedR, expectedG, expectedB)

        val look = CharacterLook(
            equippedItems = mapOf(Layer.DRESS.name to dressItem),
            layerColors = mapOf(Layer.DRESS.name to swatchColor)
        )

        val exported = DollImageExporter.renderMasterBitmapWithLoader(
            look = look,
            bitmapLoader = { path ->
                if (path == "synthetic/dress_gray.png") dressBitmap else null
            },
            recycleLoadedBitmaps = false
        )

        val actualPixel = exported.getPixel(300, 300)
        assertEquals(
            "Exported bitmap tint pixel must equal on-screen Compose BlendMode.Modulate math",
            expectedPixel,
            actualPixel
        )
    }
}

