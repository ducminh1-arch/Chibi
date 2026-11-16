import Foundation

/// Handles equipping and unequipping items adhering strictly to paper-doll layering rules.
/// Exactly replicates Android `EquipItemUseCase.kt`.
public struct EquipItemUseCase: Sendable {
    public init() {}

    /// Toggles or equips an item:
    /// - Tapping an already equipped item unequips it (except essential base layers: body, eyes, mouth).
    /// - Equipping dress removes top and bottom.
    /// - Equipping top or bottom removes dress.
    /// - Equipping discrete face features removes legacy face item.
    /// - Equipping background toggles background.
    /// - Resolves bidirectional `conflictsWith` rules.
    @discardableResult
    public func execute(currentLook: CharacterLook, item: CatalogItem) -> CharacterLook {
        // Handle background selection
        if item.categoryEnum == .background || item.primaryLayer == .background {
            let newBg = (currentLook.backgroundItem?.id == item.id) ? nil : item
            var copy = currentLook
            copy.backgroundItem = newBg
            return copy
        }

        let slotKey = item.primaryLayer.name
        let isAlreadyEquipped = currentLook.equippedItems.values.contains { $0.id == item.id }
        var newEquipped = currentLook.equippedItems

        if isAlreadyEquipped {
            // Cannot unequip essential features: body, eyes, mouth
            if item.primaryLayer == .body ||
                item.primaryLayer == .eyes ||
                item.primaryLayer == .eyesLine ||
                item.primaryLayer == .eyesIris ||
                item.primaryLayer == .mouth {
                return currentLook
            }
            // Tap again to unequip
            newEquipped = newEquipped.filter { key, val in
                val.id != item.id && key != slotKey && key != item.primaryLayer.key
            }
        } else {
            // Equipping discrete face part removes legacy full-face
            if item.primaryLayer == .eyes ||
                item.primaryLayer == .eyesLine ||
                item.primaryLayer == .eyesIris ||
                item.primaryLayer == .eyebrows ||
                item.primaryLayer == .mouth ||
                item.primaryLayer == .blush {
                removeLayer(&newEquipped, layer: .face)
            }

            // Equipping legacy full-face removes discrete face parts
            if item.primaryLayer == .face {
                removeLayer(&newEquipped, layer: .eyes)
                removeLayer(&newEquipped, layer: .eyesLine)
                removeLayer(&newEquipped, layer: .eyesIris)
                removeLayer(&newEquipped, layer: .eyebrows)
                removeLayer(&newEquipped, layer: .mouth)
                removeLayer(&newEquipped, layer: .blush)
            }

            // Equipping item: enforce mutual exclusion rules
            let isDress = (item.categoryEnum == .dress || item.primaryLayer == .dress)
            let isTop = (item.categoryEnum == .top || item.primaryLayer == .top)
            let isBottom = (item.categoryEnum == .bottom || item.primaryLayer == .bottom)

            if isDress {
                removeLayer(&newEquipped, layer: .top)
                removeLayer(&newEquipped, layer: .bottom)
            } else if isTop || isBottom {
                removeLayer(&newEquipped, layer: .dress)
            }

            // Conflict resolution: check item.conflictsWith
            if !item.conflictsWith.isEmpty {
                newEquipped = newEquipped.filter { _, eq in
                    !item.conflictsWith.contains { conflict in
                        conflict.caseInsensitiveCompare(eq.primaryLayer.name) == .orderedSame ||
                        conflict.caseInsensitiveCompare(eq.primaryLayer.key) == .orderedSame ||
                        conflict.caseInsensitiveCompare(eq.category) == .orderedSame ||
                        conflict.caseInsensitiveCompare(eq.id) == .orderedSame
                    }
                }
            }

            // Check if any existing equipped item conflicts with incoming item
            newEquipped = newEquipped.filter { _, eq in
                !eq.conflictsWith.contains { conflict in
                    conflict.caseInsensitiveCompare(item.primaryLayer.name) == .orderedSame ||
                    conflict.caseInsensitiveCompare(item.primaryLayer.key) == .orderedSame ||
                    conflict.caseInsensitiveCompare(item.category) == .orderedSame ||
                    conflict.caseInsensitiveCompare(item.id) == .orderedSame
                }
            }

            // Equip item in its layer slot
            newEquipped[slotKey] = item
        }

        var updatedLook = currentLook
        updatedLook.equippedItems = newEquipped
        return updatedLook
    }

    /// Callable syntax sugar matching Kotlin invoke operator: `equipItemUseCase(look, item)`
    public func callAsFunction(_ currentLook: CharacterLook, _ item: CatalogItem) -> CharacterLook {
        execute(currentLook: currentLook, item: item)
    }

    /// Explicitly sets or clears the background item.
    /// Passing nil sets "No Background".
    public func selectBackground(currentLook: CharacterLook, background: CatalogItem?) -> CharacterLook {
        var copy = currentLook
        copy.backgroundItem = background
        return copy
    }

    private func removeLayer(_ dict: inout [String: CatalogItem], layer: Layer) {
        dict.removeValue(forKey: layer.name)
        dict.removeValue(forKey: layer.key)
        dict = dict.filter { _, item in item.primaryLayer != layer }
    }
}

