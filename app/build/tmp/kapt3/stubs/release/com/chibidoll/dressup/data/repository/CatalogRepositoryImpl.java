package com.chibidoll.dressup.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0096@\u00a2\u0006\u0002\u0010\rJ\u000e\u0010\u000e\u001a\u00020\u000fH\u0096@\u00a2\u0006\u0002\u0010\rJ\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0011\u001a\u00020\u0012H\u0096@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0016H\u0096@\u00a2\u0006\u0002\u0010\u0017J\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000b0\bH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00160\bH\u0096@\u00a2\u0006\u0002\u0010\rR\u0016\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/chibidoll/dressup/data/repository/CatalogRepositoryImpl;", "Lcom/chibidoll/dressup/data/repository/CatalogRepository;", "context", "Landroid/content/Context;", "json", "Lkotlinx/serialization/json/Json;", "(Landroid/content/Context;Lkotlinx/serialization/json/Json;)V", "cachedCatalog", "", "Lcom/chibidoll/dressup/domain/model/CatalogItem;", "cachedPalettes", "Lcom/chibidoll/dressup/domain/model/ColorPalette;", "getCatalog", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDefaultLook", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "getItemsByCategory", "category", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLookForPreset", "preset", "Lcom/chibidoll/dressup/domain/model/CharacterPreset;", "(Lcom/chibidoll/dressup/domain/model/CharacterPreset;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPalettes", "getPresetCharacters", "app_release"})
public final class CatalogRepositoryImpl implements com.chibidoll.dressup.data.repository.CatalogRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.serialization.json.Json json = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile java.util.List<com.chibidoll.dressup.domain.model.CatalogItem> cachedCatalog;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private volatile java.util.List<com.chibidoll.dressup.domain.model.ColorPalette> cachedPalettes;
    
    public CatalogRepositoryImpl(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlinx.serialization.json.Json json) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getCatalog(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.chibidoll.dressup.domain.model.CatalogItem>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getPalettes(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.chibidoll.dressup.domain.model.ColorPalette>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getPresetCharacters(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.chibidoll.dressup.domain.model.CharacterPreset>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getLookForPreset(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterPreset preset, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.chibidoll.dressup.domain.model.CharacterLook> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getItemsByCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.chibidoll.dressup.domain.model.CatalogItem>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getDefaultLook(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.chibidoll.dressup.domain.model.CharacterLook> $completion) {
        return null;
    }
}