package com.chibidoll.dressup.data.repository

import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.CharacterPreset
import com.chibidoll.dressup.domain.model.ColorPalette

interface CatalogRepository {
    suspend fun getCatalog(): List<CatalogItem>
    suspend fun getItemsByCategory(category: String): List<CatalogItem>
    suspend fun getDefaultLook(): CharacterLook
    suspend fun getPalettes(): List<ColorPalette>
    suspend fun getPresetCharacters(): List<CharacterPreset>
    suspend fun getLookForPreset(preset: CharacterPreset): CharacterLook
}
