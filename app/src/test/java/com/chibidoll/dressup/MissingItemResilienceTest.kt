package com.chibidoll.dressup

import android.graphics.Bitmap
import android.graphics.Color
import com.chibidoll.dressup.data.local.SavedLookEntity
import com.chibidoll.dressup.data.local.SavedLookMapper
import com.chibidoll.dressup.domain.export.DollImageExporter
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MissingItemResilienceTest {

    private val validShoes = CatalogItem(
        id = "shoes_valid",
        category = "shoes",
        layer = "shoes",
        file = "items/shoes_valid.png",
        thumbnail = "items/thumbnails/thumb_shoes.png"
    )

    private val currentCatalog = listOf(validShoes)

    @Test
    fun savedLookMapper_skipsDeletedItemsAndMissingBackgroundSafely() {
        // Saved look referencing items and background that were removed from catalog.json
        val entityWithDeletedItems = SavedLookEntity(
            id = 100L,
            createdAt = 123456789L,
            equippedItemIdsJson = """{"shoes":"shoes_valid","top":"deleted_top_99","hat":"deleted_hat_88"}""",
            backgroundId = "deleted_bg_77",
            thumbnailPath = "/app/thumbnails/look_100.png"
        )

        val restoredLook = SavedLookMapper.toCharacterLook(entityWithDeletedItems, currentCatalog)

        // Only valid item remains; missing top and hat are skipped
        assertEquals(1, restoredLook.equippedItems.size)
        assertEquals("shoes_valid", restoredLook.equippedItems[Layer.SHOES.name]?.id)
        assertNull(restoredLook.equippedItems[Layer.TOP.name])
        assertNull(restoredLook.equippedItems[Layer.HAT.name])

        // Missing background gracefully falls back to null
        assertNull(restoredLook.backgroundItem)
    }

    @Test
    fun savedLookMapper_handlesCorruptedJsonGracefully() {
        val corruptedEntity = SavedLookEntity(
            id = 101L,
            createdAt = 123456789L,
            equippedItemIdsJson = "MALFORMED_CORRUPTED_JSON_DATA",
            backgroundId = "shoes_valid", // even if backgroundId matches
            thumbnailPath = "/app/thumbnails/look_101.png"
        )

        val restored = SavedLookMapper.toCharacterLook(corruptedEntity, currentCatalog)

        // Gracefully handles without throwing
        assertNotNull(restored)
        assertEquals(0, restored.equippedItems.size)
    }

    @Test
    fun dollImageExporter_skipsMissingAssetBitmapsWithoutCrashing() {
        val ghostItem = CatalogItem(
            id = "ghost_dress",
            category = "dress",
            layer = "dress",
            file = "items/does_not_exist.png",
            thumbnail = ""
        )
        val validItem = CatalogItem(
            id = "valid_shoes",
            category = "shoes",
            layer = "shoes",
            file = "items/valid_shoes.png",
            thumbnail = ""
        )

        val lookWithMissingAsset = CharacterLook(
            equippedItems = mapOf(
                Layer.DRESS.name to ghostItem,
                Layer.SHOES.name to validItem
            ),
            backgroundItem = CatalogItem(
                id = "ghost_bg",
                category = "background",
                layer = "background",
                file = "items/bg_missing.png",
                thumbnail = ""
            )
        )

        val validBitmap = Bitmap.createBitmap(
            DollImageExporter.MASTER_WIDTH,
            DollImageExporter.MASTER_HEIGHT,
            Bitmap.Config.ARGB_8888
        ).apply { eraseColor(Color.CYAN) }

        // Bitmap loader returns null for ghost assets, but succeeds for valid_shoes
        val output = DollImageExporter.renderMasterBitmapWithLoader(
            look = lookWithMissingAsset,
            bitmapLoader = { path ->
                if (path == "items/valid_shoes.png") validBitmap else null
            },
            recycleLoadedBitmaps = false
        )

        assertNotNull(output)
        assertEquals(DollImageExporter.MASTER_WIDTH, output.width)
        assertEquals(DollImageExporter.MASTER_HEIGHT, output.height)
    }
}

