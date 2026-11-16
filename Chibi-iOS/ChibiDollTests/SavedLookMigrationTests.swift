import XCTest
@testable import ChibiDoll

final class SavedLookMigrationTests: XCTestCase {

    private let sampleCatalog: [CatalogItem] = [
        CatalogItem(
            id: "body_fair",
            category: "body",
            layer: "body",
            file: "items/body/body_fair.png",
            thumbnail: "thumb_body.png"
        ),
        CatalogItem(
            id: "dress_mint",
            category: "dress",
            layer: "dress",
            file: "items/dress/mint.png",
            thumbnail: "thumb_dress.png"
        ),
        CatalogItem(
            id: "hair_lilac",
            category: "hair",
            layer: "hair_front",
            file: "items/hair/lilac.png",
            thumbnail: "thumb_hair.png"
        ),
        CatalogItem(
            id: "bg_sakura",
            category: "background",
            layer: "background",
            file: "items/bg/sakura.png",
            thumbnail: "thumb_bg.png"
        )
    ]

    func testSavedLookMapper_fullRoundTrip() {
        let originalLook = CharacterLook(
            equippedItems: [
                Layer.body.name: sampleCatalog[0],
                Layer.dress.name: sampleCatalog[1],
                Layer.hairFront.name: sampleCatalog[2]
            ],
            backgroundItem: sampleCatalog[3],
            layerColors: ["hair_front": 0xFFFF00FF, "HAIR_FRONT": 0xFFFF00FF],
            presetId: "preset_luna"
        )

        let entity = SavedLookMapper.toEntity(
            look: originalLook,
            thumbnailFileName: "thumb_123.png",
            id: 123
        )

        XCTAssertEqual(123, entity.id)
        XCTAssertEqual("thumb_123.png", entity.thumbnailFileName)
        XCTAssertEqual("bg_sakura", entity.backgroundId)
        XCTAssertEqual("preset_luna", entity.presetId)

        let restoredLook = SavedLookMapper.toCharacterLook(
            entity: entity,
            catalogItems: sampleCatalog
        )

        XCTAssertEqual(originalLook.presetId, restoredLook.presetId)
        XCTAssertEqual(originalLook.backgroundItem?.id, restoredLook.backgroundItem?.id)
        XCTAssertEqual(3, restoredLook.equippedItems.count)
        XCTAssertEqual("body_fair", restoredLook.equippedItems[Layer.body.name]?.id)
        XCTAssertEqual("dress_mint", restoredLook.equippedItems[Layer.dress.name]?.id)
        XCTAssertEqual("hair_lilac", restoredLook.equippedItems[Layer.hairFront.name]?.id)
        XCTAssertEqual(0xFFFF00FF, restoredLook.layerColors["hair_front"])
    }

    func testSavedLookMapper_legacyEntityWithNullPresetAndEmptyColors() {
        // Simulating legacy V1/V2 entity with null presetId and empty color JSON
        let legacyEntity = SavedLookEntity(
            id: 1,
            createdAt: 1000,
            equippedItemIdsJson: "{\"body\":\"body_fair\"}",
            backgroundId: nil,
            thumbnailFileName: "legacy.png",
            layerColorsJson: "{}",
            presetId: nil
        )

        let restored = SavedLookMapper.toCharacterLook(entity: legacyEntity, catalogItems: sampleCatalog)

        XCTAssertNil(restored.presetId)
        XCTAssertNil(restored.backgroundItem)
        XCTAssertTrue(restored.layerColors.isEmpty)
        XCTAssertEqual(1, restored.equippedItems.count)
        XCTAssertEqual("body_fair", restored.equippedItems[Layer.body.name]?.id)
    }

    func testSavedLookMapper_corruptedJsonHandledGracefully() {
        let corrupted = SavedLookEntity(
            id: 2,
            createdAt: 2000,
            equippedItemIdsJson: "NOT_VALID_JSON",
            backgroundId: "non_existent_bg",
            thumbnailFileName: "corrupt.png",
            layerColorsJson: "MALFORMED",
            presetId: "preset_test"
        )

        let restored = SavedLookMapper.toCharacterLook(entity: corrupted, catalogItems: sampleCatalog)

        XCTAssertNotNil(restored)
        XCTAssertTrue(restored.equippedItems.isEmpty)
        XCTAssertNil(restored.backgroundItem)
        XCTAssertTrue(restored.layerColors.isEmpty)
        XCTAssertEqual("preset_test", restored.presetId)
    }
}

