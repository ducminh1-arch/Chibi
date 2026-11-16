package com.chibidoll.dressup;

@org.junit.runner.RunWith(value = org.robolectric.RobolectricTestRunner.class)
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0007J\b\u0010\t\u001a\u00020\bH\u0007J\b\u0010\n\u001a\u00020\bH\u0007J\b\u0010\u000b\u001a\u00020\bH\u0007J\b\u0010\f\u001a\u00020\bH\u0007J\b\u0010\r\u001a\u00020\bH\u0007J\b\u0010\u000e\u001a\u00020\bH\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/chibidoll/dressup/SavedLookDaoTest;", "", "()V", "dao", "Lcom/chibidoll/dressup/data/local/SavedLookDao;", "db", "Lcom/chibidoll/dressup/data/local/AppDatabase;", "closeDb", "", "createDb", "deleteLook_removesRecordSuccessfully", "editLook_changeColor_save_updatesSameRecord_noDuplicate_preservesPresetIdAndLayerColors", "getLookById_returnsCorrectRecord", "insertAndGetLooks_orderedNewestFirst", "updateLook_isIdempotent", "app_debugUnitTest"})
public final class SavedLookDaoTest {
    private com.chibidoll.dressup.data.local.AppDatabase db;
    private com.chibidoll.dressup.data.local.SavedLookDao dao;
    
    public SavedLookDaoTest() {
        super();
    }
    
    @org.junit.Before()
    public final void createDb() {
    }
    
    @org.junit.After()
    @kotlin.jvm.Throws(exceptionClasses = {java.io.IOException.class})
    public final void closeDb() throws java.io.IOException {
    }
    
    @org.junit.Test()
    public final void insertAndGetLooks_orderedNewestFirst() {
    }
    
    @org.junit.Test()
    public final void updateLook_isIdempotent() {
    }
    
    @org.junit.Test()
    public final void deleteLook_removesRecordSuccessfully() {
    }
    
    @org.junit.Test()
    public final void getLookById_returnsCorrectRecord() {
    }
    
    @org.junit.Test()
    public final void editLook_changeColor_save_updatesSameRecord_noDuplicate_preservesPresetIdAndLayerColors() {
    }
}