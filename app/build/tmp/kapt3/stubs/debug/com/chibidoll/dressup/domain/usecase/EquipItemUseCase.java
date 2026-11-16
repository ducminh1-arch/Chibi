package com.chibidoll.dressup.domain.usecase;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0019\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007H\u0086\u0002J\u0018\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\b\u0010\t\u001a\u0004\u0018\u00010\u0007\u00a8\u0006\n"}, d2 = {"Lcom/chibidoll/dressup/domain/usecase/EquipItemUseCase;", "", "()V", "invoke", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "currentLook", "item", "Lcom/chibidoll/dressup/domain/model/CatalogItem;", "selectBackground", "background", "app_debug"})
public final class EquipItemUseCase {
    
    public EquipItemUseCase() {
        super();
    }
    
    /**
     * Toggles or equips an item according to paper-doll layering rules:
     * - Tap an item that is already equipped -> unequip it (except body base).
     * - Equipping a dress removes top and bottom.
     * - Equipping a top removes dress.
     * - Equipping a bottom removes dress.
     * - Equipping hair updates hair layers (hair_front and hair_back).
     * - Equipping a background sets or toggles backgroundItem.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.chibidoll.dressup.domain.model.CharacterLook invoke(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterLook currentLook, @org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CatalogItem item) {
        return null;
    }
    
    /**
     * Explicitly sets or clears background.
     * Passing null sets "No Background".
     */
    @org.jetbrains.annotations.NotNull()
    public final com.chibidoll.dressup.domain.model.CharacterLook selectBackground(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterLook currentLook, @org.jetbrains.annotations.Nullable()
    com.chibidoll.dressup.domain.model.CatalogItem background) {
        return null;
    }
}