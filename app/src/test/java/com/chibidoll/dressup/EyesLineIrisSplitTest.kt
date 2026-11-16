package com.chibidoll.dressup

import android.graphics.Bitmap
import android.graphics.Color
import com.chibidoll.dressup.domain.export.DollImageExporter
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class EyesLineIrisSplitTest {

    @Test
    fun eyesLineAndIris_properZOrder_andTintSeparation() {
        val dualEyeItem = CatalogItem(
            id = "eyes_purple",
            category = "eyes",
            layer = "eyes_line",
            file = "items/eyes/eyes_purple_line_open.png",
            altFile = "items/eyes/eyes_purple_line_closed.png",
            secondaryLayer = "eyes_iris",
            secondaryFile = "items/eyes/eyes_purple_iris_open.png",
            secondaryAltFile = "items/eyes/eyes_purple_iris_closed.png",
            thumbnail = "items/thumbnails/thumb_eyes_purple.png",
            colorable = true,
            palette = "eyes"
        )

        // Equipping eye item and choosing a purple tint (0xFF9C27B0)
        val initialLook = CharacterLook(
            equippedItems = mapOf(
                Layer.EYES_LINE.name to dualEyeItem
            )
        )
        val tintedLook = initialLook.withLayerColor(Layer.EYES_IRIS, 0xFF9C27B0L)

        val renderLayers = tintedLook.getDetailedRenderLayers()
        assertEquals("Must render 2 layers (iris and line)", 2, renderLayers.size)

        val irisLayer = renderLayers[0]
        val lineLayer = renderLayers[1]

        // 1. Iris layer: zIndex 79, colorable = true, tinted
        assertEquals(Layer.EYES_IRIS, irisLayer.layer)
        assertEquals(79, irisLayer.zIndex)
        assertTrue("Iris must be colorable", irisLayer.isColorable)
        assertEquals(0xFF9C27B0L, irisLayer.colorTint)
        assertEquals("items/eyes/eyes_purple_iris_open.png", irisLayer.filePath)
        assertEquals("items/eyes/eyes_purple_iris_closed.png", irisLayer.altFilePath)

        // 2. Line layer: zIndex 82, colorable = false, untinted
        assertEquals(Layer.EYES_LINE, lineLayer.layer)
        assertEquals(82, lineLayer.zIndex)
        assertFalse("Line layer must NOT be colorable", lineLayer.isColorable)
        assertNull("Line layer must NOT have color tint", lineLayer.colorTint)
        assertEquals("items/eyes/eyes_purple_line_open.png", lineLayer.filePath)
        assertEquals("items/eyes/eyes_purple_line_closed.png", lineLayer.altFilePath)
    }

    @Test
    fun export_rendersIrisThenLine_andNeverClosedAltFile() {
        val eyeItem = CatalogItem(
            id = "eyes_purple",
            category = "eyes",
            layer = "eyes_line",
            file = "items/eyes/eyes_purple_line_open.png",
            altFile = "items/eyes/eyes_purple_line_closed.png",
            secondaryLayer = "eyes_iris",
            secondaryFile = "items/eyes/eyes_purple_iris_open.png",
            secondaryAltFile = "items/eyes/eyes_purple_iris_closed.png",
            thumbnail = "items/thumbnails/thumb_eyes_purple.png",
            colorable = true,
            palette = "eyes"
        )

        val look = CharacterLook(
            equippedItems = mapOf(
                Layer.EYES_LINE.name to eyeItem
            )
        ).withLayerColor(Layer.EYES, 0xFF2196F3L)

        val loadedPaths = mutableListOf<String>()

        DollImageExporter.renderMasterBitmapWithLoader(
            look = look,
            bitmapLoader = { path ->
                loadedPaths.add(path)
                Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
            }
        )

        assertEquals("Export must composite iris (79) first, then line (82)", 2, loadedPaths.size)
        assertEquals("items/eyes/eyes_purple_iris_open.png", loadedPaths[0])
        assertEquals("items/eyes/eyes_purple_line_open.png", loadedPaths[1])

        assertFalse("Export must NOT load closed alt_file", loadedPaths.contains("items/eyes/eyes_purple_line_closed.png"))
        assertFalse("Export must NOT load closed alt_file", loadedPaths.contains("items/eyes/eyes_purple_iris_closed.png"))
    }
}

