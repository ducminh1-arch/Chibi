package com.chibidoll.dressup;

@org.robolectric.annotation.Implements(value = android.graphics.Canvas.class)
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0004H\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0002J*\u0010\f\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0007J\u0018\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u0011H\u0007J0\u0010\u0012\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0011H\u0007R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/chibidoll/dressup/ShadowTintTestCanvas;", "", "()V", "targetBitmap", "Landroid/graphics/Bitmap;", "__constructor__", "", "bitmap", "applyMultiply", "", "src", "filter", "drawBitmap", "left", "", "top", "paint", "Landroid/graphics/Paint;", "drawRect", "rect", "Landroid/graphics/Rect;", "right", "bottom", "app_debugUnitTest"})
public final class ShadowTintTestCanvas {
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Bitmap targetBitmap;
    
    public ShadowTintTestCanvas() {
        super();
    }
    
    @org.robolectric.annotation.Implementation()
    public final void __constructor__(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
    }
    
    @org.robolectric.annotation.Implementation()
    public final void drawRect(float left, float top, float right, float bottom, @org.jetbrains.annotations.NotNull()
    android.graphics.Paint paint) {
    }
    
    @org.robolectric.annotation.Implementation()
    public final void drawRect(@org.jetbrains.annotations.NotNull()
    android.graphics.Rect rect, @org.jetbrains.annotations.NotNull()
    android.graphics.Paint paint) {
    }
    
    @org.robolectric.annotation.Implementation()
    public final void drawBitmap(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, float left, float top, @org.jetbrains.annotations.Nullable()
    android.graphics.Paint paint) {
    }
    
    private final int applyMultiply(int src, int filter) {
        return 0;
    }
}