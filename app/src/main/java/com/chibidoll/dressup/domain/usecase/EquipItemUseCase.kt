package com.chibidoll.dressup.domain.usecase

import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.ItemCategory
import com.chibidoll.dressup.domain.model.Layer

class EquipItemUseCase {

    /**
     * Toggles or equips an item according to paper-doll layering rules:
     * - Tap an item that is already equipped -> unequip it (except body base).
     * - Equipping a dress removes top and bottom.
     * - Equipping a top removes dress.
     * - Equipping a bottom removes dress.
     * - Equipping hair updates hair layers (hair_front and hair_back).
     * - Equipping a background sets or toggles backgroundItem.
     */
    operator fun invoke(currentLook: CharacterLook, item: CatalogItem): CharacterLook {
        // Handle background selection
        if (item.categoryEnum == ItemCategory.BACKGROUND || item.primaryLayer == Layer.BACKGROUND) {
            val newBg = if (currentLook.backgroundItem?.id == item.id) null else item
            return currentLook.copy(backgroundItem = newBg)
        }

        val slotKey = item.primaryLayer.name
        val isAlreadyEquipped = currentLook.equippedItems.values.any { it.id == item.id }

        val newEquipped = currentLook.equippedItems.toMutableMap()

        if (isAlreadyEquipped) {
            // Cannot unequip essential features: body, eyes, mouth (doll must always have them)
            if (item.primaryLayer == Layer.BODY ||
                item.primaryLayer == Layer.EYES ||
                item.primaryLayer == Layer.EYES_LINE ||
                item.primaryLayer == Layer.EYES_IRIS ||
                item.primaryLayer == Layer.MOUTH
            ) {
                return currentLook
            }
            // Tap again to unequip
            newEquipped.remove(slotKey)
        } else {
            // Equipping discrete face part removes legacy full-face
            if (item.primaryLayer == Layer.EYES ||
                item.primaryLayer == Layer.EYES_LINE ||
                item.primaryLayer == Layer.EYES_IRIS ||
                item.primaryLayer == Layer.EYEBROWS ||
                item.primaryLayer == Layer.MOUTH ||
                item.primaryLayer == Layer.BLUSH
            ) {
                newEquipped.remove(Layer.FACE.name)
            }
            // Equipping legacy full face removes discrete face parts
            if (item.primaryLayer == Layer.FACE) {
                newEquipped.remove(Layer.EYES.name)
                newEquipped.remove(Layer.EYES_LINE.name)
                newEquipped.remove(Layer.EYES_IRIS.name)
                newEquipped.remove(Layer.EYEBROWS.name)
                newEquipped.remove(Layer.MOUTH.name)
                newEquipped.remove(Layer.BLUSH.name)
            }

            // Equipping item: enforce mutual exclusion rules
            val isDress = item.categoryEnum == ItemCategory.DRESS || item.primaryLayer == Layer.DRESS
            val isTop = item.categoryEnum == ItemCategory.TOP || item.primaryLayer == Layer.TOP
            val isBottom = item.categoryEnum == ItemCategory.BOTTOM || item.primaryLayer == Layer.BOTTOM

            when {
                isDress -> {
                    // Equipping dress removes top and bottom
                    newEquipped.remove(Layer.TOP.name)
                    newEquipped.remove(Layer.BOTTOM.name)
                }
                isTop || isBottom -> {
                    // Equipping top or bottom removes dress
                    newEquipped.remove(Layer.DRESS.name)
                }
            }

            // Conflict resolution: check item.conflictsWith
            if (item.conflictsWith.isNotEmpty()) {
                val toRemove = newEquipped.filter { (_, eq) ->
                    item.conflictsWith.any { conflict ->
                        conflict.equals(eq.primaryLayer.name, ignoreCase = true) ||
                        conflict.equals(eq.primaryLayer.key, ignoreCase = true) ||
                        conflict.equals(eq.category, ignoreCase = true) ||
                        conflict.equals(eq.id, ignoreCase = true)
                    }
                }.keys
                toRemove.forEach { newEquipped.remove(it) }
            }

            // Check if any existing equipped item conflicts with incoming item
            val incomingConflictEquipped = newEquipped.filter { (_, eq) ->
                eq.conflictsWith.any { conflict ->
                    conflict.equals(item.primaryLayer.name, ignoreCase = true) ||
                    conflict.equals(item.primaryLayer.key, ignoreCase = true) ||
                    conflict.equals(item.category, ignoreCase = true) ||
                    conflict.equals(item.id, ignoreCase = true)
                }
            }.keys
            incomingConflictEquipped.forEach { newEquipped.remove(it) }

            // Equip item in its layer slot
            newEquipped[slotKey] = item
        }

        return currentLook.copy(equippedItems = newEquipped)
    }

    /**
     * Explicitly sets or clears background.
     * Passing null sets "No Background".
     */
    fun selectBackground(currentLook: CharacterLook, background: CatalogItem?): CharacterLook {
        return currentLook.copy(backgroundItem = background)
    }
}
