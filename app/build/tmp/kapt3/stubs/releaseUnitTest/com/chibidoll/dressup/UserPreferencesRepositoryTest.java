package com.chibidoll.dressup;

@org.junit.runner.RunWith(value = org.robolectric.RobolectricTestRunner.class)
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\f\u0010\u000b\u001a\u00060\fj\u0002`\rH\u0007J\f\u0010\u000e\u001a\u00060\fj\u0002`\rH\u0007J\f\u0010\u000f\u001a\u00060\fj\u0002`\rH\u0007J\f\u0010\u0010\u001a\u00060\fj\u0002`\rH\u0007J\b\u0010\u0011\u001a\u00020\fH\u0007J\b\u0010\u0012\u001a\u00020\fH\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/chibidoll/dressup/UserPreferencesRepositoryTest;", "", "()V", "inMemoryDataStore", "Lcom/chibidoll/dressup/InMemoryDataStore;", "repository", "Lcom/chibidoll/dressup/data/local/UserPreferencesRepository;", "testDispatcher", "Lkotlinx/coroutines/test/TestDispatcher;", "testScope", "Lkotlinx/coroutines/test/TestScope;", "defaultPreferences_returnsSensibleDefaults", "", "Lkotlinx/coroutines/test/TestResult;", "setLanguageCode_updatesAndPersistsLanguage", "setOnboardingCompleted_updatesFlow", "setSoundEnabled_togglesSoundSetting", "setUp", "soundManager_respectsMuteFlag", "app_releaseUnitTest"})
@kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
public final class UserPreferencesRepositoryTest {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.test.TestDispatcher testDispatcher = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.test.TestScope testScope = null;
    private com.chibidoll.dressup.InMemoryDataStore inMemoryDataStore;
    private com.chibidoll.dressup.data.local.UserPreferencesRepository repository;
    
    public UserPreferencesRepositoryTest() {
        super();
    }
    
    @org.junit.Before()
    public final void setUp() {
    }
    
    @org.junit.Test()
    public final void defaultPreferences_returnsSensibleDefaults() {
    }
    
    @org.junit.Test()
    public final void setOnboardingCompleted_updatesFlow() {
    }
    
    @org.junit.Test()
    public final void setSoundEnabled_togglesSoundSetting() {
    }
    
    @org.junit.Test()
    public final void setLanguageCode_updatesAndPersistsLanguage() {
    }
    
    @org.junit.Test()
    public final void soundManager_respectsMuteFlag() {
    }
}