package com.chibidoll.dressup.domain.usecase

import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.ItemCategory
import com.chibidoll.dressup.domain.model.Layer
import kotlin.random.Random

class RandomizeLookUseCase {

    operator fun invoke(
        catalog: List<CatalogItem>,
        baseLook: CharacterLook? = null
    ): CharacterLook {
        val equipped = mutableMapOf<String, CatalogItem>()
        val layerColors = baseLook?.layerColors?.toMutableMap() ?: mutableMapOf()

        fun pickOne(category: ItemCategory): CatalogItem? {
            val list = catalog.filter { it.categoryEnum == category && !it.locked }
            return if (list.isNotEmpty()) list[Random.nextInt(list.size)] else null
        }

        // If baseLook has presetId or fixed body/face/hair, preserve them!
        if (baseLook?.presetId != null) {
            baseLook.equippedItems[Layer.BODY.name]?.let { equipped[Layer.BODY.name] = it }
            baseLook.equippedItems[Layer.HAIR_FRONT.name]?.let { equipped[Layer.HAIR_FRONT.name] = it }
            baseLook.equippedItems[Layer.EYES.name]?.let { equipped[Layer.EYES.name] = it }
            baseLook.equippedItems[Layer.EYEBROWS.name]?.let { equipped[Layer.EYEBROWS.name] = it }
            baseLook.equippedItems[Layer.MOUTH.name]?.let { equipped[Layer.MOUTH.name] = it }
            baseLook.equippedItems[Layer.BLUSH.name]?.let { equipped[Layer.BLUSH.name] = it }
            baseLook.equippedItems[Layer.FACE.name]?.let { equipped[Layer.FACE.name] = it }
        } else {
            // Body base
            catalog.firstOrNull { it.primaryLayer == Layer.BODY }?.let {
                equipped[Layer.BODY.name] = it
            }
            // Hair
            pickOne(ItemCategory.HAIR)?.let { equipped[Layer.HAIR_FRONT.name] = it }
            // Eyes
            pickOne(ItemCategory.EYES)?.let { equipped[Layer.EYES.name] = it }
            // Eyebrows
            pickOne(ItemCategory.EYEBROWS)?.let { equipped[Layer.EYEBROWS.name] = it }
            // Mouth
            pickOne(ItemCategory.MOUTH)?.let { equipped[Layer.MOUTH.name] = it }
            // Blush (optional 50%)
            if (Random.nextBoolean()) {
                pickOne(ItemCategory.BLUSH)?.let { equipped[Layer.BLUSH.name] = it }
            }
        }

        // Outfit: 50% Dress or 50% Top + Bottom
        val chooseDress = Random.nextBoolean()
        if (chooseDress) {
            pickOne(ItemCategory.DRESS)?.let { equipped[Layer.DRESS.name] = it }
        } else {
            pickOne(ItemCategory.TOP)?.let { equipped[Layer.TOP.name] = it }
            pickOne(ItemCategory.BOTTOM)?.let { equipped[Layer.BOTTOM.name] = it }
        }

        // Outerwear (optional 40% chance)
        if (Random.nextFloat() < 0.4f) {
            pickOne(ItemCategory.OUTERWEAR)?.let { equipped[Layer.OUTERWEAR.name] = it }
        }

        // Socks (optional 50% chance)
        if (Random.nextFloat() < 0.5f) {
            pickOne(ItemCategory.SOCKS)?.let { equipped[Layer.SOCKS.name] = it }
        }

        // Shoes
        pickOne(ItemCategory.SHOES)?.let { equipped[Layer.SHOES.name] = it }

        // Hat (optional 50% chance)
        if (Random.nextFloat() < 0.5f) {
            pickOne(ItemCategory.HAT)?.let { equipped[Layer.HAT.name] = it }
        }

        // Glasses (optional 40% chance)
        if (Random.nextFloat() < 0.4f) {
            pickOne(ItemCategory.GLASSES)?.let { equipped[Layer.GLASSES.name] = it }
        }

        // Ears (optional 30% chance)
        if (Random.nextFloat() < 0.3f) {
            pickOne(ItemCategory.EARS)?.let { equipped[Layer.EARS.name] = it }
        }

        // Tail (optional 30% chance)
        if (Random.nextFloat() < 0.3f) {
            pickOne(ItemCategory.TAIL)?.let { equipped[Layer.TAIL.name] = it }
        }

        // Wings (optional 30% chance)
        if (Random.nextFloat() < 0.3f) {
            pickOne(ItemCategory.WINGS)?.let { equipped[Layer.WINGS.name] = it }
        }

        // Accessory (optional 40% chance)
        if (Random.nextFloat() < 0.4f) {
            pickOne(ItemCategory.ACCESSORY)?.let { equipped[Layer.ACCESSORY.name] = it }
        }

        // Background
        val randomBg = pickOne(ItemCategory.BACKGROUND)

        return CharacterLook(
            equippedItems = equipped,
            backgroundItem = randomBg,
            layerColors = layerColors,
            presetId = baseLook?.presetId
        )
    }
}

