package com.chibidoll.dressup.data.local;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010J*\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u00062\b\b\u0002\u0010\u0015\u001a\u00020\u00072\b\b\u0002\u0010\u0016\u001a\u00020\u0007R \u0010\u0003\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/chibidoll/dressup/data/local/SavedLookMapper;", "", "()V", "colorMapSerializer", "Lkotlinx/serialization/KSerializer;", "", "", "", "json", "Lkotlinx/serialization/json/Json;", "mapSerializer", "toCharacterLook", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "entity", "Lcom/chibidoll/dressup/data/local/SavedLookEntity;", "catalogItems", "", "Lcom/chibidoll/dressup/domain/model/CatalogItem;", "toEntity", "look", "thumbnailPath", "id", "createdAt", "app_debug"})
public final class SavedLookMapper {
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.serialization.json.Json json = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.serialization.KSerializer<java.util.Map<java.lang.String, java.lang.String>> mapSerializer = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.serialization.KSerializer<java.util.Map<java.lang.String, java.lang.Long>> colorMapSerializer = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.chibidoll.dressup.data.local.SavedLookMapper INSTANCE = null;
    
    private SavedLookMapper() {
        super();
    }
    
    /**
     * Converts a CharacterLook to a SavedLookEntity including layer colors.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.chibidoll.dressup.data.local.SavedLookEntity toEntity(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterLook look, @org.jetbrains.annotations.NotNull()
    java.lang.String thumbnailPath, long id, long createdAt) {
        return null;
    }
    
    /**
     * Restores a CharacterLook from a SavedLookEntity and the full catalog items list.
     * Hardened against obsolete/missing items, corrupted JSON, missing colors, and slot-casing variations.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.chibidoll.dressup.domain.model.CharacterLook toCharacterLook(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.data.local.SavedLookEntity entity, @org.jetbrains.annotations.NotNull()
    java.util.List<com.chibidoll.dressup.domain.model.CatalogItem> catalogItems) {
        return null;
    }
}