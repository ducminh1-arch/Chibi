import XCTest
@testable import ChibiDoll

final class LayerOrderingTests: XCTestCase {

    func testZOrderEnum_strictlyAscendingMatchesPaperDollSpecification() {
        let expectedOrder: [Layer] = [
            .background,
            .wings,
            .hairBack,
            .tail,
            .body,
            .socks,
            .shoes,
            .bottom,
            .top,
            .dress,
            .outerwear,
            .blush,
            .mouth,
            .face,
            .eyesIris,
            .eyes,
            .eyesLine,
            .eyebrows,
            .glasses,
            .ears,
            .hairFront,
            .hat,
            .accessory
        ]

        for i in 0..<(expectedOrder.count - 1) {
            let current = expectedOrder[i]
            let next = expectedOrder[i + 1]
            XCTAssertTrue(
                current.zIndex < next.zIndex,
                "Expected \(current.name) (\(current.zIndex)) < \(next.name) (\(next.zIndex))"
            )
        }
    }

    func testItemWithZOverride_overridesDefaultLayerZIndex() {
        let normalTail = CatalogItem(
            id: "tail_cat",
            category: "tail",
            layer: "tail",
            file: "items/tail/tail_cat.png",
            thumbnail: "items/thumbnails/thumb_tail_cat.png",
            zOverride: nil
        )

        let frontTail = CatalogItem(
            id: "tail_fox_front",
            category: "tail",
            layer: "tail",
            file: "items/tail/tail_fox_front.png",
            thumbnail: "items/thumbnails/thumb_tail_fox_front.png",
            zOverride: 45
        )

        let skirt = CatalogItem(
            id: "bottom_skirt",
            category: "bottom",
            layer: "bottom",
            file: "items/bottom/bottom_skirt.png",
            thumbnail: "items/thumbnails/thumb_skirt.png"
        )

        let top = CatalogItem(
            id: "top_shirt",
            category: "top",
            layer: "top",
            file: "items/top/top_shirt.png",
            thumbnail: "items/thumbnails/thumb_top.png"
        )

        // Case 1: Normal tail (zIndex 15) sits behind skirt (zIndex 40)
        let lookWithNormalTail = CharacterLook(
            equippedItems: [
                Layer.tail.name: normalTail,
                Layer.bottom.name: skirt,
                Layer.top.name: top
            ]
        )
        let normalOrder = lookWithNormalTail.getDetailedRenderLayers().compactMap { $0.catalogItem?.id }
        XCTAssertEqual(["tail_cat", "bottom_skirt", "top_shirt"], normalOrder)

        // Case 2: Front tail with z_override = 45 sits IN FRONT OF skirt (40) and BEHIND top (50)
        let lookWithFrontTail = CharacterLook(
            equippedItems: [
                Layer.tail.name: frontTail,
                Layer.bottom.name: skirt,
                Layer.top.name: top
            ]
        )
        let frontOrder = lookWithFrontTail.getDetailedRenderLayers().compactMap { $0.catalogItem?.id }
        XCTAssertEqual(
            ["bottom_skirt", "tail_fox_front", "top_shirt"],
            frontOrder,
            "Tail with z_override=45 must render after skirt (40) and before top (50)"
        )
    }

    func testDualLayerHair_ordersBackAndFrontCorrectly() {
        let hair = CatalogItem(
            id: "hair_lilac",
            category: "hair",
            layer: "hair_front",
            file: "items/hair/front.png",
            thumbnail: "thumb.png",
            secondaryLayer: "hair_back",
            secondaryFile: "items/hair/back.png"
        )
        let body = CatalogItem(
            id: "body_fair",
            category: "body",
            layer: "body",
            file: "items/body/body.png",
            thumbnail: "thumb.png"
        )

        let look = CharacterLook(equippedItems: [
            Layer.hairFront.name: hair,
            Layer.body.name: body
        ])

        let layers = look.getDetailedRenderLayers()
        XCTAssertEqual(3, layers.count) // hair_back, body, hair_front
        XCTAssertEqual(Layer.hairBack, layers[0].layer)
        XCTAssertEqual(Layer.body, layers[1].layer)
        XCTAssertEqual(Layer.hairFront, layers[2].layer)
    }
}

