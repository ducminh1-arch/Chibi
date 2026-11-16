package com.chibidoll.dressup

import com.chibidoll.dressup.data.local.SavedLookMapper
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.ItemCategory
import com.chibidoll.dressup.domain.model.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedLookMapperTest {

    private val sampleBody = CatalogItem(
        id = "body_01",
        category = "body",
        layer = "body",
        file = "items/body_01.png",
        thumbnail = "items/thumbnails/thumb_body_01.png"
    )

    private val sampleHair = CatalogItem(
        id = "hair_01",
        category = "hair",
        layer = "hair_front",
        file = "items/hair_01_front.png",
        thumbnail = "items/thumbnails/thumb_hair_01.png",
        secondaryLayer = "hair_back",
        secondaryFile = "items/hair_01_back.png"
    )

    private val sampleTop = CatalogItem(
        id = "top_01",
        category = "top",
        layer = "top",
        file = "items/top_01.png",
        thumbnail = "items/thumbnails/thumb_top_01.png"
    )

    private val sampleBottom = CatalogItem(
        id = "bottom_01",
        category = "bottom",
        layer = "bottom",
        file = "items/bottom_01.png",
        thumbnail = "items/thumbnails/thumb_bottom_01.png"
    )

    private val sampleHat = CatalogItem(
        id = "hat_01",
        category = "hat",
        layer = "hat",
        file = "items/hat_01.png",
        thumbnail = "items/thumbnails/thumb_hat_01.png"
    )

    private val sampleBg = CatalogItem(
        id = "bg_dream",
        category = "background",
        layer = "background",
        file = "items/bg_dream.png",
        thumbnail = "items/thumbnails/thumb_bg_dream.png"
    )

    private val catalog = listOf(sampleBody, sampleHair, sampleTop, sampleBottom, sampleHat, sampleBg)

    @Test
    fun toEntity_and_toCharacterLook_preservesAllEquippedItemsAndBackground() {
        val originalLook = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to sampleBody,
                Layer.HAIR_FRONT.name to sampleHair,
                Layer.TOP.name to sampleTop,
                Layer.BOTTOM.name to sampleBottom,
                Layer.HAT.name to sampleHat
            ),
            backgroundItem = sampleBg,
            presetId = "preset_luna"
        )

        val entity = SavedLookMapper.toEntity(
            look = originalLook,
            thumbnailPath = "/app/files/thumbnails/look_42.png",
            id = 42L,
            createdAt = 1700000000000L
        )

        assertEquals(42L, entity.id)
        assertEquals(1700000000000L, entity.createdAt)
        assertEquals("bg_dream", entity.backgroundId)
        assertEquals("/app/files/thumbnails/look_42.png", entity.thumbnailPath)
        assertEquals("preset_luna", entity.presetId)

        // Restore back to CharacterLook
        val restoredLook = SavedLookMapper.toCharacterLook(entity, catalog)

        assertEquals(originalLook.equippedItems.size, restoredLook.equippedItems.size)
        assertEquals("preset_luna", restoredLook.presetId)
        assertEquals("body_01", restoredLook.equippedItems[Layer.BODY.name]?.id)
        assertEquals("hair_01", restoredLook.equippedItems[Layer.HAIR_FRONT.name]?.id)
        assertEquals("top_01", restoredLook.equippedItems[Layer.TOP.name]?.id)
        assertEquals("bottom_01", restoredLook.equippedItems[Layer.BOTTOM.name]?.id)
        assertEquals("hat_01", restoredLook.equippedItems[Layer.HAT.name]?.id)
        assertNotNull(restoredLook.backgroundItem)
        assertEquals("bg_dream", restoredLook.backgroundItem?.id)
    }

    @Test
    fun toEntity_withEmptyLook_serializesAndRestoresSafely() {
        val emptyLook = CharacterLook()

        val entity = SavedLookMapper.toEntity(
            look = emptyLook,
            thumbnailPath = "/path/thumb.png",
            id = 1L
        )

        assertNull(entity.backgroundId)
        assertTrue(entity.equippedItemIdsJson.contains("{}") || entity.equippedItemIdsJson.isEmpty() || entity.equippedItemIdsJson == "{}")

        val restored = SavedLookMapper.toCharacterLook(entity, catalog)
        assertTrue(restored.equippedItems.isEmpty())
        assertNull(restored.backgroundItem)
    }

    @Test
    fun toCharacterLook_handlesMissingCatalogItemsGracefully() {
        val entity = SavedLookMapper.toEntity(
            look = CharacterLook(
                equippedItems = mapOf(Layer.TOP.name to sampleTop),
                backgroundItem = sampleBg
            ),
            thumbnailPath = "/path/thumb.png",
            id = 5L
        )

        // Empty catalog: items won't be found, should gracefully return empty map without crashing
        val restoredWithEmptyCatalog = SavedLookMapper.toCharacterLook(entity, emptyList())
        assertTrue(restoredWithEmptyCatalog.equippedItems.isEmpty())
        assertNull(restoredWithEmptyCatalog.backgroundItem)
    }

    @Test
    fun toEntity_and_toCharacterLook_preservesLayerColors() {
        val originalLook = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to sampleBody,
                Layer.HAIR_FRONT.name to sampleHair,
                Layer.TOP.name to sampleTop
            ),
            backgroundItem = sampleBg,
            layerColors = mapOf(
                Layer.HAIR_FRONT.name to 0xFFFF8DAAL,
                Layer.TOP.name to 0xFFB5EAD7L
            )
        )

        val entity = SavedLookMapper.toEntity(
            look = originalLook,
            thumbnailPath = "/app/files/thumbnails/look_99.png",
            id = 99L
        )

        assertTrue(entity.layerColorsJson.contains("HAIR_FRONT"))
        assertTrue(entity.layerColorsJson.contains("TOP"))

        val restored = SavedLookMapper.toCharacterLook(entity, catalog)
        assertEquals(2, restored.layerColors.size)
        assertEquals(0xFFFF8DAAL, restored.layerColors[Layer.HAIR_FRONT.name])
        assertEquals(0xFFB5EAD7L, restored.layerColors[Layer.TOP.name])
    }

    @Test
    fun toEntity_and_toCharacterLook_preservesAllPhase4cLayersRoundTrip() {
        val eyesItem = CatalogItem(
            id = "eyes_purple",
            category = "eyes",
            layer = "eyes",
            file = "items/eyes/eyes_purple_open.png",
            altFile = "items/eyes/eyes_purple_closed.png",
            thumbnail = "thumb_eyes.png",
            colorable = true,
            palette = "eyes"
        )
        val browItem = CatalogItem(
            id = "brow_gentle",
            category = "eyebrows",
            layer = "eyebrows",
            file = "items/eyebrows/brow_gentle.png",
            thumbnail = "thumb_brow.png"
        )
        val mouthItem = CatalogItem(
            id = "mouth_smile",
            category = "mouth",
            layer = "mouth",
            file = "items/mouth/mouth_smile.png",
            thumbnail = "thumb_mouth.png"
        )
        val blushItem = CatalogItem(
            id = "blush_pink",
            category = "blush",
            layer = "blush",
            file = "items/blush/blush_pink.png",
            thumbnail = "thumb_blush.png"
        )
        val socksItem = CatalogItem(
            id = "socks_knee",
            category = "socks",
            layer = "socks",
            file = "items/socks/socks_knee.png",
            thumbnail = "thumb_socks.png"
        )
        val glassesItem = CatalogItem(
            id = "glasses_gold",
            category = "glasses",
            layer = "glasses",
            file = "items/glasses/glasses_gold.png",
            thumbnail = "thumb_glasses.png"
        )
        val earsItem = CatalogItem(
            id = "ears_elf",
            category = "ears",
            layer = "ears",
            file = "items/ears/ears_elf.png",
            thumbnail = "thumb_ears.png"
        )
        val tailItem = CatalogItem(
            id = "tail_cat",
            category = "tail",
            layer = "tail",
            file = "items/tail/tail_cat.png",
            thumbnail = "thumb_tail.png"
        )
        val wingsItem = CatalogItem(
            id = "wings_angel",
            category = "wings",
            layer = "wings",
            file = "items/wings/wings_angel.png",
            thumbnail = "thumb_wings.png"
        )

        val fullCatalog = catalog + listOf(
            eyesItem, browItem, mouthItem, blushItem,
            socksItem, glassesItem, earsItem, tailItem, wingsItem
        )

        val look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to sampleBody,
                Layer.HAIR_FRONT.name to sampleHair,
                Layer.EYES.name to eyesItem,
                Layer.EYEBROWS.name to browItem,
                Layer.MOUTH.name to mouthItem,
                Layer.BLUSH.name to blushItem,
                Layer.SOCKS.name to socksItem,
                Layer.GLASSES.name to glassesItem,
                Layer.EARS.name to earsItem,
                Layer.TAIL.name to tailItem,
                Layer.WINGS.name to wingsItem,
                Layer.TOP.name to sampleTop,
                Layer.BOTTOM.name to sampleBottom
            ),
            backgroundItem = sampleBg,
            presetId = "preset_luna",
            layerColors = mapOf(
                Layer.EYES.name to 0xFF9C27B0L,
                Layer.BODY.name to 0xFFFFE0BDL
            )
        )

        val entity = SavedLookMapper.toEntity(
            look = look,
            thumbnailPath = "/path/thumb_phase4c.png",
            id = 100L
        )

        val restored = SavedLookMapper.toCharacterLook(entity, fullCatalog)

        assertEquals("preset_luna", restored.presetId)
        assertEquals(13, restored.equippedItems.size)
        assertEquals("eyes_purple", restored.equippedItems[Layer.EYES.name]?.id)
        assertEquals("brow_gentle", restored.equippedItems[Layer.EYEBROWS.name]?.id)
        assertEquals("mouth_smile", restored.equippedItems[Layer.MOUTH.name]?.id)
        assertEquals("blush_pink", restored.equippedItems[Layer.BLUSH.name]?.id)
        assertEquals("socks_knee", restored.equippedItems[Layer.SOCKS.name]?.id)
        assertEquals("glasses_gold", restored.equippedItems[Layer.GLASSES.name]?.id)
        assertEquals("ears_elf", restored.equippedItems[Layer.EARS.name]?.id)
        assertEquals("tail_cat", restored.equippedItems[Layer.TAIL.name]?.id)
        assertEquals("wings_angel", restored.equippedItems[Layer.WINGS.name]?.id)

        // Check eyes and body colors
        assertEquals(0xFF9C27B0L, restored.layerColors[Layer.EYES.name])
        assertEquals(0xFFFFE0BDL, restored.layerColors[Layer.BODY.name])
    }
}

