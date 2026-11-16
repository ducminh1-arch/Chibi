import Foundation

/// Rich metadata for rendering an individual layer in strict z-order.
/// 1:1 match with Android `RenderLayerInfo`.
public struct RenderLayerInfo: Identifiable, Sendable {
    public var id: String { "\(layer.key)_\(zIndex)_\(filePath)" }
    public let layer: Layer
    public let filePath: String
    public let altFilePath: String?
    public let colorTint: UInt64?
    public let isColorable: Bool
    public let catalogItem: CatalogItem?
    public let zIndex: Int

    public init(
        layer: Layer,
        filePath: String,
        altFilePath: String? = nil,
        colorTint: UInt64? = nil,
        isColorable: Bool = false,
        catalogItem: CatalogItem? = nil,
        zIndex: Int? = nil
    ) {
        self.layer = layer
        self.filePath = filePath
        self.altFilePath = altFilePath
        self.colorTint = colorTint
        self.isColorable = isColorable
        self.catalogItem = catalogItem
        self.zIndex = zIndex ?? layer.zIndex
    }
}

