package com.chibidoll.dressup.ui.dressup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chibidoll.dressup.data.repository.CatalogRepository
import com.chibidoll.dressup.domain.model.CatalogItem
import com.chibidoll.dressup.domain.model.CategoryGroup
import com.chibidoll.dressup.domain.model.CategorySubcategory
import com.chibidoll.dressup.domain.model.CharacterLook
import com.chibidoll.dressup.domain.model.CharacterPreset
import com.chibidoll.dressup.domain.model.ColorPalette
import com.chibidoll.dressup.domain.model.Layer
import com.chibidoll.dressup.domain.usecase.EquipItemUseCase
import com.chibidoll.dressup.domain.usecase.RandomizeLookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DressUpViewModel(
    private val catalogRepository: CatalogRepository,
    private val equipItemUseCase: EquipItemUseCase = EquipItemUseCase(),
    private val randomizeLookUseCase: RandomizeLookUseCase = RandomizeLookUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DressUpUiState())
    val uiState: StateFlow<DressUpUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val catalog = catalogRepository.getCatalog()
            val palettes = catalogRepository.getPalettes()
            val defaultLook = catalogRepository.getDefaultLook()
            val defaultGroups = CategoryGroup.defaultGroups()
            val initialGroup = defaultGroups.first()
            val initialSubcat = initialGroup.subcategories.first()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    catalog = catalog,
                    palettes = palettes,
                    categoryGroups = defaultGroups,
                    selectedGroup = initialGroup,
                    selectedSubcategory = initialSubcat,
                    currentLook = defaultLook,
                    editingLookId = null
                )
            }
        }
    }

    /**
     * Loads a selected character preset (Luna, Sakura, Sunny, Mimi) and switches to Dress-up mode.
     */
    fun loadPreset(preset: CharacterPreset) {
        viewModelScope.launch {
            val look = catalogRepository.getLookForPreset(preset)
            _uiState.update { state ->
                val clothesGroup = state.categoryGroups.find { it.id == "clothes" } ?: state.categoryGroups.first()
                state.copy(
                    currentLook = look,
                    editingLookId = null,
                    selectedGroup = clothesGroup,
                    selectedSubcategory = clothesGroup.subcategories.first(),
                    isPaletteOpen = false,
                    activeColorableLayer = null,
                    activePalette = null
                )
            }
        }
    }

    fun onCategoryGroupSelected(group: CategoryGroup) {
        _uiState.update { state ->
            val firstSubcat = group.subcategories.firstOrNull() ?: state.selectedSubcategory
            state.copy(
                selectedGroup = group,
                selectedSubcategory = firstSubcat
            )
        }
    }

    fun onSubcategorySelected(subcategory: CategorySubcategory) {
        if (subcategory.id == "background") {
            openBackgroundPicker()
            return
        }
        _uiState.update { state ->
            state.copy(selectedSubcategory = subcategory)
        }
    }

    fun onItemClicked(item: CatalogItem) {
        _uiState.update { state ->
            val updatedLook = equipItemUseCase(state.currentLook, item)
            val isColorable = item.colorable
            val targetPalette = if (isColorable) {
                state.palettes.find { it.id == item.palette }
                    ?: state.palettes.find { it.id == "general" }
            } else null

            state.copy(
                currentLook = updatedLook,
                activeColorableLayer = if (isColorable) item.primaryLayer else state.activeColorableLayer,
                activePalette = targetPalette ?: state.activePalette,
                isPaletteOpen = if (isColorable) true else state.isPaletteOpen
            )
        }
    }

    fun togglePalette() {
        _uiState.update { state ->
            val willBeOpen = !state.isPaletteOpen
            val activePalette = if (willBeOpen && state.activePalette == null) {
                state.palettes.find { it.id == "general" } ?: state.palettes.firstOrNull()
            } else state.activePalette

            state.copy(
                isPaletteOpen = willBeOpen,
                activePalette = activePalette
            )
        }
    }

    fun closePalette() {
        _uiState.update { it.copy(isPaletteOpen = false) }
    }

    fun onColorSwatchSelected(color: Long) {
        _uiState.update { state ->
            val layerToColor = state.activeColorableLayer
                ?: state.currentLook.equippedItems.values.find {
                    it.subcategory.equals(state.selectedSubcategory.id, ignoreCase = true) && it.colorable
                }?.primaryLayer
                ?: Layer.DRESS

            val updatedLook = state.currentLook.withLayerColor(layerToColor, color)
            state.copy(currentLook = updatedLook)
        }
    }

    fun onBackgroundSelected(background: CatalogItem?) {
        _uiState.update { state ->
            val updatedLook = equipItemUseCase.selectBackground(state.currentLook, background)
            state.copy(currentLook = updatedLook)
        }
    }

    fun openBackgroundPicker() {
        _uiState.update { it.copy(isBackgroundPickerVisible = true) }
    }

    fun closeBackgroundPicker() {
        _uiState.update { it.copy(isBackgroundPickerVisible = false) }
    }

    fun onRandomizeClicked() {
        _uiState.update { state ->
            val randomLook = randomizeLookUseCase(state.catalog, baseLook = state.currentLook)
            state.copy(currentLook = randomLook)
        }
    }

    fun showResetConfirmation() {
        _uiState.update { it.copy(isResetDialogVisible = true) }
    }

    fun dismissResetConfirmation() {
        _uiState.update { it.copy(isResetDialogVisible = false) }
    }

    fun confirmResetOutfit() {
        viewModelScope.launch {
            val presetId = _uiState.value.currentLook.presetId
            val defaultPreset = if (presetId != null) {
                catalogRepository.getPresetCharacters().find { it.id == presetId }
                    ?: catalogRepository.getPresetCharacters().first()
            } else {
                catalogRepository.getPresetCharacters().first()
            }
            val resetLook = catalogRepository.getLookForPreset(defaultPreset)
            _uiState.update {
                it.copy(
                    currentLook = resetLook,
                    isResetDialogVisible = false,
                    isPaletteOpen = false
                )
            }
        }
    }

    /**
     * Loads a saved look from My Collection back into the DressUp screen for editing.
     */
    fun loadSavedLook(look: CharacterLook, lookId: Long) {
        _uiState.update {
            it.copy(
                currentLook = look,
                editingLookId = lookId,
                isPaletteOpen = false
            )
        }
    }

    /**
     * Updates the active look ID after saving, ensuring subsequent saves are idempotent.
     */
    fun updateSavedLookId(lookId: Long) {
        _uiState.update {
            it.copy(editingLookId = lookId)
        }
    }

    /**
     * Starts a fresh new look session with default character.
     */
    fun startNewLook() {
        viewModelScope.launch {
            val defaultLook = catalogRepository.getDefaultLook()
            _uiState.update {
                it.copy(
                    currentLook = defaultLook,
                    editingLookId = null,
                    isPaletteOpen = false
                )
            }
        }
    }
}
