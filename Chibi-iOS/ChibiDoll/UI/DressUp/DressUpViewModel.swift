import Foundation
import SwiftUI
import Observation

/// ViewModel managing the Dress-up studio state replicating Android `DressUpViewModel.kt`.
@Observable
public final class DressUpViewModel: @unchecked Sendable {
    public var isLoading: Bool = true
    public var catalog: [CatalogItem] = []
    public var palettes: [Palette] = []
    public var categoryGroups: [CategoryGroup] = CategoryGroup.defaultGroups()
    public var selectedGroup: CategoryGroup
    public var selectedSubcategory: CategorySubcategory
    public var currentLook: CharacterLook = CharacterLook()
    public var editingLookId: Int64? = nil

    public var isPaletteOpen: Bool = false
    public var activeColorableLayer: Layer? = nil
    public var activePalette: Palette? = nil

    public var isBackgroundPickerVisible: Bool = false
    public var isResetDialogVisible: Bool = false

    // Composited cached images
    public var baseCompositeImage: UIImage? = nil
    public var overlayCompositeImage: UIImage? = nil

    // Sparkle effect
    public var sparkleTrigger: Int = 0

    private let catalogService: CatalogService
    private let equipItemUseCase: EquipItemUseCase
    private let randomizeLookUseCase: RandomizeLookUseCase
    private let compositor: DollCanvasCompositor
    private let imageCache: ImageCache

    public init(
        catalogService: CatalogService = .shared,
        equipItemUseCase: EquipItemUseCase = EquipItemUseCase(),
        randomizeLookUseCase: RandomizeLookUseCase = RandomizeLookUseCase(),
        compositor: DollCanvasCompositor = .shared,
        imageCache: ImageCache = .shared
    ) {
        self.catalogService = catalogService
        self.equipItemUseCase = equipItemUseCase
        self.randomizeLookUseCase = randomizeLookUseCase
        self.compositor = compositor
        self.imageCache = imageCache

        let groups = CategoryGroup.defaultGroups()
        let initialGroup = groups.first { $0.id == "clothes" } ?? groups.first!
        let initialSubcat = initialGroup.subcategories.first!
        self.selectedGroup = initialGroup
        self.selectedSubcategory = initialSubcat

        loadInitialData()
    }

    public func loadInitialData() {
        isLoading = true
        Task { @MainActor in
            do {
                self.catalog = try self.catalogService.getCatalog()
                self.palettes = try self.catalogService.getPalettes()
                let defaultLook = try self.catalogService.getDefaultLook()
                self.currentLook = defaultLook
                self.isLoading = false
                self.refreshComposites()
            } catch {
                self.isLoading = false
            }
        }
    }

    public func loadPreset(_ preset: CharacterPreset) {
        Task { @MainActor in
            do {
                let look = try self.catalogService.getLookForPreset(preset)
                let clothesGroup = self.categoryGroups.first { $0.id == "clothes" } ?? self.categoryGroups.first!
                self.currentLook = look
                self.editingLookId = nil
                self.selectedGroup = clothesGroup
                self.selectedSubcategory = clothesGroup.subcategories.first!
                self.isPaletteOpen = false
                self.activeColorableLayer = nil
                self.activePalette = nil
                self.refreshComposites()
            } catch {
                // Handle fallback gracefully
            }
        }
    }

    public func selectCategoryGroup(_ group: CategoryGroup) {
        self.selectedGroup = group
        if let first = group.subcategories.first {
            self.selectedSubcategory = first
        }
    }

    public func selectSubcategory(_ subcat: CategorySubcategory) {
        if subcat.id == "background" {
            isBackgroundPickerVisible = true
            return
        }
        self.selectedSubcategory = subcat
    }

    public func selectItem(_ item: CatalogItem) {
        let updatedLook = equipItemUseCase(currentLook, item)
        currentLook = updatedLook
        sparkleTrigger += 1

        let isColorable = item.colorable
        let targetPalette = isColorable ? (palettes.first { $0.id == item.palette } ?? palettes.first { $0.id == "general" }) : nil

        if isColorable {
            activeColorableLayer = item.primaryLayer
            activePalette = targetPalette ?? activePalette
            isPaletteOpen = true
        }

        refreshComposites()
    }

    public func togglePalette() {
        let willBeOpen = !isPaletteOpen
        let pal = (willBeOpen && activePalette == nil) ? (palettes.first { $0.id == "general" } ?? palettes.first) : activePalette
        isPaletteOpen = willBeOpen
        activePalette = pal
    }

    public func closePalette() {
        isPaletteOpen = false
    }

    public func selectColorSwatch(_ swatch: ColorSwatch) {
        let layerToColor = activeColorableLayer
            ?? currentLook.equippedItems.values.first {
                $0.subcategory.caseInsensitiveCompare(selectedSubcategory.id) == .orderedSame && $0.colorable
            }?.primaryLayer
            ?? .dress

        currentLook = currentLook.withLayerColor(layer: layerToColor, color: swatch.colorValue)
        refreshComposites()
    }

    public func selectBackground(_ background: CatalogItem?) {
        currentLook = equipItemUseCase.selectBackground(currentLook: currentLook, background: background)
        refreshComposites()
    }

    public func randomizeOutfit() {
        currentLook = randomizeLookUseCase(catalog: catalog, baseLook: currentLook)
        sparkleTrigger += 1
        refreshComposites()
    }

    public func showResetAlert() {
        isResetDialogVisible = true
    }

    public func dismissResetAlert() {
        isResetDialogVisible = false
    }

    public func confirmResetOutfit() {
        Task { @MainActor in
            let presets = self.catalogService.getPresetCharacters()
            let preset = self.currentLook.presetId.flatMap { pid in presets.first { $0.id == pid } }
                ?? presets.first!
            if let resetLook = try? self.catalogService.getLookForPreset(preset) {
                self.currentLook = resetLook
                self.isResetDialogVisible = false
                self.isPaletteOpen = false
                self.refreshComposites()
            }
        }
    }

    /// Re-renders the cached base and overlay static bitmaps.
    public func refreshComposites() {
        let look = self.currentLook
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            guard let self = self else { return }
            let base = self.compositor.compositeBaseLayers(look: look, imageCache: self.imageCache)
            let overlay = self.compositor.compositeOverlayLayers(look: look, imageCache: self.imageCache)
            DispatchQueue.main.async {
                self.baseCompositeImage = base
                self.overlayCompositeImage = overlay
            }
        }
    }

    /// Returns filtered items matching the currently selected subcategory.
    public var currentSubcategoryItems: [CatalogItem] {
        catalog.filter { item in
            item.subcategory.caseInsensitiveCompare(selectedSubcategory.id) == .orderedSame ||
            item.category.caseInsensitiveCompare(selectedSubcategory.id) == .orderedSame
        }
    }
}

