package com.chibidoll.dressup;

@org.robolectric.annotation.Implements(value = android.graphics.Bitmap.class)
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010%\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\b\b\u0007\u0018\u0000 \"2\u00020\u0001:\u0001\"B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0004H\u0007J\u0018\u0010\u001d\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u0004H\u0007J \u0010 \u001a\u00020\u001b2\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u00042\u0006\u0010!\u001a\u00020\u0004H\u0007R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001c\u0010\t\u001a\u00020\n8GX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001c\u0010\u000f\u001a\u00020\u00048GX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0006\"\u0004\b\u0011\u0010\bR\u001d\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00040\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u001c\u0010\u0017\u001a\u00020\u00048GX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0006\"\u0004\b\u0019\u0010\b\u00a8\u0006#"}, d2 = {"Lcom/chibidoll/dressup/ShadowTestBitmap;", "", "()V", "baseColor", "", "getBaseColor", "()I", "setBaseColor", "(I)V", "config", "Landroid/graphics/Bitmap$Config;", "getConfig", "()Landroid/graphics/Bitmap$Config;", "setConfig", "(Landroid/graphics/Bitmap$Config;)V", "height", "getHeight", "setHeight", "pixels", "", "", "getPixels", "()Ljava/util/Map;", "width", "getWidth", "setWidth", "eraseColor", "", "c", "getPixel", "x", "y", "setPixel", "color", "Companion", "app_releaseUnitTest"})
public final class ShadowTestBitmap {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.Long, java.lang.Integer> pixels = null;
    private int baseColor = 0;
    private int width = 0;
    private int height = 0;
    @org.jetbrains.annotations.NotNull()
    private android.graphics.Bitmap.Config config = android.graphics.Bitmap.Config.ARGB_8888;
    @org.jetbrains.annotations.NotNull()
    public static final com.chibidoll.dressup.ShadowTestBitmap.Companion Companion = null;
    
    public ShadowTestBitmap() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.Long, java.lang.Integer> getPixels() {
        return null;
    }
    
    public final int getBaseColor() {
        return 0;
    }
    
    public final void setBaseColor(int p0) {
    }
    
    @org.robolectric.annotation.Implementation()
    public final int getWidth() {
        return 0;
    }
    
    public final void setWidth(int p0) {
    }
    
    @org.robolectric.annotation.Implementation()
    public final int getHeight() {
        return 0;
    }
    
    public final void setHeight(int p0) {
    }
    
    @org.robolectric.annotation.Implementation()
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap.Config getConfig() {
        return null;
    }
    
    public final void setConfig(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap.Config p0) {
    }
    
    @org.robolectric.annotation.Implementation()
    public final void setPixel(int x, int y, int color) {
    }
    
    @org.robolectric.annotation.Implementation()
    public final int getPixel(int x, int y) {
        return 0;
    }
    
    @org.robolectric.annotation.Implementation()
    public final void eraseColor(int c) {
    }
    
    @org.robolectric.annotation.Implementation()
    @kotlin.jvm.JvmStatic()
    @org.jetbrains.annotations.NotNull()
    public static final android.graphics.Bitmap createBitmap(int w, int h, @org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap.Config conf) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\tH\u0007\u00a8\u0006\n"}, d2 = {"Lcom/chibidoll/dressup/ShadowTestBitmap$Companion;", "", "()V", "createBitmap", "Landroid/graphics/Bitmap;", "w", "", "h", "conf", "Landroid/graphics/Bitmap$Config;", "app_releaseUnitTest"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.robolectric.annotation.Implementation()
        @kotlin.jvm.JvmStatic()
        @org.jetbrains.annotations.NotNull()
        public final android.graphics.Bitmap createBitmap(int w, int h, @org.jetbrains.annotations.NotNull()
        android.graphics.Bitmap.Config conf) {
            return null;
        }
    }
}