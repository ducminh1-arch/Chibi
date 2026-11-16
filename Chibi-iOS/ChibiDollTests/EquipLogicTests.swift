import XCTest
@testable import ChibiDoll

final class EquipLogicTests: XCTestCase {

    private var equipItemUseCase: EquipItemUseCase!

    private let bodyItem = CatalogItem(
        id: "body_fair",
        category: "body",
        layer: "body",
        file: "items/body/body_fair.png",
        thumbnail: "items/thumbnails/thumb_body_fair.png",
        nameEn: "Fair Body",
        nameVi: "Cơ thể hồng"
    )

    private val topItem = CatalogItem(
        id: "top_sailor",
        category: "top",
        layer: "top",
        file: "items/top/top_sailor.png",
        thumbnail: "items/thumbnails/thumb_top_sailor.png",
        nameEn: "Sailor Blouse",
        nameVi: "Áo thủy thủ"
    )

    private val bottomItem = CatalogItem(
        id: "bot_navy_skirt",
        category: "bottom",
        layer: "bottom",
        file: "items/bottom/bottom_navy_skirt.png",
        thumbnail: "items/thumbnails/thumb_bot_navy.png",
        nameEn: "Navy Skirt",
        nameVi: "Váy xanh"
    )

    private let dressItem = CatalogItem(
        id: "dress_princess",
        category: "dress",
        layer: "dress",
        file: "items/dress/dress_princess_pink.png",
        thumbnail: "items/thumbnails/thumb_dress_pink.png",
        nameEn: "Princess Gown",
        nameVi: "Váy công chúa"
    )

    private let hairItem = CatalogItem(
        id: "hair_lilac_twin",
        category: "hair",
        layer: "hair_front",
        thumbnail: "items/thumbnails/thumb_hair_lilac.png",
        nameEn: "Lilac Twin",
        nameVi: "Tóc tím",
        secondaryLayer: "hair_back",
        secondaryFile: "items/hair/hair_lilac_back.png",
        file: "items/hair/hair_lilac_front.png"
    )

    private let hatItem = CatalogItem(
        id: "hat_bow",
        category: "hat",
        layer: "hat",
        file: "items/hat/hat_giant_bow.png",
        thumbnail: "items/thumbnails/thumb_hat_bow.png",
        nameEn: "Giant Bow",
        nameVi: "Nơ to"
    )

    private let bgItem1 = CatalogItem(
        id: "bg_dream",
        category: "background",
        layer: "background",
        file: "items/backgrounds/bg_pastel_dream.png",
        thumbnail: "items/thumbnails/thumb_bg_dream.png",
        nameEn: "Pastel Dream",
        nameVi: "Mộng mơ"
    )

    private let bgItem2 = CatalogItem(
        id: "bg_stars",
        category: "background",
        layer: "background",
        file: "items/backgrounds/bg_starry_twilight.png",
        thumbnail: "items/thumbnails/thumb_bg_stars.png",
        nameEn: "Starry Twilight",
        nameVi: "Đêm sao"
    )

    override func setUp() {
        super.setUp()
        equipItemUseCase = EquipItemUseCase()
    }

