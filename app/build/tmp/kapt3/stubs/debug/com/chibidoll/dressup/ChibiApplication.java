package com.chibidoll.dressup;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u0000 \u00152\u00020\u00012\u00020\u0002:\u0001\u0015B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0014H\u0016R\u001e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001e\u0010\n\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\t@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001e\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\r@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0016"}, d2 = {"Lcom/chibidoll/dressup/ChibiApplication;", "Landroid/app/Application;", "Lcoil/ImageLoaderFactory;", "()V", "<set-?>", "Lcom/chibidoll/dressup/data/repository/CatalogRepository;", "catalogRepository", "getCatalogRepository", "()Lcom/chibidoll/dressup/data/repository/CatalogRepository;", "Lcom/chibidoll/dressup/data/repository/SavedLooksRepository;", "savedLooksRepository", "getSavedLooksRepository", "()Lcom/chibidoll/dressup/data/repository/SavedLooksRepository;", "Lcom/chibidoll/dressup/data/local/UserPreferencesRepository;", "userPreferencesRepository", "getUserPreferencesRepository", "()Lcom/chibidoll/dressup/data/local/UserPreferencesRepository;", "newImageLoader", "Lcoil/ImageLoader;", "onCreate", "", "Companion", "app_debug"})
public final class ChibiApplication extends android.app.Application implements coil.ImageLoaderFactory {
    private com.chibidoll.dressup.data.repository.CatalogRepository catalogRepository;
    private com.chibidoll.dressup.data.repository.SavedLooksRepository savedLooksRepository;
    private com.chibidoll.dressup.data.local.UserPreferencesRepository userPreferencesRepository;
    @org.jetbrains.annotations.NotNull()
    public static final com.chibidoll.dressup.ChibiApplication.Companion Companion = null;
    
    public ChibiApplication() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.chibidoll.dressup.data.repository.CatalogRepository getCatalogRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.chibidoll.dressup.data.repository.SavedLooksRepository getSavedLooksRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.chibidoll.dressup.data.local.UserPreferencesRepository getUserPreferencesRepository() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public coil.ImageLoader newImageLoader() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/chibidoll/dressup/ChibiApplication$Companion;", "", "()V", "applyLocale", "", "langCode", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final void applyLocale(@org.jetbrains.annotations.NotNull()
        java.lang.String langCode) {
        }
    }
}