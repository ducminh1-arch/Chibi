package com.chibidoll.dressup.ui.dressup.components;

/**
 * High-performance on-screen layer compositor:
 * - Adaptive decode cap:
 *  - On devices that are not low-RAM (ActivityManager.isLowRamDevice() == false and memoryClass >= 256),
 *    composites cached base/overlay bitmaps at full 1024x1536 px.
 *  - On low-RAM devices, keeps 640x960 px to safeguard memory.
 * - Decodes each layer one at a time and recycles immediately after drawing onto the Canvas.
 * - Leaves dynamic eye layers (eyes_iris and eyes_line) separate so idle blinking (~120ms every ~3.8s)
 *  recomposes without touching or re-rendering the static layers.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u0004J4\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00042\b\b\u0002\u0010\u0016\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\u0017J4\u0010\u0018\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u00042\b\b\u0002\u0010\u0016\u001a\u00020\u0004H\u0086@\u00a2\u0006\u0002\u0010\u0017J2\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\"H\u0002J\u001a\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040$2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010%\u001a\u00020&2\u0006\u0010\u0011\u001a\u00020\u0012R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/chibidoll/dressup/ui/dressup/components/DollCanvasCompositor;", "", "()V", "HIGH_RES_CANVAS_HEIGHT", "", "HIGH_RES_CANVAS_WIDTH", "LOW_RAM_CANVAS_HEIGHT", "LOW_RAM_CANVAS_WIDTH", "MAX_CANVAS_HEIGHT", "MAX_CANVAS_WIDTH", "calculateInSampleSize", "options", "Landroid/graphics/BitmapFactory$Options;", "reqWidth", "reqHeight", "compositeBaseLayers", "Landroid/graphics/Bitmap;", "context", "Landroid/content/Context;", "look", "Lcom/chibidoll/dressup/domain/model/CharacterLook;", "targetWidth", "targetHeight", "(Landroid/content/Context;Lcom/chibidoll/dressup/domain/model/CharacterLook;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "compositeOverlayLayers", "decodeAndDrawLayer", "", "canvas", "Landroid/graphics/Canvas;", "relativePath", "", "destRect", "Landroid/graphics/Rect;", "paint", "Landroid/graphics/Paint;", "getMaxCanvasDimensions", "Lkotlin/Pair;", "isHighEndDevice", "", "app_debug"})
public final class DollCanvasCompositor {
    public static final int LOW_RAM_CANVAS_WIDTH = 640;
    public static final int LOW_RAM_CANVAS_HEIGHT = 960;
    public static final int HIGH_RES_CANVAS_WIDTH = 1024;
    public static final int HIGH_RES_CANVAS_HEIGHT = 1536;
    public static final int MAX_CANVAS_WIDTH = 640;
    public static final int MAX_CANVAS_HEIGHT = 960;
    @org.jetbrains.annotations.NotNull()
    public static final com.chibidoll.dressup.ui.dressup.components.DollCanvasCompositor INSTANCE = null;
    
    private DollCanvasCompositor() {
        super();
    }
    
    /**
     * Checks whether the current device is a high-end device with sufficient RAM:
     * ActivityManager.isLowRamDevice() == false and memoryClass >= 256.
     */
    public final boolean isHighEndDevice(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    /**
     * Returns the maximum render dimensions for on-screen caching:
     * (1024, 1536) on high-end devices, (640, 960) on low-RAM devices.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlin.Pair<java.lang.Integer, java.lang.Integer> getMaxCanvasDimensions(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    public final int calculateInSampleSize(@org.jetbrains.annotations.NotNull()
    android.graphics.BitmapFactory.Options options, int reqWidth, int reqHeight) {
        return 0;
    }
    
    /**
     * Composites base layers (zIndex < 79, e.g. background, wings, body, clothes, blush, mouth)
     * into a single cached bitmap.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object compositeBaseLayers(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterLook look, int targetWidth, int targetHeight, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super android.graphics.Bitmap> $completion) {
        return null;
    }
    
    /**
     * Composites overlay layers (zIndex > 82, e.g. eyebrows, glasses, ears, hair_front, hat, accessory)
     * into a single cached bitmap.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object compositeOverlayLayers(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.chibidoll.dressup.domain.model.CharacterLook look, int targetWidth, int targetHeight, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super android.graphics.Bitmap> $completion) {
        return null;
    }
    
    private final void decodeAndDrawLayer(android.content.Context context, android.graphics.Canvas canvas, java.lang.String relativePath, android.graphics.Rect destRect, android.graphics.Paint paint) {
    }
}