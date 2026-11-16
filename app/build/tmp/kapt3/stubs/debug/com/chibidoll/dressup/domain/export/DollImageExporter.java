package com.chibidoll.dressup.domain.export;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rJ\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fJ\u0010\u0010\u0011\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u001e\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J=\u0010\u0018\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u00162#\u0010\u0019\u001a\u001f\u0012\u0013\u0012\u00110\u001b\u00a2\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b(\u001e\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u001a2\b\b\u0002\u0010\u001f\u001a\u00020 J6\u0010!\u001a\b\u0012\u0004\u0012\u00020#0\"2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010$\u001a\u00020\u000f2\b\b\u0002\u0010%\u001a\u00020\u001bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b&\u0010\'J4\u0010(\u001a\b\u0012\u0004\u0012\u00020\t0\"2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010$\u001a\u00020\u000f2\u0006\u0010)\u001a\u00020\u001bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b*\u0010\'R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006+"}, d2 = {"Lcom/chibidoll/dressup/domain/export/DollImageExporter;", "", "()V", "MASTER_HEIGHT", "", "MASTER_WIDTH", "THUMBNAIL_HEIGHT", "THUMBNAIL_WIDTH", "cleanOldSharedLooks", "", "context", "Landroid/content/Context;", "maxAgeMillis", "", "createThumbnail", "Landroid/graphics/Bitmap;", "masterBitmap", "drawDefaultBackground", "canvas", "Landroid/graphics/Canvas;", "renderMasterBitmap", "look", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "(Landroid/content/Context;Lcom/chibidoll/dressup/domain/model/CharacterLook;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "renderMasterBitmapWithLoader", "bitmapLoader", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "filePath", "recycleLoadedBitmaps", "", "saveToGallery", "Lkotlin/Result;", "Landroid/net/Uri;", "bitmap", "appName", "saveToGallery-BWLJW6A", "(Landroid/content/Context;Landroid/graphics/Bitmap;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "shareLook", "chooserTitle", "shareLook-BWLJW6A", "app_debug"})
public final class DollImageExporter {
    public static final int MASTER_WIDTH = 1024;
    public static final int MASTER_HEIGHT = 1536;
    public static final int THUMBNAIL_WIDTH = 341;
    public static final int THUMBNAIL_HEIGHT = 512;
    @org.jetbrains.annotations.NotNull()
    public static final com.chibidoll.dressup.domain.export.DollImageExporter INSTANCE = null;
    
    private DollImageExporter() {
        super();
    }
    
    /**
     * Composites master 1024x1536 Bitmap using the provided bitmapLoader.
     * Composites background first, then equipped layers in strict z-order ascending.
     * Can be tested with synthetic in-memory bitmaps.
     */
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap renderMasterBitmapWithLoader(@org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterLook look, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, android.graphics.Bitmap> bitmapLoader, boolean recycleLoadedBitmaps) {
        return null;
    }
    
    /**
     * Renders character + background into a crisp 1024x1536 Bitmap from asset files.
     * Composites background first, followed by equipped layers ordered by z-order.
     * Runs off the main thread.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object renderMasterBitmap(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterLook look, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super android.graphics.Bitmap> $completion) {
        return null;
    }
    
    private final void drawDefaultBackground(android.graphics.Canvas canvas) {
    }
    
    /**
     * Creates a high-quality 341x512 thumbnail from the master bitmap.
     */
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap createThumbnail(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap masterBitmap) {
        return null;
    }
    
    /**
     * Deletes cached shared PNGs older than the specified maxAgeMillis (default 24 hours).
     */
    public final void cleanOldSharedLooks(@org.jetbrains.annotations.NotNull()
    android.content.Context context, long maxAgeMillis) {
    }
}