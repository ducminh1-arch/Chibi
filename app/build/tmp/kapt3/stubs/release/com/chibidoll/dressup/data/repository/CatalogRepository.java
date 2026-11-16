package com.chibidoll.dressup.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u0005J\u000e\u0010\u0006\u001a\u00020\u0007H\u00a6@\u00a2\u0006\u0002\u0010\u0005J\u001c\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\t\u001a\u00020\nH\u00a6@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\f\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u000eH\u00a6@\u00a2\u0006\u0002\u0010\u000fJ\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u0005J\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0003H\u00a6@\u00a2\u0006\u0002\u0010\u0005\u00a8\u0006\u0013"}, d2 = {"Lcom/chibidoll/dressup/data/repository/CatalogRepository;", "", "getCatalog", "", "Lcom/chibidoll/dressup/domain/model/CatalogItem;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDefaultLook", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "getItemsByCategory", "category", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLookForPreset", "preset", "Lcom/chibidoll/dressup/domain/model/CharacterPreset;", "(Lcom/chibidoll/dressup/domain/model/CharacterPreset;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPalettes", "Lcom/chibidoll/dressup/domain/model/ColorPalette;", "getPresetCharacters", "app_release"})
public abstract interface CatalogRepository {
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCatalog(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.chibidoll.dressup.domain.model.CatalogItem>> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getItemsByCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.chibidoll.dressup.domain.model.CatalogItem>> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDefaultLook(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.chibidoll.dressup.domain.model.CharacterLook> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPalettes(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.chibidoll.dressup.domain.model.ColorPalette>> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPresetCharacters(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.chibidoll.dressup.domain.model.CharacterPreset>> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLookForPreset(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterPreset preset, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.chibidoll.dressup.domain.model.CharacterLook> $completion);
}