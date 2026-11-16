package com.chibidoll.dressup

import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportLayerOrderingTest {

    @Test
    fun zOrderEnum_strictlyAscendingMatchesPaperDollSpecification() {
        val expectedOrder = listOf(
            Layer.BACKGROUND,
            Layer.WINGS,
            Layer.HAIR_BACK,
            Layer.TAIL,
            Layer.BODY,
            Layer.SOCKS,
            Layer.SHOES,
            Layer.BOTTOM,
            Layer.TOP,
            Layer.DRESS,
            Layer.OUTERWEAR,
            Layer.BLUSH,
            Layer.MOUTH,
            Layer.FACE,
            Layer.EYES_IRIS,
            Layer.EYES,
            Layer.EYES_LINE,
            Layer.EYEBROWS,
            Layer.GLASSES,
            Layer.EARS,
            Layer.HAIR_FRONT,
            Layer.HAT,
            Layer.ACCESSORY
        )

        for (i in 0 until expectedOrder.size - 1) {
            val current = expectedOrder[i]
            val next = expectedOrder[i + 1]
            assertTrue(
                "Expected ${current.name} (${current.zIndex}) < ${next.name} (${next.zIndex})",
                current.zIndex < next.zIndex
            )
        }
    }

    @Test
    fun getRenderLayers_ordersEquippedLayersStrictlyByZIndex() {
        val hairItem = CatalogItem(
            id = "hair_pigtails",
            category = "hair",
            layer = "hair_front",
            file = "items/hair_front.png",
            thumbnail = "items/thumbnails/thumb_hair.png",
            secondaryLayer = "hair_back",
            secondaryFile = "items/hair_back.png"
        )
        val bodyItem = CatalogItem(
            id = "body_01",
            category = "body",
            layer = "body",
            file = "items/body.png",
            thumbnail = "items/thumbnails/thumb_body.png"
        )
        val topItem = CatalogItem(
            id = "top_hoodie",
            category = "top",
            layer = "top",
            file = "items/top.png",
            thumbnail = "items/thumbnails/thumb_top.png"
        )
        val shoesItem = CatalogItem(
            id = "shoes_sneakers",
            category = "shoes",
            layer = "shoes",
            file = "items/shoes.png",
            thumbnail = "items/thumbnails/thumb_shoes.png"
        )
        val hatItem = CatalogItem(
            id = "hat_beret",
            category = "hat",
            layer = "hat",
            file = "items/hat.png",
            thumbnail = "items/thumbnails/thumb_hat.png"
        )
        val accItem = CatalogItem(
            id = "acc_glasses",
            category = "accessory",
            layer = "accessory",
            file = "items/acc.png",
            thumbnail = "items/thumbnails/thumb_acc.png"
        )

        val look = CharacterLook(
            equippedItems = mapOf(
                Layer.HAIR_FRONT.name to hairItem,
                Layer.BODY.name to bodyItem,
                Layer.TOP.name to topItem,
                Layer.SHOES.name to shoesItem,
                Layer.HAT.name to hatItem,
                Layer.ACCESSORY.name to accItem
            )
        )

        val renderLayers = look.getRenderLayers()

        // Total 7 layers rendered (hair produces both hair_back and hair_front)
        assertEquals(7, renderLayers.size)

        // Verify layer sequence
        assertEquals(Layer.HAIR_BACK, renderLayers[0].first)
        assertEquals(Layer.BODY, renderLayers[1].first)
        assertEquals(Layer.SHOES, renderLayers[2].first)
        assertEquals(Layer.TOP, renderLayers[3].first)
        assertEquals(Layer.HAIR_FRONT, renderLayers[4].first)
        assertEquals(Layer.HAT, renderLayers[5].first)
        assertEquals(Layer.ACCESSORY, renderLayers[6].first)

        // Verify strictly non-decreasing zIndex
        for (i in 0 until renderLayers.size - 1) {
            val currZ = renderLayers[i].first.zIndex
            val nextZ = renderLayers[i + 1].first.zIndex
            assertTrue(
                "Layer zIndex must be strictly monotonic: $currZ <= $nextZ",
                currZ <= nextZ
            )
        }
    }
}

