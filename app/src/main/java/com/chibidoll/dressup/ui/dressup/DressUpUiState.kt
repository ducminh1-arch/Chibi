package com.chibidoll.dressup.ui.dressup

import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CategoryGroup
import com.chibidoll.dressup.domain.model.CategorySubcategory
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.ColorPalette
import com.chibidoll.dressup.domain.model.ItemCategory
import com.chibidoll.dressup.domain.model.Layer

data class DressUpUiState(
    val isLoading: Boolean = true,
    val catalog: List<CatalogItem> = emptyList(),
    val palettes: List<ColorPalette> = emptyList(),
    val categoryGroups: List<CategoryGroup> = CategoryGroup.defaultGroups(),
    val selectedGroup: CategoryGroup = CategoryGroup.defaultGroups().first(),
    val selectedSubcategory: CategorySubcategory = CategoryGroup.defaultGroups().first().subcategories.first(),
    val currentLook: CharacterLook = CharacterLook(),
    val isPaletteOpen: Boolean = false,
    val activePalette: ColorPalette? = null,
    val activeColorableLayer: Layer? = null,
    val isBackgroundPickerVisible: Boolean = false,
    val isResetDialogVisible: Boolean = false,
    val editingLookId: Long? = null
) {
    /**
     * Items belonging to the selected subcategory (e.g. "dress", "top", "shoes", etc.).
     */
    val itemsInSelectedSubcategory: List<CatalogItem>
        get() = catalog.filter {
            it.subcategory.equals(selectedSubcategory.id, ignoreCase = true) ||
            it.category.equals(selectedSubcategory.id, ignoreCase = true)
        }

    /**
     * All background wallpaper items in catalog.
     */
    val allBackgrounds: List<CatalogItem>
        get() = catalog.filter {
            it.categoryEnum == ItemCategory.BACKGROUND || it.primaryLayer == Layer.BACKGROUND
        }
}
