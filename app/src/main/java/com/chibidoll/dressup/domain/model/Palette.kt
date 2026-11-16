package com.chibidoll.dressup.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PaletteContainer(
    val palettes: List<ColorPalette> = emptyList()
)

@Serializable
data class ColorPalette(
    val id: String,
    val nameEn: String,
    val nameVi: String,
    val swatches: List<ColorSwatch> = emptyList()
) {
    fun getDisplayName(isVietnamese: Boolean = false): String {
        return if (isVietnamese && nameVi.isNotBlank()) nameVi else nameEn
    }
}

@Serializable
data class ColorSwatch(
    val id: String,
    val nameEn: String,
    val nameVi: String,
    val color: String
) {
    val colorLong: Long
        get() {
            return try {
                val cleaned = color.trim().removePrefix("0x").removePrefix("#")
                val parsed = cleaned.toLong(16)
                if (cleaned.length <= 6) {
                    parsed or 0xFF000000L
                } else {
                    parsed
                }
            } catch (_: Exception) {
                0xFFFF8DAAL
            }
        }

    fun getDisplayName(isVietnamese: Boolean = false): String {
        return if (isVietnamese && nameVi.isNotBlank()) nameVi else nameEn
    }
}

