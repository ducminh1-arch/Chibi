package com.chibidoll.dressup.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatalogItem(
    val id: String,
    val category: String,
    val layer: String,
    val file: String,
    val thumbnail: String,
    val nameEn: String = "",
    val nameVi: String = "",
    val locked: Boolean = false,
    val secondaryLayer: String? = null,
    val secondaryFile: String? = null,
    val group: String = "",
    val subcategory: String = "",
    val colorable: Boolean = false,
    val palette: String? = null,
    @SerialName("alt_file")
    val altFile: String? = null,
    @SerialName("secondary_alt_file")
    val secondaryAltFile: String? = null,
    @SerialName("z_override")
    val zOverride: Int? = null,
    @SerialName("secondary_z_override")
    val secondaryZOverride: Int? = null,
    val conflictsWith: List<String> = emptyList()
) {
    val primaryLayer: Layer
        get() = Layer.fromKey(layer)

    val secondaryLayerEnum: Layer?
        get() = secondaryLayer?.let { Layer.fromKey(it) }

    val categoryEnum: ItemCategory
        get() = ItemCategory.fromKey(category)

    val effectiveGroup: String
        get() {
            if (group.isNotBlank()) return group
            return when (category.lowercase()) {
                "top", "bottom", "dress", "outerwear", "shoes", "socks" -> "clothes"
                "hat", "accessory", "glasses", "ears", "tail", "wings" -> "accessories"
                "hair", "face", "body", "eyes", "eyes_line", "eyes_iris", "eyebrows", "mouth", "blush" -> "appearance"
                "background", "backgrounds" -> "scene"
                else -> "clothes"
            }
        }

    val effectiveSubcategory: String
        get() = if (subcategory.isNotBlank()) subcategory else category

    fun getDisplayName(isVietnamese: Boolean = false): String {
        return if (isVietnamese && nameVi.isNotBlank()) nameVi else nameEn
    }
}
