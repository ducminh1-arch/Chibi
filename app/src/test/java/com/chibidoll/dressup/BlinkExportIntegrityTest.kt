package com.chibidoll.dressup

import android.graphics.Bitmap
import android.graphics.Color
import com.chibidoll.dressup.domain.export.DollImageExporter
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BlinkExportIntegrityTest {

    @Test
    fun export_alwaysUsesOpenEyeFile_andNeverClosedAltFile() {
        val openFilePath = "items/eyes/eyes_purple_open.png"
        val closedFilePath = "items/eyes/eyes_purple_closed.png"

        val eyesItem = CatalogItem(
            id = "eyes_purple",
            category = "eyes",
            layer = "eyes",
            file = openFilePath,
            altFile = closedFilePath,
            thumbnail = "items/thumbnails/thumb_eyes_purple_open.png",
            nameEn = "Sparkle Purple Eyes",
            nameVi = "Mắt Tím Lấp Lánh",
            colorable = true,
            palette = "eyes"
        )

        val look = CharacterLook(
            equippedItems = mapOf(
                Layer.EYES.name to eyesItem
            )
        )

        // Verify that RenderLayerInfo exposes altFilePath for Compose idle blink
        val detailedLayers = look.getDetailedRenderLayers()
        val eyeLayerInfo = detailedLayers.firstOrNull { it.layer == Layer.EYES }
        assertTrue("Eyes layer must be present", eyeLayerInfo != null)
        assertEquals(openFilePath, eyeLayerInfo!!.filePath)
        assertEquals(closedFilePath, eyeLayerInfo.altFilePath)

        // Track files loaded during export
        val loadedFiles = mutableListOf<String>()

        val masterBitmap = DollImageExporter.renderMasterBitmapWithLoader(
            look = look,
            bitmapLoader = { path ->
                loadedFiles.add(path)
                // Return a dummy 1024x1536 solid bitmap
                Bitmap.createBitmap(
                    DollImageExporter.MASTER_WIDTH,
                    DollImageExporter.MASTER_HEIGHT,
                    Bitmap.Config.ARGB_8888
                )
            }
        )

        // 1. Master bitmap must be rendered
        assertEquals(DollImageExporter.MASTER_WIDTH, masterBitmap.width)
        assertEquals(DollImageExporter.MASTER_HEIGHT, masterBitmap.height)

        // 2. Open eye file was loaded for export
        assertTrue("Export must load open eyes file", loadedFiles.contains(openFilePath))

        // 3. Alt closed eye file was NEVER loaded for export
        assertFalse("Export must NEVER load closed alt_file (blink state)", loadedFiles.contains(closedFilePath))
    }
}