    func testEquippingDressRemovesTopAndBottom() {
        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem,
                Layer.top.name: topItem,
                Layer.bottom.name: bottomItem
            ]
        )
        XCTAssertTrue(look.isEquipped(topItem))
        XCTAssertTrue(look.isEquipped(bottomItem))

        // Equip dress
        look = equipItemUseCase(look, dressItem)

        XCTAssertTrue(look.isEquipped(dressItem), "Dress should be equipped")
        XCTAssertFalse(look.isEquipped(topItem), "Top should be removed when dress is equipped")
        XCTAssertFalse(look.isEquipped(bottomItem), "Bottom should be removed when dress is equipped")
        XCTAssertTrue(look.isEquipped(bodyItem), "Body should remain")
    }

    func testEquippingTopRemovesDress() {
        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem,
                Layer.dress.name: dressItem
            ]
        )
        XCTAssertTrue(look.isEquipped(dressItem))

        // Equip top
        look = equipItemUseCase(look, topItem)

        XCTAssertTrue(look.isEquipped(topItem), "Top should be equipped")
        XCTAssertFalse(look.isEquipped(dressItem), "Dress should be removed when top is equipped")
    }

    func testEquippingBottomRemovesDress() {
        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem,
                Layer.dress.name: dressItem
            ]
        )
        XCTAssertTrue(look.isEquipped(dressItem))

        // Equip bottom
        look = equipItemUseCase(look, bottomItem)

        XCTAssertTrue(look.isEquipped(bottomItem), "Bottom should be equipped")
        XCTAssertFalse(look.isEquipped(dressItem), "Dress should be removed when bottom is equipped")
    }

    func testTapEquippedItemUnequipsIt() {
        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem,
                Layer.hat.name: hatItem
            ]
        )
        XCTAssertTrue(look.isEquipped(hatItem))

        // Tap hat again to unequip
        look = equipItemUseCase(look, hatItem)

        XCTAssertFalse(look.isEquipped(hatItem), "Hat should be unequipped")
    }

    func testCannotUnequipBodyBase() {
        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem
            ]
        )
        look = equipItemUseCase(look, bodyItem)
        XCTAssertTrue(look.isEquipped(bodyItem), "Body cannot be unequipped")
    }

    func testHairEquipsBothFrontAndBackInRenderLayers() {
        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem
            ]
        )
        look = equipItemUseCase(look, hairItem)

        let renderLayers = look.getRenderLayers()
        let layerEnums = renderLayers.map { $0.0 }

        XCTAssertTrue(layerEnums.contains(.hairBack))
        XCTAssertTrue(layerEnums.contains(.hairFront))
    }

    func testRenderLayersAreStrictlyOrderedByZIndex() {
        let look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem,
                Layer.hairFront.name: hairItem,
                Layer.hat.name: hatItem,
                Layer.top.name: topItem
            ]
        )

        let renderLayers = look.getRenderLayers()
        let zIndices = renderLayers.map { $0.0.zIndex }

        XCTAssertEqual(zIndices.sorted(), zIndices)
        XCTAssertEqual(Layer.hairBack, renderLayers.first?.0) // zIndex 10
        XCTAssertEqual(Layer.hat, renderLayers.last?.0) // zIndex 100
    }

    func testBackgroundToggleWorks() {
        var look = CharacterLook(backgroundItem: nil)

        // Equip background
        look = equipItemUseCase(look, bgItem1)
        XCTAssertEqual("bg_dream", look.backgroundItem?.id)
        XCTAssertTrue(look.isEquipped(bgItem1))

        // Tap again to unequip
        look = equipItemUseCase(look, bgItem1)
        XCTAssertNil(look.backgroundItem)
        XCTAssertFalse(look.isEquipped(bgItem1))
    }

    func testSelectBackgroundExplicitlySetsBackground() {
        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem,
                Layer.top.name: topItem
            ],
            backgroundItem: nil
        )

        // Select background 1
        look = equipItemUseCase.selectBackground(currentLook: look, background: bgItem1)
        XCTAssertEqual("bg_dream", look.backgroundItem?.id)
        XCTAssertTrue(look.isEquipped(bgItem1))
        XCTAssertTrue(look.isEquipped(bodyItem), "Body preserved")
        XCTAssertTrue(look.isEquipped(topItem), "Top preserved")

        // Swap to background 2
        look = equipItemUseCase.selectBackground(currentLook: look, background: bgItem2)
        XCTAssertEqual("bg_stars", look.backgroundItem?.id)
        XCTAssertTrue(look.isEquipped(bgItem2))
        XCTAssertFalse(look.isEquipped(bgItem1))
        XCTAssertTrue(look.isEquipped(topItem), "Clothes still preserved")

        // Select nil for "No Background" option
        look = equipItemUseCase.selectBackground(currentLook: look, background: nil)
        XCTAssertNil(look.backgroundItem, "Background must be nil")
        XCTAssertFalse(look.isEquipped(bgItem1))
        XCTAssertFalse(look.isEquipped(bgItem2))
        XCTAssertTrue(look.isEquipped(topItem), "Clothes still preserved with no background")
    }

    func testSocksAndShoes_stackWithoutConflict() {
        let socksItem = CatalogItem(
            id: "socks_knee",
            category: "socks",
            layer: "socks",
            file: "items/socks/socks_white_knee.png",
            thumbnail: "thumb_socks.png"
        )
        let shoesItem = CatalogItem(
            id: "shoes_mary_jane",
            category: "shoes",
            layer: "shoes",
            file: "items/shoes/shoes_mary_jane.png",
            thumbnail: "thumb_shoes.png"
        )

        var look = CharacterLook(equippedItems: [Layer.body.name: bodyItem])

        // Equip socks
        look = equipItemUseCase(look, socksItem)
        XCTAssertTrue(look.isEquipped(socksItem))

        // Equip shoes
        look = equipItemUseCase(look, shoesItem)
        XCTAssertTrue(look.isEquipped(socksItem), "Socks must remain equipped when shoes are equipped")
        XCTAssertTrue(look.isEquipped(shoesItem), "Shoes must be equipped")
        XCTAssertEqual(3, look.equippedItems.count) // body, socks, shoes
    }

    func testEquippingDiscreteFacePart_removesLegacyWholeFace() {
        let legacyFace = CatalogItem(
            id: "face_legacy",
            category: "face",
            layer: "face",
            file: "items/face/legacy.png",
            thumbnail: "thumb_face.png"
        )
        let eyesItem = CatalogItem(
            id: "eyes_purple",
            category: "eyes",
            layer: "eyes",
            file: "items/eyes/eyes_purple_open.png",
            thumbnail: "thumb_eyes.png"
        )

        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem,
                Layer.face.name: legacyFace
            ]
        )
        XCTAssertTrue(look.isEquipped(legacyFace))

        // Equip discrete eyes
        look = equipItemUseCase(look, eyesItem)
        XCTAssertTrue(look.isEquipped(eyesItem))
        XCTAssertFalse(look.isEquipped(legacyFace), "Legacy face must be removed when discrete face part is equipped")
    }

    func testEssentialFeatures_bodyEyesMouth_cannotBeUnequipped() {
        let eyesItem = CatalogItem(
            id: "eyes_purple",
            category: "eyes",
            layer: "eyes",
            file: "items/eyes/eyes_purple_open.png",
            thumbnail: "thumb_eyes.png"
        )
        let mouthItem = CatalogItem(
            id: "mouth_smile",
            category: "mouth",
            layer: "mouth",
            file: "items/mouth/mouth_smile.png",
            thumbnail: "thumb_mouth.png"
        )

        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem,
                Layer.eyes.name: eyesItem,
                Layer.mouth.name: mouthItem
            ]
        )

        // Tap body -> cannot unequip
        look = equipItemUseCase(look, bodyItem)
        XCTAssertTrue(look.isEquipped(bodyItem))

        // Tap eyes -> cannot unequip
        look = equipItemUseCase(look, eyesItem)
        XCTAssertTrue(look.isEquipped(eyesItem))

        // Tap mouth -> cannot unequip
        look = equipItemUseCase(look, mouthItem)
        XCTAssertTrue(look.isEquipped(mouthItem))
    }

    func testConflictsWithRule_unEquipsConflictingLayers() {
        let catEarsWithHatConflict = CatalogItem(
            id: "ears_cat",
            category: "ears",
            layer: "ears",
            file: "items/ears/ears_cat_white.png",
            thumbnail: "thumb_ears.png",
            conflictsWith: ["hat"]
        )

        var look = CharacterLook(
            equippedItems: [
                Layer.body.name: bodyItem,
                Layer.hat.name: hatItem
            ]
        )
        XCTAssertTrue(look.isEquipped(hatItem))

        // Equip ears with hat conflict -> hat must be unequipped!
        look = equipItemUseCase(look, catEarsWithHatConflict)
        XCTAssertTrue(look.isEquipped(catEarsWithHatConflict))
        XCTAssertFalse(look.isEquipped(hatItem), "Hat must be unequipped due to conflictsWith")

        // Equip hat again -> cat ears must be unequipped!
        look = equipItemUseCase(look, hatItem)
        XCTAssertTrue(look.isEquipped(hatItem))
        XCTAssertFalse(look.isEquipped(catEarsWithHatConflict), "Cat ears must be unequipped when conflicting hat is equipped")
    }
}

