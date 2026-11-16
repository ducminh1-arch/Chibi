package com.chibidoll.dressup

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], shadows = [ShadowTestBitmap::class, ShadowTintTestCanvas::class])
class SocksUnderShoesPixelTest {

    private val testPaint = Paint().apply { isAntiAlias = false }

    @Test
    fun export_rendersSocksUnderShoes_shoesCoverOverlappingFoot_andSocksVisibleAboveShoes() {
        val width = DollImageExporter.MASTER_WIDTH
        val height = DollImageExporter.MASTER_HEIGHT

        val socksFilePath = "items/socks/socks_test.png"
        val shoesFilePath = "items/shoes/shoes_test.png"

        // Socks: covers leg from y=1050 to y=1350 in solid BLUE
        val socksBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val socksCanvas = Canvas(socksBitmap)
        testPaint.color = Color.BLUE
        socksCanvas.drawRect(400f, 1050f, 600f, 1350f, testPaint)

        // Shoes: covers foot from y=1250 to y=1420 in solid RED
        val shoesBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val shoesCanvas = Canvas(shoesBitmap)
        testPaint.color = Color.RED
        shoesCanvas.drawRect(400f, 1250f, 600f, 1420f, testPaint)

        val socksItem = CatalogItem(
            id = "socks_test",
            category = "socks",
            layer = "socks",
            file = socksFilePath,
            thumbnail = "thumb_socks.png"
        )

        val shoesItem = CatalogItem(
            id = "shoes_test",
            category = "shoes",
            layer = "shoes",
            file = shoesFilePath,
            thumbnail = "thumb_shoes.png"
        )

        val look = CharacterLook(
            equippedItems = mapOf(
                Layer.SOCKS.name to socksItem,
                Layer.SHOES.name to shoesItem
            )
        )

        // Verify z-index: SOCKS (25) < SHOES (30)
        assertTrue(
            "Socks zIndex must be strictly less than Shoes zIndex",
            Layer.SOCKS.zIndex < Layer.SHOES.zIndex
        )

        val masterBitmap = DollImageExporter.renderMasterBitmapWithLoader(
            look = look,
            bitmapLoader = { path ->
                when (path) {
                    socksFilePath -> socksBitmap
                    shoesFilePath -> shoesBitmap
                    else -> null
                }
            }
        )

        // 1. In overlapping region (foot, y=1300):
        // Shoes (zIndex 30) renders over Socks (zIndex 25) -> must be RED
        val overlapPixel = masterBitmap.getPixel(500, 1300)
        assertEquals(
            "Overlapping foot region must be RED (shoes over socks)",
            Color.RED,
            overlapPixel
        )

        // 2. In non-overlapping calf/knee region (y=1150):
        // Socks (zIndex 25) is visible above shoes -> must be BLUE
        val sockOnlyPixel = masterBitmap.getPixel(500, 1150)
        assertEquals(
            "Calf/knee region above shoe top must be BLUE (socks visible)",
            Color.BLUE,
            sockOnlyPixel
        )
    }
}

