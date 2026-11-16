package com.chibidoll.dressup.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ$\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\rH\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000e\u0010\u000fJ$\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u0011\u001a\u00020\u0012H\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0013\u0010\u0014J\u0014\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00170\u0016H\u0016J\u0018\u0010\u0018\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0011\u001a\u00020\u0012H\u0096@\u00a2\u0006\u0002\u0010\u0014J6\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\r0\n2\u0006\u0010\f\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0012H\u0096@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001e\u0010\u001fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006 "}, d2 = {"Lcom/chibidoll/dressup/data/repository/SavedLooksRepositoryImpl;", "Lcom/chibidoll/dressup/data/repository/SavedLooksRepository;", "savedLookDao", "Lcom/chibidoll/dressup/data/local/SavedLookDao;", "context", "Landroid/content/Context;", "ioDispatcher", "Lkotlinx/coroutines/CoroutineDispatcher;", "(Lcom/chibidoll/dressup/data/local/SavedLookDao;Landroid/content/Context;Lkotlinx/coroutines/CoroutineDispatcher;)V", "deleteLook", "Lkotlin/Result;", "", "look", "Lcom/chibidoll/dressup/data/local/SavedLookEntity;", "deleteLook-gIAlu-s", "(Lcom/chibidoll/dressup/data/local/SavedLookEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteLookById", "id", "", "deleteLookById-gIAlu-s", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllLooks", "Lkotlinx/coroutines/flow/Flow;", "", "getLookById", "saveLook", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "thumbnailBitmap", "Landroid/graphics/Bitmap;", "existingId", "saveLook-BWLJW6A", "(Lcom/chibidoll/dressup/domain/model/CharacterLook;Landroid/graphics/Bitmap;Ljava/lang/Long;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class SavedLooksRepositoryImpl implements com.chibidoll.dressup.data.repository.SavedLooksRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.chibidoll.dressup.data.local.SavedLookDao savedLookDao = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineDispatcher ioDispatcher = null;
    
    public SavedLooksRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.data.local.SavedLookDao savedLookDao, @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineDispatcher ioDispatcher) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<java.util.List<com.chibidoll.dressup.data.local.SavedLookEntity>> getAllLooks() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getLookById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.chibidoll.dressup.data.local.SavedLookEntity> $completion) {
        return null;
    }
}