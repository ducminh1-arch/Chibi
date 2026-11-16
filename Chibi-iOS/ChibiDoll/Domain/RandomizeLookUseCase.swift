import Foundation

/// Generates a randomized outfit while respecting doll layering rules and preserving preset fixed features.
/// Exactly mirrors Android `RandomizeLookUseCase.kt`.
public struct RandomizeLookUseCase: Sendable {
    public init() {}

    public func execute(catalog: [CatalogItem], baseLook: CharacterLook? = nil) -> CharacterLook {
        var equipped: [String: CatalogItem] = [:]
        let layerColors = baseLook?.layerColors ?? [:]

        func pickOne(category: ItemCategory) -> CatalogItem? {
            let list = catalog.filter { $0.categoryEnum == category && !$0.locked }
            return list.randomElement()
        }

        // If baseLook has presetId or fixed body/face/hair, preserve them!
        if baseLook?.presetId != nil, let base = baseLook {
            if let body = base.equippedItems[Layer.body.name] ?? base.equippedItems[Layer.body.key] {
                equipped[Layer.body.name] = body
            }
            if let hair = base.equippedItems[Layer.hairFront.name] ?? base.equippedItems[Layer.hairFront.key] {
                equipped[Layer.hairFront.name] = hair
            }
            if let eyes = base.equippedItems[Layer.eyes.name] ?? base.equippedItems[Layer.eyes.key] {
                equipped[Layer.eyes.name] = eyes
            }
            if let eyesLine = base.equippedItems[Layer.eyesLine.name] ?? base.equippedItems[Layer.eyesLine.key] {
                equipped[Layer.eyesLine.name] = eyesLine
            }
            if let eyesIris = base.equippedItems[Layer.eyesIris.name] ?? base.equippedItems[Layer.eyesIris.key] {
                equipped[Layer.eyesIris.name] = eyesIris
            }
            if let eyebrows = base.equippedItems[Layer.eyebrows.name] ?? base.equippedItems[Layer.eyebrows.key] {
                equipped[Layer.eyebrows.name] = eyebrows
            }
            if let mouth = base.equippedItems[Layer.mouth.name] ?? base.equippedItems[Layer.mouth.key] {
                equipped[Layer.mouth.name] = mouth
            }
            if let blush = base.equippedItems[Layer.blush.name] ?? base.equippedItems[Layer.blush.key] {
                equipped[Layer.blush.name] = blush
            }
            if let face = base.equippedItems[Layer.face.name] ?? base.equippedItems[Layer.face.key] {
                equipped[Layer.face.name] = face
            }
        } else {
            // Body base
            if let body = catalog.first(where: { $0.primaryLayer == .body }) {
                equipped[Layer.body.name] = body
            }
            // Hair
            if let hair = pickOne(category: .hair) { equipped[Layer.hairFront.name] = hair }
            // Eyes
            if let eyes = pickOne(category: .eyes) { equipped[Layer.eyes.name] = eyes }
            // Eyebrows
            if let eyebrows = pickOne(category: .eyebrows) { equipped[Layer.eyebrows.name] = eyebrows }
            // Mouth
            if let mouth = pickOne(category: .mouth) { equipped[Layer.mouth.name] = mouth }
            // Blush (optional 50%)
            if Bool.random(), let blush = pickOne(category: .blush) {
                equipped[Layer.blush.name] = blush
            }
        }

        // Outfit: 50% Dress or 50% Top + Bottom
        if Bool.random() {
            if let dress = pickOne(category: .dress) { equipped[Layer.dress.name] = dress }
        } else {
            if let top = pickOne(category: .top) { equipped[Layer.top.name] = top }
            if let bottom = pickOne(category: .bottom) { equipped[Layer.bottom.name] = bottom }
        }

        // Outerwear (optional 40% chance)
        if Float.random(in: 0...1) < 0.4, let outerwear = pickOne(category: .outerwear) {
            equipped[Layer.outerwear.name] = outerwear
        }

        // Socks (optional 50% chance)
        if Float.random(in: 0...1) < 0.5, let socks = pickOne(category: .socks) {
            equipped[Layer.socks.name] = socks
        }

        // Shoes
        if let shoes = pickOne(category: .shoes) {
            equipped[Layer.shoes.name] = shoes
        }

        // Hat (optional 50% chance)
        if Float.random(in: 0...1) < 0.5, let hat = pickOne(category: .hat) {
            equipped[Layer.hat.name] = hat
        }

        // Glasses (optional 40% chance)
        if Float.random(in: 0...1) < 0.4, let glasses = pickOne(category: .glasses) {
            equipped[Layer.glasses.name] = glasses
        }

        // Ears (optional 30% chance)
        if Float.random(in: 0...1) < 0.3, let ears = pickOne(category: .ears) {
            equipped[Layer.ears.name] = ears
        }

        // Tail (optional 30% chance)
        if Float.random(in: 0...1) < 0.3, let tail = pickOne(category: .tail) {
            equipped[Layer.tail.name] = tail
        }

        // Wings (optional 30% chance)
        if Float.random(in: 0...1) < 0.3, let wings = pickOne(category: .wings) {
            equipped[Layer.wings.name] = wings
        }

        // Accessory (optional 40% chance)
        if Float.random(in: 0...1) < 0.4, let accessory = pickOne(category: .accessory) {
            equipped[Layer.accessory.name] = accessory
        }

        // Background
        let randomBg = pickOne(category: .background)

        return CharacterLook(
            equippedItems: equipped,
            backgroundItem: randomBg,
            layerColors: layerColors,
            presetId: baseLook?.presetId
        )
    }

    public func callAsFunction(catalog: [CatalogItem], baseLook: CharacterLook? = nil) -> CharacterLook {
        execute(catalog: catalog, baseLook: baseLook)
    }
}

