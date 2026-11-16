package com.chibidoll.dressup

import android.graphics.Bitmap
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
@Config(sdk = [33])
class ZOverrideTest {

    @Test
    fun itemWithZOverride_overridesDefaultLayerZIndex() {
        val normalTail = CatalogItem(
            id = "tail_cat",
            category = "tail",
            layer = "tail",
            file = "items/tail/tail_cat.png",
            thumbnail = "items/thumbnails/thumb_tail_cat.png",
            zOverride = null
        )

        val frontTail = CatalogItem(
            id = "tail_fox_front",
            category = "tail",
            layer = "tail",
            file = "items/tail/tail_fox_front.png",
            thumbnail = "items/thumbnails/thumb_tail_fox_front.png",
            zOverride = 45
        )

        val skirt = CatalogItem(
            id = "bottom_skirt",
            category = "bottom",
            layer = "bottom",
            file = "items/bottom/bottom_skirt.png",
            thumbnail = "items/thumbnails/thumb_skirt.png"
        )

        val top = CatalogItem(
            id = "top_shirt",
            category = "top",
            layer = "top",
            file = "items/top/top_shirt.png",
            thumbnail = "items/thumbnails/thumb_top.png"
        )

        // Case 1: Normal tail (zIndex 15) sits behind skirt (zIndex 40)
        val lookWithNormalTail = CharacterLook(
            equippedItems = mapOf(
                Layer.TAIL.name to normalTail,
                Layer.BOTTOM.name to skirt,
                Layer.TOP.name to top
            )
        )
        val normalOrder = lookWithNormalTail.getDetailedRenderLayers().map { it.catalogItem?.id }
        assertEquals(listOf("tail_cat", "bottom_skirt", "top_shirt"), normalOrder)

        // Case 2: Front tail with z_override = 45 sits IN FRONT OF skirt (40) and BEHIND top (50)
        val lookWithFrontTail = CharacterLook(
            equippedItems = mapOf(
                Layer.TAIL.name to frontTail,
                Layer.BOTTOM.name to skirt,
                Layer.TOP.name to top
            )
        )
        val frontOrder = lookWithFrontTail.getDetailedRenderLayers().map { it.catalogItem?.id }
        assertEquals(
            "Tail with z_override=45 must render after skirt (40) and before top (50)",
            listOf("bottom_skirt", "tail_fox_front", "top_shirt"),
            frontOrder
        )
    }

    @Test
    fun masterExporter_rendersLayersInZOverrideOrder() {
        val skirt = CatalogItem(
            id = "bottom_skirt",
            category = "bottom",
            layer = "bottom",
            file = "items/bottom/bottom_skirt.png",
            thumbnail = "items/thumbnails/thumb_skirt.png"
        )
        val frontTail = CatalogItem(
            id = "tail_fox_front",
            category = "tail",
            layer = "tail",
            file = "items/tail/tail_fox_front.png",
            thumbnail = "items/thumbnails/thumb_tail_fox_front.png",
            zOverride = 45
        )

        val look = CharacterLook(
            equippedItems = mapOf(
                Layer.BOTTOM.name to skirt,
                Layer.TAIL.name to frontTail
            )
        )

        val renderSequence = mutableListOf<String>()

        DollImageExporter.renderMasterBitmapWithLoader(
            look = look,
            bitmapLoader = { path ->
                renderSequence.add(path)
                Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
            }
        )

        assertEquals(
            "Exporter must draw skirt first, then front tail over it",
            listOf("items/bottom/bottom_skirt.png", "items/tail/tail_fox_front.png"),
            renderSequence
        )
    }
}

