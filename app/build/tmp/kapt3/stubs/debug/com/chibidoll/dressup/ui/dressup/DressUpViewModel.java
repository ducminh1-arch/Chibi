package com.chibidoll.dressup.ui.dressup;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0012\u001a\u00020\u0011J\u0006\u0010\u0013\u001a\u00020\u0011J\u0006\u0010\u0014\u001a\u00020\u0011J\b\u0010\u0015\u001a\u00020\u0011H\u0002J\u000e\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0018J\u0016\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dJ\u0010\u0010\u001e\u001a\u00020\u00112\b\u0010\u001f\u001a\u0004\u0018\u00010 J\u000e\u0010!\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020#J\u000e\u0010$\u001a\u00020\u00112\u0006\u0010%\u001a\u00020\u001dJ\u000e\u0010&\u001a\u00020\u00112\u0006\u0010\'\u001a\u00020 J\u0006\u0010(\u001a\u00020\u0011J\u000e\u0010)\u001a\u00020\u00112\u0006\u0010*\u001a\u00020+J\u0006\u0010,\u001a\u00020\u0011J\u0006\u0010-\u001a\u00020\u0011J\u0006\u0010.\u001a\u00020\u0011J\u0006\u0010/\u001a\u00020\u0011J\u000e\u00100\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\u001dR\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u00061"}, d2 = {"Lcom/chibidoll/dressup/ui/dressup/DressUpViewModel;", "Landroidx/lifecycle/ViewModel;", "catalogRepository", "Lcom/chibidoll/dressup/data/repository/CatalogRepository;", "equipItemUseCase", "Lcom/chibidoll/dressup/domain/usecase/EquipItemUseCase;", "randomizeLookUseCase", "Lcom/chibidoll/dressup/domain/usecase/RandomizeLookUseCase;", "(Lcom/chibidoll/dressup/data/repository/CatalogRepository;Lcom/chibidoll/dressup/domain/usecase/EquipItemUseCase;Lcom/chibidoll/dressup/domain/usecase/RandomizeLookUseCase;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/chibidoll/dressup/ui/dressup/DressUpUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "closeBackgroundPicker", "", "closePalette", "confirmResetOutfit", "dismissResetConfirmation", "loadInitialData", "loadPreset", "preset", "Lcom/chibidoll/dressup/domain/model/CharacterPreset;", "loadSavedLook", "look", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "lookId", "", "onBackgroundSelected", "background", "Lcom/chibidoll/dressup/domain/model/CatalogItem;", "onCategoryGroupSelected", "group", "Lcom/chibidoll/dressup/domain/model/CategoryGroup;", "onColorSwatchSelected", "color", "onItemClicked", "item", "onRandomizeClicked", "onSubcategorySelected", "subcategory", "Lcom/chibidoll/dressup/domain/model/CategorySubcategory;", "openBackgroundPicker", "showResetConfirmation", "startNewLook", "togglePalette", "updateSavedLookId", "app_debug"})
public final class DressUpViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.chibidoll.dressup.data.repository.CatalogRepository catalogRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.chibidoll.dressup.domain.usecase.EquipItemUseCase equipItemUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.chibidoll.dressup.domain.usecase.RandomizeLookUseCase randomizeLookUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.chibidoll.dressup.ui.dressup.DressUpUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.chibidoll.dressup.ui.dressup.DressUpUiState> uiState = null;
    
    public DressUpViewModel(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.data.repository.CatalogRepository catalogRepository, @org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.usecase.EquipItemUseCase equipItemUseCase, @org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.usecase.RandomizeLookUseCase randomizeLookUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.chibidoll.dressup.ui.dressup.DressUpUiState> getUiState() {
        return null;
    }
    
    private final void loadInitialData() {
    }
    
    /**
     * Loads a selected character preset (Luna, Sakura, Sunny, Mimi) and switches to Dress-up mode.
     */
    public final void loadPreset(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterPreset preset) {
    }
    
    public final void onCategoryGroupSelected(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CategoryGroup group) {
    }
    
    public final void onSubcategorySelected(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CategorySubcategory subcategory) {
    }
    
    public final void onItemClicked(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CatalogItem item) {
    }
    
    public final void togglePalette() {
    }
    
    public final void closePalette() {
    }
    
    public final void onColorSwatchSelected(long color) {
    }
    
    public final void onBackgroundSelected(@org.jetbrains.annotations.Nullable()
    com.chibidoll.dressup.domain.model.CatalogItem background) {
    }
    
    public final void openBackgroundPicker() {
    }
    
    public final void closeBackgroundPicker() {
    }
    
    public final void onRandomizeClicked() {
    }
    
    public final void showResetConfirmation() {
    }
    
    public final void dismissResetConfirmation() {
    }
    
    public final void confirmResetOutfit() {
    }
    
    /**
     * Loads a saved look from My Collection back into the DressUp screen for editing.
     */
    public final void loadSavedLook(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterLook look, long lookId) {
    }
    
    /**
     * Updates the active look ID after saving, ensuring subsequent saves are idempotent.
     */
    public final void updateSavedLookId(long lookId) {
    }
    
    /**
     * Starts a fresh new look session with default character.
     */
    public final void startNewLook() {
    }
}