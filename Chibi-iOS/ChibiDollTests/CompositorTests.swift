import XCTest
import UIKit
@testable import ChibiDoll

final class CompositorTests: XCTestCase {

    func testCompositorPartitioning_excludesEyeLayers() {
        let body = CatalogItem(id: "body", category: "body", layer: "body", file: "items/body.png", thumbnail: "")
        let dress = CatalogItem(id: "dress", category: "dress", layer: "dress", file: "items/dress.png", thumbnail: "")
        let eyesLine = CatalogItem(id: "eyes_line", category: "eyes", layer: "eyes_line", file: "items/eyes_line.png", thumbnail: "")
        let eyesIris = CatalogItem(id: "eyes_iris", category: "eyes", layer: "eyes_iris", file: "items/eyes_iris.png", thumbnail: "")
        let hat = CatalogItem(id: "hat", category: "hat", layer: "hat", file: "items/hat.png", thumbnail: "")

        let look = CharacterLook(equippedItems: [
            Layer.body.name: body,
            Layer.dress.name: dress,
            Layer.eyesLine.name: eyesLine,
            Layer.eyesIris.name: eyesIris,
            Layer.hat.name: hat
        ])

        let detailed = look.getDetailedRenderLayers()
        let baseLayers = detailed.filter { $0.zIndex < 79 }
        let eyeLayers = detailed.filter { $0.zIndex >= 79 && $0.zIndex <= 82 }
        let overlayLayers = detailed.filter { $0.zIndex > 82 }

        // Base must include body (20) and dress (60)
        XCTAssertEqual(2, baseLayers.count)
        XCTAssertEqual(Layer.body, baseLayers[0].layer)
        XCTAssertEqual(Layer.dress, baseLayers[1].layer)

        // Dynamic eyes must include eyesIris (79) and eyesLine (82)
        XCTAssertEqual(2, eyeLayers.count)
        XCTAssertEqual(Layer.eyesIris, eyeLayers[0].layer)
        XCTAssertEqual(Layer.eyesLine, eyeLayers[1].layer)

        // Overlay must include hat (100)
        XCTAssertEqual(1, overlayLayers.count)
        XCTAssertEqual(Layer.hat, overlayLayers[0].layer)
    }

    func testAdaptiveResolutionDimensions() {
        XCTAssertEqual(640, DollCanvasCompositor.lowRamCanvasWidth)
        XCTAssertEqual(960, DollCanvasCompositor.lowRamCanvasHeight)
        XCTAssertEqual(1024, DollCanvasCompositor.highResCanvasWidth)
        XCTAssertEqual(1536, DollCanvasCompositor.highResCanvasHeight)

        let dims = DollCanvasCompositor.maxCanvasDimensions()
        XCTAssertTrue(dims.width == 640 || dims.width == 1024)
        XCTAssertTrue(dims.height == 960 || dims.height == 1536)
    }

    func testTintImageAppliesMultiplication() {
        let size = CGSize(width: 4, height: 4)
        let renderer = UIGraphicsImageRenderer(size: size)
        let whiteImage = renderer.image { ctx in
            UIColor.white.setFill()
            ctx.fill(CGRect(origin: .zero, size: size))
        }

        // Tint with 0xFFFF0000 (pure red)
        let tinted = DollCanvasCompositor.tintImage(whiteImage, tintColor: 0xFFFF0000)
        XCTAssertEqual(size, tinted.size)
    }
}

