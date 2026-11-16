import XCTest
import UIKit
@testable import ChibiDoll

final class MissingItemResilienceTests: XCTestCase {

    func testDollImageExporter_skipsMissingAssetBitmapsWithoutCrashing() {
        let ghostItem = CatalogItem(
            id: "ghost_dress",
            category: "dress",
            layer: "dress",
            file: "items/does_not_exist.png",
            thumbnail: ""
        )
        let validItem = CatalogItem(
            id: "valid_shoes",
            category: "shoes",
            layer: "shoes",
            file: "items/valid_shoes.png",
            thumbnail: ""
        )

        let lookWithMissingAsset = CharacterLook(
            equippedItems: [
                Layer.dress.name: ghostItem,
                Layer.shoes.name: validItem
            ],
            backgroundItem: CatalogItem(
                id: "ghost_bg",
                category: "background",
                layer: "background",
                file: "items/bg_missing.png",
                thumbnail: ""
            )
        )

        var renderedFiles: [String] = []

        // Mock loader where "items/valid_shoes.png" returns a 1x1 image, others return nil
        let masterImage = DollImageExporter.shared.renderMasterImageWithLoader(look: lookWithMissingAsset) { path in
            renderedFiles.append(path)
            if path == "items/valid_shoes.png" {
                let renderer = UIGraphicsImageRenderer(size: CGSize(width: 1, height: 1))
                return renderer.image { ctx in
                    UIColor.red.setFill()
                    ctx.fill(CGRect(x: 0, y: 0, width: 1, height: 1))
                }
            }
            return nil
        }

        XCTAssertNotNil(masterImage, "Master image must render without throwing")
        XCTAssertEqual(CGSize(width: 1024, height: 1536), masterImage.size)
        XCTAssertTrue(renderedFiles.contains("items/does_not_exist.png"))
        XCTAssertTrue(renderedFiles.contains("items/valid_shoes.png"))
    }

    func testCharacterLook_emptyLookReturnsCleanLayers() {
        let emptyLook = CharacterLook()
        XCTAssertTrue(emptyLook.getRenderLayers().isEmpty)
        XCTAssertTrue(emptyLook.getDetailedRenderLayers().isEmpty)
    }
}

