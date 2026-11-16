import Foundation

/// Repository and loader service for catalog items, color palettes, and character presets.
/// Exactly mirrors Android `CatalogRepository` and `CatalogRepositoryImpl`.
public final class CatalogService: @unchecked Sendable {
    public static let shared = CatalogService()

    private let bundle: Bundle
    private var cachedCatalog: [CatalogItem]?
    private var cachedPalettes: [Palette]?
    private let lock = NSLock()

    public init(bundle: Bundle = .main) {
        self.bundle = bundle
    }

    /// Loads and decodes catalog items from `catalog.json`.
    public func getCatalog() throws -> [CatalogItem] {
        lock.lock()
        defer { lock.unlock() }

        if let cached = cachedCatalog {
            return cached
        }

        guard let url = bundle.url(forResource: "catalog", withExtension: "json") ??
                        Bundle.main.url(forResource: "catalog", withExtension: "json") ??
                        Bundle(for: CatalogService.self).url(forResource: "catalog", withExtension: "json") else {
            throw NSError(domain: "CatalogService", code: 404, userInfo: [NSLocalizedDescriptionKey: "catalog.json not found in bundle"])
        }

        let data = try Data(contentsOf: url)
        let decoder = JSONDecoder()
        let items = try decoder.decode([CatalogItem].self, from: data)
        self.cachedCatalog = items
        return items
    }

    /// Loads and decodes palettes from `palettes.json`.
    public func getPalettes() throws -> [Palette] {
        lock.lock()
        defer { lock.unlock() }

        if let cached = cachedPalettes {
            return cached
        }

        guard let url = bundle.url(forResource: "palettes", withExtension: "json") ??
                        Bundle.main.url(forResource: "palettes", withExtension: "json") ??
                        Bundle(for: CatalogService.self).url(forResource: "palettes", withExtension: "json") else {
            return []
        }

        let data = try Data(contentsOf: url)
        let decoder = JSONDecoder()
        let container = try decoder.decode(PalettesContainer.self, from: data)
        self.cachedPalettes = container.palettes
        return container.palettes
    }

    /// Returns list of available default character presets.
    public func getPresetCharacters() -> [CharacterPreset] {
        CharacterPreset.defaultPresets()
    }

    /// Builds a full `CharacterLook` from a given preset, matching Android `CatalogRepositoryImpl.getLookForPreset`.
    public func getLookForPreset(_ preset: CharacterPreset) throws -> CharacterLook {
        let all = try getCatalog()
        let itemsById = Dictionary(uniqueKeysWithValues: all.map { ($0.id, $0) })
        var equipped: [String: CatalogItem] = [:]

        // Fixed base layers
        if let body = itemsById[preset.bodyItemId] {
            equipped[Layer.body.name] = body
        }
        if let hair = itemsById[preset.hairItemId] {
            equipped[Layer.hairFront.name] = hair
        }

        // Eyes: line & iris split, with fallback to legacy composite eyes
        let eyesLine = itemsById[preset.eyesLineItemId]
        let eyesIris = itemsById[preset.eyesIrisItemId]
        if let eyesLine = eyesLine, let eyesIris = eyesIris {
            equipped[Layer.eyesLine.name] = eyesLine
            equipped[Layer.eyesIris.name] = eyesIris
        } else if let eyes = itemsById[preset.eyesItemId] {
            equipped[eyes.primaryLayer.name] = eyes
        }

        if let eyebrows = itemsById[preset.eyebrowsItemId] {
            equipped[Layer.eyebrows.name] = eyebrows
        }
        if let mouth = itemsById[preset.mouthItemId] {
            equipped[Layer.mouth.name] = mouth
        }
        if let blushId = preset.blushItemId, let blush = itemsById[blushId] {
            equipped[Layer.blush.name] = blush
        }

        // Legacy fallback
        if equipped[Layer.eyes.name] == nil &&
           equipped[Layer.eyesLine.name] == nil &&
           equipped[Layer.eyesIris.name] == nil {
            if let face = itemsById[preset.faceItemId] {
                equipped[Layer.face.name] = face
            }
        }

        // Default clothes
        for clothId in preset.defaultClothesItemIds {
            if let cloth = itemsById[clothId] {
                equipped[cloth.primaryLayer.name] = cloth
            }
        }

        // Default background
        let bgItem = preset.defaultBackgroundId.flatMap { itemsById[$0] }
            ?? all.first { $0.primaryLayer == .background }

        return CharacterLook(
            equippedItems: equipped,
            backgroundItem: bgItem,
            presetId: preset.id
        )
    }

    /// Returns default look based on the first preset (Luna).
    public func getDefaultLook() throws -> CharacterLook {
        guard let defaultPreset = getPresetCharacters().first else {
            return CharacterLook()
        }
        return try getLookForPreset(defaultPreset)
    }

    /// Filters items by category string.
    public func getItemsByCategory(_ category: String) throws -> [CatalogItem] {
        let all = try getCatalog()
        return all.filter { $0.category.caseInsensitiveCompare(category) == .orderedSame }
    }

    /// Filters items by `ItemCategory`.
    public func getItemsByCategory(_ category: ItemCategory) throws -> [CatalogItem] {
        let all = try getCatalog()
        return all.filter { $0.categoryEnum == category }
    }
}

