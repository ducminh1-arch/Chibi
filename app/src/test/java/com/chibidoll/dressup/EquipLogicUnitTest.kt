package com.chibidoll.dressup

import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.Layer
import com.chibidoll.dressup.domain.usecase.EquipItemUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EquipLogicUnitTest {

    private lateinit var equipItemUseCase: EquipItemUseCase

    private val bodyItem = CatalogItem(
        id = "body_fair",
        category = "body",
        layer = "body",
        file = "items/body/body_fair.png",
        thumbnail = "items/thumbnails/thumb_body_fair.png",
        nameEn = "Fair Body",
        nameVi = "Cơ thể hồng"
    )

    private val topItem = CatalogItem(
        id = "top_sailor",
        category = "top",
        layer = "top",
        file = "items/top/top_sailor.png",
        thumbnail = "items/thumbnails/thumb_top_sailor.png",
        nameEn = "Sailor Blouse",
        nameVi = "Áo thủy thủ"
    )

    private val bottomItem = CatalogItem(
        id = "bot_navy_skirt",
        category = "bottom",
        layer = "bottom",
        file = "items/bottom/bottom_navy_skirt.png",
        thumbnail = "items/thumbnails/thumb_bot_navy.png",
        nameEn = "Navy Skirt",
        nameVi = "Váy xanh"
    )

    private val dressItem = CatalogItem(
        id = "dress_princess",
        category = "dress",
        layer = "dress",
        file = "items/dress/dress_princess_pink.png",
        thumbnail = "items/thumbnails/thumb_dress_pink.png",
        nameEn = "Princess Gown",
        nameVi = "Váy công chúa"
    )

    private val hairItem = CatalogItem(
        id = "hair_lilac_twin",
        category = "hair",
        layer = "hair_front",
        secondaryLayer = "hair_back",
        file = "items/hair/hair_lilac_front.png",
        secondaryFile = "items/hair/hair_lilac_back.png",
        thumbnail = "items/thumbnails/thumb_hair_lilac.png",
        nameEn = "Lilac Twin",
        nameVi = "Tóc tím"
    )

    private val hatItem = CatalogItem(
        id = "hat_bow",
        category = "hat",
        layer = "hat",
        file = "items/hat/hat_giant_bow.png",
        thumbnail = "items/thumbnails/thumb_hat_bow.png",
        nameEn = "Giant Bow",
        nameVi = "Nơ to"
    )

    private val bgItem1 = CatalogItem(
        id = "bg_dream",
        category = "background",
        layer = "background",
        file = "items/backgrounds/bg_pastel_dream.png",
        thumbnail = "items/thumbnails/thumb_bg_dream.png",
        nameEn = "Pastel Dream",
        nameVi = "Mộng mơ"
    )

    private val bgItem2 = CatalogItem(
        id = "bg_stars",
        category = "background",
        layer = "background",
        file = "items/backgrounds/bg_starry_twilight.png",
        thumbnail = "items/thumbnails/thumb_bg_stars.png",
        nameEn = "Starry Twilight",
        nameVi = "Đêm sao"
    )

    @Before
    fun setup() {
        equipItemUseCase = EquipItemUseCase()
    }

    @Test
    fun equippingDressRemovesTopAndBottom() {
        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.TOP.name to topItem,
                Layer.BOTTOM.name to bottomItem
            )
        )
        assertTrue(look.isEquipped(topItem))
        assertTrue(look.isEquipped(bottomItem))

        // Equip dress
        look = equipItemUseCase(look, dressItem)

        assertTrue("Dress should be equipped", look.isEquipped(dressItem))
        assertFalse("Top should be removed when dress is equipped", look.isEquipped(topItem))
        assertFalse("Bottom should be removed when dress is equipped", look.isEquipped(bottomItem))
        assertTrue("Body should remain", look.isEquipped(bodyItem))
    }

    @Test
    fun equippingTopRemovesDress() {
        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.DRESS.name to dressItem
            )
        )
        assertTrue(look.isEquipped(dressItem))

        // Equip top
        look = equipItemUseCase(look, topItem)

        assertTrue("Top should be equipped", look.isEquipped(topItem))
        assertFalse("Dress should be removed when top is equipped", look.isEquipped(dressItem))
    }

    @Test
    fun equippingBottomRemovesDress() {
        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.DRESS.name to dressItem
            )
        )
        assertTrue(look.isEquipped(dressItem))

        // Equip bottom
        look = equipItemUseCase(look, bottomItem)

        assertTrue("Bottom should be equipped", look.isEquipped(bottomItem))
        assertFalse("Dress should be removed when bottom is equipped", look.isEquipped(dressItem))
    }

    @Test
    fun tapEquippedItemUnequipsIt() {
        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.HAT.name to hatItem
            )
        )
        assertTrue(look.isEquipped(hatItem))

        // Tap hat again to unequip
        look = equipItemUseCase(look, hatItem)

        assertFalse("Hat should be unequipped", look.isEquipped(hatItem))
    }

    @Test
    fun cannotUnequipBodyBase() {
        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem
            )
        )
        look = equipItemUseCase(look, bodyItem)
        assertTrue("Body cannot be unequipped", look.isEquipped(bodyItem))
    }

    @Test
    fun hairEquipsBothFrontAndBackInRenderLayers() {
        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem
            )
        )
        look = equipItemUseCase(look, hairItem)

        val renderLayers = look.getRenderLayers()
        val layerEnums = renderLayers.map { it.first }

        assertTrue(layerEnums.contains(Layer.HAIR_BACK))
        assertTrue(layerEnums.contains(Layer.HAIR_FRONT))
    }

    @Test
    fun renderLayersAreStrictlyOrderedByZIndex() {
        val look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.HAIR_FRONT.name to hairItem,
                Layer.HAT.name to hatItem,
                Layer.TOP.name to topItem
            )
        )

        val renderLayers = look.getRenderLayers()
        val zIndices = renderLayers.map { it.first.zIndex }

        // Must be in ascending order
        assertEquals(zIndices.sorted(), zIndices)
        assertEquals(Layer.HAIR_BACK, renderLayers.first().first) // zIndex 10
        assertEquals(Layer.HAT, renderLayers.last().first) // zIndex 100
    }

    @Test
    fun backgroundToggleWorks() {
        var look = CharacterLook(
            backgroundItem = null
        )
        // Equip background
        look = equipItemUseCase(look, bgItem1)
        assertEquals("bg_dream", look.backgroundItem?.id)
        assertTrue(look.isEquipped(bgItem1))

        // Tap again to unequip
        look = equipItemUseCase(look, bgItem1)
        assertNull(look.backgroundItem)
        assertFalse(look.isEquipped(bgItem1))
    }

    @Test
    fun selectBackgroundExplicitlySetsBackground() {
        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.TOP.name to topItem
            ),
            backgroundItem = null
        )

        // Select background 1
        look = equipItemUseCase.selectBackground(look, bgItem1)
        assertEquals("bg_dream", look.backgroundItem?.id)
        assertTrue(look.isEquipped(bgItem1))
        // Verify clothes are preserved
        assertTrue("Body preserved", look.isEquipped(bodyItem))
        assertTrue("Top preserved", look.isEquipped(topItem))

        // Swap to background 2
        look = equipItemUseCase.selectBackground(look, bgItem2)
        assertEquals("bg_stars", look.backgroundItem?.id)
        assertTrue(look.isEquipped(bgItem2))
        assertFalse(look.isEquipped(bgItem1))
        assertTrue("Clothes still preserved", look.isEquipped(topItem))

        // Select null for "No Background" option
        look = equipItemUseCase.selectBackground(look, null)
        assertNull("Background must be null", look.backgroundItem)
        assertFalse(look.isEquipped(bgItem1))
        assertFalse(look.isEquipped(bgItem2))
        assertTrue("Clothes still preserved with no background", look.isEquipped(topItem))
    }

    @Test
    fun socksAndShoes_stackWithoutConflict() {
        val socksItem = CatalogItem(
            id = "socks_knee",
            category = "socks",
            layer = "socks",
            file = "items/socks/socks_white_knee.png",
            thumbnail = "thumb_socks.png"
        )
        val shoesItem = CatalogItem(
            id = "shoes_mary_jane",
            category = "shoes",
            layer = "shoes",
            file = "items/shoes/shoes_mary_jane.png",
            thumbnail = "thumb_shoes.png"
        )

        var look = CharacterLook(
            equippedItems = mapOf(Layer.BODY.name to bodyItem)
        )

        // Equip socks
        look = equipItemUseCase(look, socksItem)
        assertTrue(look.isEquipped(socksItem))

        // Equip shoes
        look = equipItemUseCase(look, shoesItem)
        // Both socks and shoes must remain equipped!
        assertTrue("Socks must remain equipped when shoes are equipped", look.isEquipped(socksItem))
        assertTrue("Shoes must be equipped", look.isEquipped(shoesItem))
        assertEquals(3, look.equippedItems.size) // body, socks, shoes
    }

    @Test
    fun equippingDiscreteFacePart_removesLegacyWholeFace() {
        val legacyFace = CatalogItem(
            id = "face_legacy",
            category = "face",
            layer = "face",
            file = "items/face/legacy.png",
            thumbnail = "thumb_face.png"
        )
        val eyesItem = CatalogItem(
            id = "eyes_purple",
            category = "eyes",
            layer = "eyes",
            file = "items/eyes/eyes_purple_open.png",
            thumbnail = "thumb_eyes.png"
        )

        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.FACE.name to legacyFace
            )
        )
        assertTrue(look.isEquipped(legacyFace))

        // Equip discrete eyes
        look = equipItemUseCase(look, eyesItem)
        assertTrue(look.isEquipped(eyesItem))
        assertFalse("Legacy face must be removed when discrete face part is equipped", look.isEquipped(legacyFace))
    }

    @Test
    fun essentialFeatures_bodyEyesMouth_cannotBeUnequipped() {
        val eyesItem = CatalogItem(
            id = "eyes_purple",
            category = "eyes",
            layer = "eyes",
            file = "items/eyes/eyes_purple_open.png",
            thumbnail = "thumb_eyes.png"
        )
        val mouthItem = CatalogItem(
            id = "mouth_smile",
            category = "mouth",
            layer = "mouth",
            file = "items/mouth/mouth_smile.png",
            thumbnail = "thumb_mouth.png"
        )

        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.EYES.name to eyesItem,
                Layer.MOUTH.name to mouthItem
            )
        )

        // Tap body -> cannot unequip
        look = equipItemUseCase(look, bodyItem)
        assertTrue("Body cannot be unequipped", look.isEquipped(bodyItem))

        // Tap eyes -> cannot unequip
        look = equipItemUseCase(look, eyesItem)
        assertTrue("Eyes cannot be unequipped", look.isEquipped(eyesItem))

        // Tap mouth -> cannot unequip
        look = equipItemUseCase(look, mouthItem)
        assertTrue("Mouth cannot be unequipped", look.isEquipped(mouthItem))
    }

    @Test
    fun conflictsWithRule_unEquipsConflictingLayers() {
        val catEarsWithHatConflict = CatalogItem(
            id = "ears_cat",
            category = "ears",
            layer = "ears",
            file = "items/ears/ears_cat_white.png",
            thumbnail = "thumb_ears.png",
            conflictsWith = listOf("hat")
        )

        var look = CharacterLook(
            equippedItems = mapOf(
                Layer.BODY.name to bodyItem,
                Layer.HAT.name to hatItem
            )
        )
        assertTrue(look.isEquipped(hatItem))

        // Equip ears with hat conflict -> hat must be unequipped!
        look = equipItemUseCase(look, catEarsWithHatConflict)
        assertTrue(look.isEquipped(catEarsWithHatConflict))
        assertFalse("Hat must be unequipped due to conflictsWith", look.isEquipped(hatItem))

        // Equip hat again -> cat ears must be unequipped!
        look = equipItemUseCase(look, hatItem)
        assertTrue(look.isEquipped(hatItem))
        assertFalse("Cat ears must be unequipped when conflicting hat is equipped", look.isEquipped(catEarsWithHatConflict))
    }
}
