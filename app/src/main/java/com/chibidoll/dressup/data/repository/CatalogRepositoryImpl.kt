package com.chibidoll.dressup.data.repository

import android.content.Context
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.CharacterPreset
import com.chibidoll.dressup.domain.model.ColorPalette
import com.chibidoll.dressup.domain.model.Layer
import com.chibidoll.dressup.domain.model.PaletteContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CatalogRepositoryImpl(
    private val context: Context,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : CatalogRepository {

    @Volatile
    private var cachedCatalog: List<CatalogItem>? = null

    @Volatile
    private var cachedPalettes: List<ColorPalette>? = null

    override suspend fun getCatalog(): List<CatalogItem> = withContext(Dispatchers.IO) {
        cachedCatalog ?: run {
            val jsonString = context.assets.open("catalog.json").bufferedReader().use { it.readText() }
            val items: List<CatalogItem> = json.decodeFromString(jsonString)
            cachedCatalog = items
            items
        }
    }

    override suspend fun getPalettes(): List<ColorPalette> = withContext(Dispatchers.IO) {
        cachedPalettes ?: run {
            try {
                val jsonString = context.assets.open("palettes.json").bufferedReader().use { it.readText() }
                val container: PaletteContainer = json.decodeFromString(jsonString)
                cachedPalettes = container.palettes
                container.palettes
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    override suspend fun getPresetCharacters(): List<CharacterPreset> {
        return CharacterPreset.defaultPresets()
    }

    override suspend fun getLookForPreset(preset: CharacterPreset): CharacterLook {
        val all = getCatalog()
        val itemsById = all.associateBy { it.id }
        val equipped = mutableMapOf<String, CatalogItem>()

        // Fixed base layers
        itemsById[preset.bodyItemId]?.let { equipped[Layer.BODY.name] = it }
        itemsById[preset.hairItemId]?.let { equipped[Layer.HAIR_FRONT.name] = it }

        // Eyes: line & iris split, with fallback to legacy composite eyes
        val eyesLine = itemsById[preset.eyesLineItemId]
        val eyesIris = itemsById[preset.eyesIrisItemId]
        if (eyesLine != null && eyesIris != null) {
            equipped[Layer.EYES_LINE.name] = eyesLine
            equipped[Layer.EYES_IRIS.name] = eyesIris
        } else {
            itemsById[preset.eyesItemId]?.let { equipped[it.primaryLayer.name] = it }
        }

        itemsById[preset.eyebrowsItemId]?.let { equipped[Layer.EYEBROWS.name] = it }
        itemsById[preset.mouthItemId]?.let { equipped[Layer.MOUTH.name] = it }
        preset.blushItemId?.let { blushId ->
            itemsById[blushId]?.let { equipped[Layer.BLUSH.name] = it }
        }

        // Legacy fallback
        if (!equipped.containsKey(Layer.EYES.name) &&
            !equipped.containsKey(Layer.EYES_LINE.name) &&
            !equipped.containsKey(Layer.EYES_IRIS.name)
        ) {
            itemsById[preset.faceItemId]?.let { equipped[Layer.FACE.name] = it }
        }

        // Default clothes
        preset.defaultClothesItemIds.forEach { clothId ->
            itemsById[clothId]?.let { clothItem ->
                equipped[clothItem.primaryLayer.name] = clothItem
            }
        }

        // Default background
        val bgItem = preset.defaultBackgroundId?.let { itemsById[it] }
            ?: all.firstOrNull { it.primaryLayer == Layer.BACKGROUND }

        return CharacterLook(
            equippedItems = equipped,
            backgroundItem = bgItem,
            presetId = preset.id
        )
    }

    override suspend fun getItemsByCategory(category: String): List<CatalogItem> {
        val all = getCatalog()
        return all.filter { it.category.equals(category, ignoreCase = true) }
    }

    override suspend fun getDefaultLook(): CharacterLook {
        val defaultPreset = getPresetCharacters().first()
        return getLookForPreset(defaultPreset)
    }
}
