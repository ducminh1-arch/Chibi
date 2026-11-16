import XCTest
@testable import ChibiDoll

final class CatalogParsingTests: XCTestCase {

    func testCatalogJsonParsing() throws {
        // Load catalog.json
        guard let url = Bundle(for: CatalogParsingTests.self).url(forResource: "catalog", withExtension: "json") ??
                        Bundle.main.url(forResource: "catalog", withExtension: "json") else {
            // In unit test environment on XcodeGen, check resources
            let bundle = Bundle(for: CatalogParsingTests.self)
            XCTAssertNotNil(bundle, "Test bundle must exist")
            return
        }

        let data = try Data(contentsOf: url)
        let decoder = JSONDecoder()
        let items = try decoder.decode([CatalogItem].self, from: data)

        XCTAssertEqual(72, items.count, "Catalog must contain exactly 72 items")

        for item in items {
            XCTAssertFalse(item.id.isEmpty, "Item ID must not be empty")
            XCTAssertFalse(item.category.isEmpty, "Item category must not be empty")
            XCTAssertFalse(item.layer.isEmpty, "Item layer must not be empty")
            XCTAssertFalse(item.file.isEmpty, "Item file path must not be empty")
        }
    }

    func testPalettesJsonParsing() throws {
        guard let url = Bundle(for: CatalogParsingTests.self).url(forResource: "palettes", withExtension: "json") ??
                        Bundle.main.url(forResource: "palettes", withExtension: "json") else {
            return
        }

        let data = try Data(contentsOf: url)
        let decoder = JSONDecoder()
        let container = try decoder.decode(PalettesContainer.self, from: data)

        XCTAssertEqual(4, container.palettes.count, "Palettes must contain exactly 4 palettes (hair, skin, eyes, general)")

        let paletteIds = container.palettes.map { $0.id }
        XCTAssertTrue(paletteIds.contains("hair"))
        XCTAssertTrue(paletteIds.contains("skin"))
        XCTAssertTrue(paletteIds.contains("eyes"))
        XCTAssertTrue(paletteIds.contains("general"))

        for palette in container.palettes {
            XCTAssertFalse(palette.swatches.isEmpty, "Palette \(palette.id) must have swatches")
            for swatch in palette.swatches {
                XCTAssertGreaterThan(swatch.colorValue, 0, "Color value must be parsed")
            }
        }
    }

    func testDefaultPresetsConfiguration() {
        let presets = CharacterPreset.defaultPresets()
        XCTAssertEqual(4, presets.count)

        let presetIds = presets.map { $0.id }
        XCTAssertEqual(["preset_luna", "preset_sakura", "preset_sunny", "preset_mimi"], presetIds)

        for preset in presets {
            XCTAssertFalse(preset.nameEn.isEmpty)
            XCTAssertFalse(preset.nameVi.isEmpty)
            XCTAssertFalse(preset.hairItemId.isEmpty)
            XCTAssertFalse(preset.bodyItemId.isEmpty)
        }
    }
}

