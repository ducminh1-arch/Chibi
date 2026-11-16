package com.chibidoll.dressup

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.chibidoll.dressup.data.local.AppDatabase
import com.chibidoll.dressup.data.local.SavedLookDao
import com.chibidoll.dressup.data.local.SavedLookEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class SavedLookDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: SavedLookDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.savedLookDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetLooks_orderedNewestFirst() = runBlocking {
        val look1 = SavedLookEntity(
            id = 1L,
            createdAt = 1000L,
            equippedItemIdsJson = """{"top":"top_01"}""",
            thumbnailPath = "/path/1.png"
        )
        val look2 = SavedLookEntity(
            id = 2L,
            createdAt = 2000L,
            equippedItemIdsJson = """{"dress":"dress_01"}""",
            thumbnailPath = "/path/2.png"
        )

        dao.insertOrUpdate(look1)
        dao.insertOrUpdate(look2)

        val allLooks = dao.getAllLooks().first()
        assertEquals(2, allLooks.size)
        // Newest first (createdAt DESC)
        assertEquals(2L, allLooks[0].id)
        assertEquals(1L, allLooks[1].id)
    }

    @Test
    fun updateLook_isIdempotent() = runBlocking {
        val initialLook = SavedLookEntity(
            id = 10L,
            createdAt = 1000L,
            equippedItemIdsJson = """{"top":"top_01"}""",
            thumbnailPath = "/path/original.png"
        )
        dao.insertOrUpdate(initialLook)

        // Same ID, updated attributes
        val updatedLook = SavedLookEntity(
            id = 10L,
            createdAt = 3000L,
            equippedItemIdsJson = """{"top":"top_02","hat":"hat_01"}""",
            thumbnailPath = "/path/updated.png"
        )
        dao.insertOrUpdate(updatedLook)

        val looks = dao.getAllLooks().first()
        // No duplicates created
        assertEquals(1, looks.size)
        assertEquals(10L, looks[0].id)
        assertEquals(3000L, looks[0].createdAt)
        assertEquals("/path/updated.png", looks[0].thumbnailPath)
        assertEquals("""{"top":"top_02","hat":"hat_01"}""", looks[0].equippedItemIdsJson)
    }

    @Test
    fun deleteLook_removesRecordSuccessfully() = runBlocking {
        val look = SavedLookEntity(
            id = 5L,
            createdAt = 5000L,
            equippedItemIdsJson = """{}""",
            thumbnailPath = "/path/5.png"
        )
        dao.insertOrUpdate(look)
        assertEquals(1, dao.getAllLooks().first().size)

        dao.deleteLook(look)
        assertTrue(dao.getAllLooks().first().isEmpty())
    }

    @Test
    fun getLookById_returnsCorrectRecord() = runBlocking {
        val look = SavedLookEntity(
            id = 7L,
            createdAt = 7000L,
            equippedItemIdsJson = """{"shoes":"shoes_01"}""",
            backgroundId = "bg_stars",
            thumbnailPath = "/path/7.png"
        )
        dao.insertOrUpdate(look)

        val fetched = dao.getLookById(7L)
        assertNotNull(fetched)
        assertEquals("bg_stars", fetched?.backgroundId)

        val nonExistent = dao.getLookById(999L)
        assertNull(nonExistent)
    }

    @Test
    fun editLook_changeColor_save_updatesSameRecord_noDuplicate_preservesPresetIdAndLayerColors() = runBlocking {
        // Initial look saved with Luna preset and hair color
        val initialLook = SavedLookEntity(
            id = 42L,
            createdAt = 1000L,
            equippedItemIdsJson = """{"top":"top_sailor","hair_front":"hair_lilac_twin"}""",
            thumbnailPath = "/path/thumb42.png",
            layerColorsJson = """{"HAIR_FRONT":4294473130}""",
            presetId = "preset_luna"
        )
        dao.insertOrUpdate(initialLook)

        val beforeEdit = dao.getAllLooks().first()
        assertEquals(1, beforeEdit.size)
        assertEquals("preset_luna", beforeEdit[0].presetId)

        // User edits look: changes hair color and dyes dress
        val updatedLook = initialLook.copy(
            layerColorsJson = """{"HAIR_FRONT":4294473130,"DRESS":4289895127}""",
            thumbnailPath = "/path/thumb42_v2.png"
        )
        dao.insertOrUpdate(updatedLook)

        val afterEdit = dao.getAllLooks().first()
        // Must be exactly 1 record (no duplicates)
        assertEquals(1, afterEdit.size)
        val saved = afterEdit[0]
        assertEquals(42L, saved.id)
        assertEquals("preset_luna", saved.presetId)
        assertTrue(saved.layerColorsJson.contains("HAIR_FRONT"))
        assertTrue(saved.layerColorsJson.contains("DRESS"))
        assertEquals("/path/thumb42_v2.png", saved.thumbnailPath)
    }
}

