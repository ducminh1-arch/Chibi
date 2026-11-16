package com.chibidoll.dressup.data.repository

import android.graphics.Bitmap
import com.chibidoll.dressup.data.local.SavedLookEntity
import com.chibidoll.dressup.domain.model.CharacterLook
import kotlinx.coroutines.flow.Flow

interface SavedLooksRepository {
    fun getAllLooks(): Flow<List<SavedLookEntity>>
    suspend fun getLookById(id: Long): SavedLookEntity?
    suspend fun saveLook(
        look: CharacterLook,
        thumbnailBitmap: Bitmap,
        existingId: Long? = null
    ): Result<SavedLookEntity>
    suspend fun deleteLook(look: SavedLookEntity): Result<Unit>
    suspend fun deleteLookById(id: Long): Result<Unit>
}
