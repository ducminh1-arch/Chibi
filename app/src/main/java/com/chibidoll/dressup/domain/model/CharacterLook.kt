package com.chibidoll.dressup.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CharacterLook(
    val equippedItems: Map<String, CatalogItem> = emptyMap(),
    val backgroundItem: CatalogItem? = null,
    val layerColors: Map<String, Long> = emptyMap(),
    val presetId: String? = null
) {
    /**
     * Returns list of (Layer, relativeFilePath) ordered strictly by Layer.zIndex
     * from bottom to top:
     * hair_back -> body -> shoes -> bottom -> top -> dress -> outerwear -> face -> hair_front -> hat -> accessory.
     */
    fun getRenderLayers(): List<Pair<Layer, String>> {
        val layers = mutableListOf<Triple<Layer, String, Int>>()

        equippedItems.values.forEach { item ->
            val pz = item.zOverride ?: item.primaryLayer.zIndex
            layers.add(Triple(item.primaryLayer, item.file, pz))
            if (item.secondaryLayerEnum != null && !item.secondaryFile.isNullOrBlank()) {
                val sz = item.secondaryZOverride ?: item.secondaryLayerEnum!!.zIndex
                layers.add(Triple(item.secondaryLayerEnum!!, item.secondaryFile, sz))
            }
        }

        return layers.sortedBy { it.third }.map { it.first to it.second }
    }

    /**
     * Returns rich layer render details including file path, altFilePath (for blinking eyes), color tint, colorable flag, and zIndex.
     */
    fun getDetailedRenderLayers(): List<RenderLayerInfo> {
        val list = mutableListOf<RenderLayerInfo>()

        equippedItems.values.forEach { item ->
            val isPrimaryLine = item.primaryLayer == Layer.EYES_LINE
            val primaryTint = if (isPrimaryLine) null else getColorForLayer(item.primaryLayer)
            val primaryColorable = if (isPrimaryLine) false else item.colorable
            val primaryZ = item.zOverride ?: item.primaryLayer.zIndex

            list.add(
                RenderLayerInfo(
                    layer = item.primaryLayer,
                    filePath = item.file,
                    altFilePath = item.altFile,
                    colorTint = primaryTint,
                    isColorable = primaryColorable,
                    catalogItem = item,
                    zIndex = primaryZ
                )
            )

            if (item.secondaryLayerEnum != null && !item.secondaryFile.isNullOrBlank()) {
                val secLayer = item.secondaryLayerEnum!!
                val isSecLine = secLayer == Layer.EYES_LINE
                val secTint = if (isSecLine) null else (getColorForLayer(secLayer) ?: primaryTint)
                val secColorable = if (isSecLine) false else item.colorable
                val secZ = item.secondaryZOverride ?: secLayer.zIndex

                list.add(
                    RenderLayerInfo(
                        layer = secLayer,
                        filePath = item.secondaryFile,
                        altFilePath = item.secondaryAltFile,
                        colorTint = secTint,
                        isColorable = secColorable,
                        catalogItem = item,
                        zIndex = secZ
                    )
                )
            }
        }

        return list.sortedBy { it.zIndex }
    }

    fun getColorForLayer(layer: Layer): Long? {
        if (layer == Layer.EYES_LINE) return null
        if (layer == Layer.EYES_IRIS) {
            return layerColors[Layer.EYES_IRIS.name]
                ?: layerColors[Layer.EYES_IRIS.key]
                ?: layerColors[Layer.EYES.name]
                ?: layerColors[Layer.EYES.key]
        }
        return layerColors[layer.name] ?: layerColors[layer.key]
    }

    fun withLayerColor(layer: Layer, color: Long): CharacterLook {
        val updated = layerColors.toMutableMap()
        updated[layer.name] = color

        // When a layer belongs to a dual-layer item (e.g. hair_front + hair_back),
        // tint both layers identically.
        if (layer == Layer.HAIR_FRONT) {
            updated[Layer.HAIR_BACK.name] = color
        } else if (layer == Layer.HAIR_BACK) {
            updated[Layer.HAIR_FRONT.name] = color
        } else if (layer == Layer.EYES || layer == Layer.EYES_IRIS) {
            updated[Layer.EYES_IRIS.name] = color
            updated[Layer.EYES.name] = color
        }

        equippedItems.values.forEach { item ->
            if (item.primaryLayer == layer && item.secondaryLayerEnum != null) {
                if (item.secondaryLayerEnum != Layer.EYES_LINE) {
                    updated[item.secondaryLayerEnum!!.name] = color
                }
            } else if (item.secondaryLayerEnum == layer) {
                if (item.primaryLayer != Layer.EYES_LINE) {
                    updated[item.primaryLayer.name] = color
                }
            }
        }

        // Always ensure EYES_LINE is never tinted
        updated.remove(Layer.EYES_LINE.name)

        return copy(layerColors = updated)
    }

    fun isEquipped(item: CatalogItem): Boolean {
        return if (item.categoryEnum == ItemCategory.BACKGROUND) {
            backgroundItem?.id == item.id
        } else {
            equippedItems.values.any { it.id == item.id }
        }
    }
}

data class RenderLayerInfo(
    val layer: Layer,
    val filePath: String,
    val altFilePath: String? = null,
    val colorTint: Long? = null,
    val isColorable: Boolean = false,
    val catalogItem: CatalogItem? = null,
    val zIndex: Int = layer.zIndex
)
