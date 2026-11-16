import Foundation

/// State representation of the doll's current equipped items, background, and dye colors.
/// 1:1 match with Android `CharacterLook`.
public struct CharacterLook: Codable, Hashable, Sendable {
    public var equippedItems: [String: CatalogItem]
    public var backgroundItem: CatalogItem?
    public var layerColors: [String: UInt64]
    public var presetId: String?

    public init(
        equippedItems: [String: CatalogItem] = [:],
        backgroundItem: CatalogItem? = nil,
        layerColors: [String: UInt64] = [:],
        presetId: String? = nil
    ) {
        self.equippedItems = equippedItems
        self.backgroundItem = backgroundItem
        self.layerColors = layerColors
        self.presetId = presetId
    }

    /// Returns list of (Layer, relativeFilePath) ordered strictly by z-index ascending.
    public func getRenderLayers() -> [(Layer, String)] {
        var layers: [(Layer, String, Int)] = []

        for item in equippedItems.values {
            let pz = item.zOverride ?? item.primaryLayer.zIndex
            layers.append((item.primaryLayer, item.file, pz))
            if let secLayer = item.secondaryLayerEnum, let secFile = item.secondaryFile, !secFile.isEmpty {
                let sz = item.secondaryZOverride ?? secLayer.zIndex
                layers.append((secLayer, secFile, sz))
            }
        }

        return layers.sorted { $0.2 < $1.2 }.map { ($0.0, $0.1) }
    }

    /// Returns rich layer render details including file path, altFilePath (for blinking eyes),
    /// color tint, colorable flag, and effective zIndex (accounting for z_override).
    public func getDetailedRenderLayers() -> [RenderLayerInfo] {
        var list: [RenderLayerInfo] = []

        for item in equippedItems.values {
            let isPrimaryLine = (item.primaryLayer == .eyesLine)
            let primaryTint = isPrimaryLine ? nil : getColorForLayer(item.primaryLayer)
            let primaryColorable = isPrimaryLine ? false : item.colorable
            let primaryZ = item.zOverride ?? item.primaryLayer.zIndex

            list.append(
                RenderLayerInfo(
                    layer: item.primaryLayer,
                    filePath: item.file,
                    altFilePath: item.altFile,
                    colorTint: primaryTint,
                    isColorable: primaryColorable,
                    catalogItem: item,
                    zIndex: primaryZ
                )
            )

            if let secLayer = item.secondaryLayerEnum, let secFile = item.secondaryFile, !secFile.isEmpty {
                let isSecLine = (secLayer == .eyesLine)
                let secTint = isSecLine ? nil : (getColorForLayer(secLayer) ?? primaryTint)
                let secColorable = isSecLine ? false : item.colorable
                let secZ = item.secondaryZOverride ?? secLayer.zIndex

                list.append(
                    RenderLayerInfo(
                        layer: secLayer,
                        filePath: secFile,
                        altFilePath: item.secondaryAltFile,
                        colorTint: secTint,
                        isColorable: secColorable,
                        catalogItem: item,
                        zIndex: secZ
                    )
                )
            }
        }

        return list.sorted { $0.zIndex < $1.zIndex }
    }

    public func getColorForLayer(_ layer: Layer) -> UInt64? {
        if layer == .eyesLine { return nil }
        if layer == .eyesIris {
            return layerColors[Layer.eyesIris.key]
                ?? layerColors[Layer.eyesIris.key.uppercased()]
                ?? layerColors[Layer.eyes.key]
                ?? layerColors[Layer.eyes.key.uppercased()]
        }
        return layerColors[layer.key] ?? layerColors[layer.key.uppercased()]
    }

    /// Updates the tint color for a layer, synchronizing dual layers (hair front + back, eyes iris).
    public func withLayerColor(layer: Layer, color: UInt64) -> CharacterLook {
        var updated = layerColors
        let key = layer.key
        let upperKey = layer.key.uppercased()

        updated[key] = color
        updated[upperKey] = color

        // Dual-layer hair synchronization
        if layer == .hairFront || layer == .hairBack {
            updated[Layer.hairFront.key] = color
            updated[Layer.hairFront.key.uppercased()] = color
            updated[Layer.hairBack.key] = color
            updated[Layer.hairBack.key.uppercased()] = color
        } else if layer == .eyes || layer == .eyesIris {
            updated[Layer.eyesIris.key] = color
            updated[Layer.eyesIris.key.uppercased()] = color
            updated[Layer.eyes.key] = color
            updated[Layer.eyes.key.uppercased()] = color
        }

        // Secondary layer synchronization across equipped items
        for item in equippedItems.values {
            if item.primaryLayer == layer, let sec = item.secondaryLayerEnum, sec != .eyesLine {
                updated[sec.key] = color
                updated[sec.key.uppercased()] = color
            } else if item.secondaryLayerEnum == layer, item.primaryLayer != .eyesLine {
                updated[item.primaryLayer.key] = color
                updated[item.primaryLayer.key.uppercased()] = color
            }
        }

        // Strictly ensure eyesLine is NEVER tinted
        updated.removeValue(forKey: Layer.eyesLine.key)
        updated.removeValue(forKey: Layer.eyesLine.key.uppercased())

        var copy = self
        copy.layerColors = updated
        return copy
    }

    public func isEquipped(_ item: CatalogItem) -> Bool {
        if item.categoryEnum == .background || item.primaryLayer == .background {
            return backgroundItem?.id == item.id
        }
        return equippedItems.values.contains { $0.id == item.id }
    }
}

