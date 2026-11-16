package com.chibidoll.dressup

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.chibidoll.dressup.data.local.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class RoomMigrationTest {

    private val TEST_DB = "migration-test.db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName ?: "com.chibidoll.dressup.data.local.AppDatabase",
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migration_1_to_2_addsLayerColorsJsonColumn_againstCommittedSchemas() {
        // 1. Create database with Version 1 schema JSON
        var db = helper.createDatabase(TEST_DB, 1)

        val v1Values = ContentValues().apply {
            put("id", 101L)
            put("createdAt", 1700000000000L)
            put("equippedItemIdsJson", """{"top":"top_sailor"}""")
            put("backgroundId", "bg_dream")
            put("thumbnailPath", "/path/thumb101.png")
        }
        db.insert("saved_looks", SQLiteDatabase.CONFLICT_REPLACE, v1Values)
        db.close()

        // 2. Run Migration 1 -> 2 and validate against 2.json schema
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2)

        val cursor = db.query("SELECT * FROM saved_looks WHERE id = 101")
        assertTrue(cursor.moveToFirst())

        val layerColorsCol = cursor.getColumnIndex("layerColorsJson")
        assertTrue("layerColorsJson column must exist in schema v2", layerColorsCol >= 0)
        assertEquals("{}", cursor.getString(layerColorsCol))
        cursor.close()
        db.close()
    }

    @Test
    fun migration_2_to_3_addsPresetIdColumn_defaultNullForOldRows_againstCommittedSchemas() {
        // 1. Create database with Version 2 schema JSON
        var db = helper.createDatabase(TEST_DB, 2)

        val v2Values = ContentValues().apply {
            put("id", 202L)
            put("createdAt", 1700000005000L)
            put("equippedItemIdsJson", """{"dress":"dress_princess"}""")
            put("backgroundId", "bg_hearts")
            put("thumbnailPath", "/path/thumb202.png")
            put("layerColorsJson", """{"DRESS":4289895127}""")
        }
        db.insert("saved_looks", SQLiteDatabase.CONFLICT_REPLACE, v2Values)
        db.close()

        // 2. Run Migration 2 -> 3 and validate against 3.json schema
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3)

        val cursor = db.query("SELECT * FROM saved_looks WHERE id = 202")
        assertTrue(cursor.moveToFirst())

        val presetIdCol = cursor.getColumnIndex("presetId")
        assertTrue("presetId column must exist in schema v3", presetIdCol >= 0)
        // Verify default null for old rows
        assertNull("Old rows must have null presetId by default", cursor.getString(presetIdCol))

        val layerColorsCol = cursor.getColumnIndex("layerColorsJson")
        assertEquals("""{"DRESS":4289895127}""", cursor.getString(layerColorsCol))
        cursor.close()

        // 3. Insert a new look with presetId in Version 3
        val v3Values = ContentValues().apply {
            put("id", 303L)
            put("createdAt", 1700000010000L)
            put("equippedItemIdsJson", """{"top":"top_lavender"}""")
            put("thumbnailPath", "/path/thumb303.png")
            put("layerColorsJson", """{}""")
            put("presetId", "preset_luna")
        }
        db.insert("saved_looks", SQLiteDatabase.CONFLICT_REPLACE, v3Values)

        val cursorV3 = db.query("SELECT * FROM saved_looks WHERE id = 303")
        assertTrue(cursorV3.moveToFirst())
        assertEquals("preset_luna", cursorV3.getString(cursorV3.getColumnIndex("presetId")))
        cursorV3.close()
        db.close()
    }

    @Test
    fun allMigrations_1_to_3_endToEnd_migratesCleanlyAndRoomCanQuery() = runBlocking {
        // 1. Create with v1
        var db = helper.createDatabase(TEST_DB, 1)
        val v1Values = ContentValues().apply {
            put("id", 1L)
            put("createdAt", 1700000000000L)
            put("equippedItemIdsJson", """{"hair_front":"hair_lilac_twin"}""")
            put("backgroundId", "bg_dream")
            put("thumbnailPath", "/path/thumb1.png")
        }
        db.insert("saved_looks", SQLiteDatabase.CONFLICT_REPLACE, v1Values)
        db.close()

        // 2. Run both migrations (1 -> 2 and 2 -> 3)
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
        db.close()

        // 3. Open migrated DB using full Room Database
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val roomDb = Room.databaseBuilder(context, AppDatabase::class.java, TEST_DB)
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .allowMainThreadQueries()
            .build()

        val dao = roomDb.savedLookDao()
        val allLooks = dao.getAllLooks().first()
        assertEquals(1, allLooks.size)

        val look = allLooks[0]
        assertEquals(1L, look.id)
        assertEquals("bg_dream", look.backgroundId)
        assertEquals("{}", look.layerColorsJson)
        assertNull(look.presetId)

        roomDb.close()
    }
}
