package com.chibidoll.dressup.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLookDao {
    @Query("SELECT * FROM saved_looks ORDER BY createdAt DESC")
    fun getAllLooks(): Flow<List<SavedLookEntity>>

    @Query("SELECT * FROM saved_looks WHERE id = :id")
    suspend fun getLookById(id: Long): SavedLookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(look: SavedLookEntity): Long

    @Delete
    suspend fun deleteLook(look: SavedLookEntity)

    @Query("DELETE FROM saved_looks WHERE id = :id")
    suspend fun deleteLookById(id: Long)
}
