package com.chibidoll.dressup

import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.CharacterPreset
import com.chibidoll.dressup.domain.model.ItemCategory
import com.chibidoll.dressup.domain.model.Layer
import com.chibidoll.dressup.domain.usecase.EquipItemUseCase
import com.chibidoll.dressup.domain.usecase.RandomizeLookUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterPresetTest {

    private val bodyItem = CatalogItem(
        id = "body_fair",
        category = "body",
        layer = "body",
        file = "items/body/body_fair.png",
        thumbnail = "items/thumbnails/thumb_body_fair.png"
    )

    private val faceLuna = CatalogItem(
        id = "face_purple",
        category = "face",
        layer = "face",
        file = "items/face/face_purple.png",
        thumbnail = "items/thumbnails/thumb_face_purple.png"
    )

    private val hairLuna = CatalogItem(
        id = "hair_lilac_twin",
        category = "hair",
        layer = "hair_front",
        file = "items/hair/hair_lilac_front.png",
        thumbnail = "items/thumbnails/thumb_hair_lilac.png"
    )

    private val topSailor = CatalogItem(
        id = "top_sailor",
        category = "top",
        layer = "top",
        file = "items/top/top_sailor.png",
        thumbnail = "items/thumbnails/thumb_top_sailor.png"
    )

    private val dressPrincess = CatalogItem(
        id = "dress_princess",
        category = "dress",
        layer = "dress",
        file = "items/dress/dress_princess.png",
        thumbnail = "items/thumbnails/thumb_dress_princess.png"
    )

    private val catalog = listOf(bodyItem, faceLuna, hairLuna, topSailor, dressPrincess)

    @Test
    fun defaultPresets_containsFourUniquePresets() {
        val presets = CharacterPreset.defaultPresets()
        assertEquals(4, presets.size)
        val ids = presets.map { it.id }.toSet()
        assertEquals(4, ids.size)
        assertTrue(ids.contains("preset_luna"))
        assertTrue(ids.contains("preset_sakura"))
        assertTrue(ids.contains("preset_sunny"))
        assertTrue(ids.contains("preset_mimi"))
    }

    @Test
    fun randomize_withPresetLook_preservesPresetBodyFaceAndHair() {
        val presetLook = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.FACE.name to faceLuna,
                Layer.HAIR_FRONT.name to hairLuna,
                Layer.TOP.name to topSailor
            ),
            presetId = "preset_luna"
        )

        val randomizeUseCase = RandomizeLookUseCase()
        val randomizedLook = randomizeUseCase(catalog, baseLook = presetLook)

        // Preset body, face, hair must be preserved!
        assertEquals("body_fair", randomizedLook.equippedItems[Layer.BODY.name]?.id)
        assertEquals("face_purple", randomizedLook.equippedItems[Layer.FACE.name]?.id)
        assertEquals("hair_lilac_twin", randomizedLook.equippedItems[Layer.HAIR_FRONT.name]?.id)
        assertEquals("preset_luna", randomizedLook.presetId)
    }

    @Test
    fun dressUpMode_allowsEquippingAndExchangingClothesOnPresetLook() {
        val presetLook = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.FACE.name to faceLuna,
                Layer.HAIR_FRONT.name to hairLuna,
                Layer.TOP.name to topSailor
            ),
            presetId = "preset_luna"
        )

        val equipUseCase = EquipItemUseCase()
        // Equipping dress should remove top, while keeping body, face, and hair!
        val afterDress = equipUseCase(presetLook, dressPrincess)

        assertEquals("body_fair", afterDress.equippedItems[Layer.BODY.name]?.id)
        assertEquals("face_purple", afterDress.equippedItems[Layer.FACE.name]?.id)
        assertEquals("hair_lilac_twin", afterDress.equippedItems[Layer.HAIR_FRONT.name]?.id)
        assertEquals("dress_princess", afterDress.equippedItems[Layer.DRESS.name]?.id)
        assertTrue(!afterDress.equippedItems.containsKey(Layer.TOP.name))
    }

    @Test
    fun restoredLook_withPresetId_preservesPresetOnSubsequentRandomize() {
        // Look restored via Edit from Room with presetId
        val restoredLook = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.FACE.name to faceLuna,
                Layer.HAIR_FRONT.name to hairLuna,
                Layer.TOP.name to topSailor
            ),
            presetId = "preset_luna"
        )

        val randomizeUseCase = RandomizeLookUseCase()
        val postRandomizeLook = randomizeUseCase(catalog, baseLook = restoredLook)

        assertEquals("preset_luna", postRandomizeLook.presetId)
        assertEquals("body_fair", postRandomizeLook.equippedItems[Layer.BODY.name]?.id)
        assertEquals("face_purple", postRandomizeLook.equippedItems[Layer.FACE.name]?.id)
        assertEquals("hair_lilac_twin", postRandomizeLook.equippedItems[Layer.HAIR_FRONT.name]?.id)
    }

    @Test
    fun restoredLook_withNullPresetId_handledGracefullyWithoutCrash() {
        val legacyLook = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.TOP.name to topSailor
            ),
            presetId = null
        )

        val randomizeUseCase = RandomizeLookUseCase()
        val randomized = randomizeUseCase(catalog, baseLook = legacyLook)

        // Does not crash, body is present, presetId is null
        assertNotNull(randomized.equippedItems[Layer.BODY.name])
        assertEquals(null, randomized.presetId)
    }
}

