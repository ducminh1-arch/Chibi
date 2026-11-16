import XCTest
@testable import ChibiDoll

final class CharacterSelectTests: XCTestCase {

    func testPresets_allHaveValidProperties() {
        let presets = CharacterPreset.defaultPresets()
        XCTAssertEqual(4, presets.count)

        for preset in presets {
            XCTAssertFalse(preset.id.isEmpty)
            XCTAssertFalse(preset.nameEn.isEmpty)
            XCTAssertFalse(preset.nameVi.isEmpty)
            XCTAssertFalse(preset.descriptionEn.isEmpty)
            XCTAssertFalse(preset.descriptionVi.isEmpty)
            XCTAssertFalse(preset.bodyItemId.isEmpty)
            XCTAssertFalse(preset.hairItemId.isEmpty)
            XCTAssertFalse(preset.eyesItemId.isEmpty)
            XCTAssertFalse(preset.eyebrowsItemId.isEmpty)
            XCTAssertFalse(preset.mouthItemId.isEmpty)
        }
    }

    func testPresetLookConstruction_preservesPresetId() {
        let preset = CharacterPreset.defaultPresets().first!
        let mockCatalog = [
            CatalogItem(id: preset.bodyItemId, category: "body", layer: "body", file: "items/body.png", thumbnail: ""),
            CatalogItem(id: preset.hairItemId, category: "hair", layer: "hair_front", file: "items/hair.png", thumbnail: ""),
            CatalogItem(id: preset.eyesItemId, category: "eyes", layer: "eyes", file: "items/eyes.png", thumbnail: "")
        ]

        let look = CharacterLook(
            equippedItems: [
                Layer.body.name: mockCatalog[0],
                Layer.hairFront.name: mockCatalog[1],
                Layer.eyes.name: mockCatalog[2]
            ],
            presetId: preset.id
        )

        XCTAssertEqual("preset_luna", look.presetId)
        XCTAssertTrue(look.isEquipped(mockCatalog[0]))
        XCTAssertTrue(look.isEquipped(mockCatalog[1]))
    }
}

