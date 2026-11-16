package com.chibidoll.dressup;

@org.junit.runner.RunWith(value = org.robolectric.RobolectricTestRunner.class)
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\t\u001a\u00020\nH\u0007J\b\u0010\u000b\u001a\u00020\nH\u0007J\b\u0010\f\u001a\u00020\nH\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0005\u001a\u00020\u00068G\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\r"}, d2 = {"Lcom/chibidoll/dressup/RoomMigrationTest;", "", "()V", "TEST_DB", "", "helper", "Landroidx/room/testing/MigrationTestHelper;", "getHelper", "()Landroidx/room/testing/MigrationTestHelper;", "allMigrations_1_to_3_endToEnd_migratesCleanlyAndRoomCanQuery", "", "migration_1_to_2_addsLayerColorsJsonColumn_againstCommittedSchemas", "migration_2_to_3_addsPresetIdColumn_defaultNullForOldRows_againstCommittedSchemas", "app_releaseUnitTest"})
public final class RoomMigrationTest {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TEST_DB = "migration-test.db";
    @org.jetbrains.annotations.NotNull()
    private final androidx.room.testing.MigrationTestHelper helper = null;
    
    public RoomMigrationTest() {
        super();
    }
    
    @org.junit.Rule()
    @org.jetbrains.annotations.NotNull()
    public final androidx.room.testing.MigrationTestHelper getHelper() {
        return null;
    }
    
    @org.junit.Test()
    public final void migration_1_to_2_addsLayerColorsJsonColumn_againstCommittedSchemas() {
    }
    
    @org.junit.Test()
    public final void migration_2_to_3_addsPresetIdColumn_defaultNullForOldRows_againstCommittedSchemas() {
    }
    
    @org.junit.Test()
    public final void allMigrations_1_to_3_endToEnd_migratesCleanlyAndRoomCanQuery() {
    }
}