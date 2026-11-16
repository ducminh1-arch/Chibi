package com.chibidoll.dressup;

/**
 * In-memory test double for DataStore<Preferences> to avoid Windows-specific
 * SingleProcessDataStore file rename locks (Issue b/173082141).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u000f\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0002\u0010\u0004JA\u0010\u000b\u001a\u00020\u000221\u0010\f\u001a-\b\u0001\u0012\u0013\u0012\u00110\u0002\u00a2\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00020\u0011\u0012\u0006\u0012\u0004\u0018\u00010\u00120\rH\u0096@\u00a2\u0006\u0002\u0010\u0013R\u001a\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00020\u0006X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/chibidoll/dressup/InMemoryDataStore;", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "initialPreferences", "(Landroidx/datastore/preferences/core/Preferences;)V", "data", "Lkotlinx/coroutines/flow/Flow;", "getData", "()Lkotlinx/coroutines/flow/Flow;", "state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "updateData", "transform", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "t", "Lkotlin/coroutines/Continuation;", "", "(Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_releaseUnitTest"})
public final class InMemoryDataStore implements androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<androidx.datastore.preferences.core.Preferences> state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<androidx.datastore.preferences.core.Preferences> data = null;
    
    public InMemoryDataStore(@org.jetbrains.annotations.NotNull()
    androidx.datastore.preferences.core.Preferences initialPreferences) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<androidx.datastore.preferences.core.Preferences> getData() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object updateData(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super androidx.datastore.preferences.core.Preferences, ? super kotlin.coroutines.Continuation<? super androidx.datastore.preferences.core.Preferences>, ? extends java.lang.Object> transform, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super androidx.datastore.preferences.core.Preferences> $completion) {
        return null;
    }
    
    public InMemoryDataStore() {
        super();
    }
}