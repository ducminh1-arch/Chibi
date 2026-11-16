import Foundation

/// Maps between runtime `CharacterLook` state and persistent `SavedLookEntity`.
/// Exactly replicates Android `SavedLookMapper.kt`.
public enum SavedLookMapper {

    public static func toEntity(
        look: CharacterLook,
        thumbnailFileName: String,
        id: Int64 = 0,
        createdAt: Int64 = Int64(Date().timeIntervalSince1970 * 1000)
    ) -> SavedLookEntity {
        let slotToItemId = look.equippedItems.mapValues { $0.id }
        let equippedJson = (try? String(data: JSONEncoder().encode(slotToItemId), encoding: .utf8)) ?? "{}"

        let colorsJson = (try? String(data: JSONEncoder().encode(look.layerColors), encoding: .utf8)) ?? "{}"

        return SavedLookEntity(
            id: id,
            createdAt: createdAt,
            equippedItemIdsJson: equippedJson,
            backgroundId: look.backgroundItem?.id,
            thumbnailFileName: thumbnailFileName,
            layerColorsJson: colorsJson,
            presetId: look.presetId
        )
    }

    public static func toCharacterLook(
        entity: SavedLookEntity,
        catalogItems: [CatalogItem]
    ) -> CharacterLook {
        let itemsById = Dictionary(uniqueKeysWithValues: catalogItems.map { ($0.id, $0) })

        var slotToItemId: [String: String] = [:]
        if let data = entity.equippedItemIdsJson.data(using: .utf8),
           let decoded = try? JSONDecoder().decode([String: String].self, from: data) {
            slotToItemId = decoded
        }

        var restoredEquipped: [String: CatalogItem] = [:]
        for (_, itemId) in slotToItemId {
            if let item = itemsById[itemId] {
                restoredEquipped[item.primaryLayer.name] = item
            }
        }

        let restoredBg = entity.backgroundId.flatMap { itemsById[$0] }

        var restoredColors: [String: UInt64] = [:]
        if let data = entity.layerColorsJson.data(using: .utf8),
           let decoded = try? JSONDecoder().decode([String: UInt64].self, from: data) {
            restoredColors = decoded
        }

        return CharacterLook(
            equippedItems: restoredEquipped,
            backgroundItem: restoredBg,
            layerColors: restoredColors,
            presetId: entity.presetId
        )
    }
}

