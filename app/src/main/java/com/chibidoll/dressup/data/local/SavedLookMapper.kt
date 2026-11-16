package com.chibidoll.dressup.data.local

import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

object SavedLookMapper {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val mapSerializer = MapSerializer(String.serializer(), String.serializer())
    private val colorMapSerializer = MapSerializer(String.serializer(), Long.serializer())

    /**
     * Converts a CharacterLook to a SavedLookEntity including layer colors.
     */
    fun toEntity(
        look: CharacterLook,
        thumbnailPath: String,
        id: Long = 0,
        createdAt: Long = System.currentTimeMillis()
    ): SavedLookEntity {
        val slotToItemId = look.equippedItems.mapValues { it.value.id }
        val jsonString = json.encodeToString(mapSerializer, slotToItemId)
        val colorsJsonString = json.encodeToString(colorMapSerializer, look.layerColors)

        return SavedLookEntity(
            id = id,
            createdAt = createdAt,
            equippedItemIdsJson = jsonString,
            backgroundId = look.backgroundItem?.id,
            thumbnailPath = thumbnailPath,
            layerColorsJson = colorsJsonString,
            presetId = look.presetId
        )
    }

    /**
     * Restores a CharacterLook from a SavedLookEntity and the full catalog items list.
     * Hardened against obsolete/missing items, corrupted JSON, missing colors, and slot-casing variations.
     */
    fun toCharacterLook(
        entity: SavedLookEntity,
        catalogItems: List<CatalogItem>
    ): CharacterLook {
        val itemsById = catalogItems.associateBy { it.id }

        val slotToItemId: Map<String, String> = try {
            json.decodeFromString(mapSerializer, entity.equippedItemIdsJson)
        } catch (_: Exception) {
            emptyMap()
        }

        val restoredEquipped = mutableMapOf<String, CatalogItem>()
        for ((_, itemId) in slotToItemId) {
            itemsById[itemId]?.let { item ->
                // Map using canonical primaryLayer name
                restoredEquipped[item.primaryLayer.name] = item
            }
        }

        val restoredBackground = entity.backgroundId?.let { itemsById[it] }

        val restoredColors: Map<String, Long> = try {
            if (entity.layerColorsJson.isNotBlank()) {
                json.decodeFromString(colorMapSerializer, entity.layerColorsJson)
            } else {
                emptyMap()
            }
        } catch (_: Exception) {
            emptyMap()
        }

        return CharacterLook(
            equippedItems = restoredEquipped,
            backgroundItem = restoredBackground,
            layerColors = restoredColors,
            presetId = entity.presetId
        )
    }
}
