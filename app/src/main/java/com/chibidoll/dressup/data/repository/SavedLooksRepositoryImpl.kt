package com.chibidoll.dressup.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.chibidoll.dressup.data.local.SavedLookDao
import com.chibidoll.dressup.data.local.SavedLookEntity
import com.chibidoll.dressup.data.local.SavedLookMapper
import com.chibidoll.dressup.domain.model.CharacterLook
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SavedLooksRepositoryImpl(
    private val savedLookDao: SavedLookDao,
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SavedLooksRepository {

    override fun getAllLooks(): Flow<List<SavedLookEntity>> {
        return savedLookDao.getAllLooks()
    }

    override suspend fun getLookById(id: Long): SavedLookEntity? {
        return withContext(ioDispatcher) {
            savedLookDao.getLookById(id)
        }
    }

    override suspend fun saveLook(
        look: CharacterLook,
        thumbnailBitmap: Bitmap,
        existingId: Long?
    ): Result<SavedLookEntity> = withContext(ioDispatcher) {
        runCatching {
            val thumbnailsDir = File(context.filesDir, "thumbnails").apply {
                if (!exists() && !mkdirs()) {
                    throw IOException("Could not create thumbnails directory: $absolutePath")
                }
            }

            if (existingId != null && existingId > 0) {
                // Updating existing look
                val oldEntity = savedLookDao.getLookById(existingId)
                val thumbnailFile = File(thumbnailsDir, "look_${existingId}_${System.currentTimeMillis()}.png")

                FileOutputStream(thumbnailFile).use { out ->
                    if (!thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                        throw IOException("Failed to compress thumbnail")
                    }
                }

                // Delete old thumbnail file if path differs and exists
                oldEntity?.thumbnailPath?.let { oldPath ->
                    val oldFile = File(oldPath)
                    if (oldFile.exists() && oldFile.absolutePath != thumbnailFile.absolutePath) {
                        oldFile.delete()
                    }
                }

                val updatedEntity = SavedLookMapper.toEntity(
                    look = look,
                    thumbnailPath = thumbnailFile.absolutePath,
                    id = existingId,
                    createdAt = System.currentTimeMillis()
                )
                savedLookDao.insertOrUpdate(updatedEntity)
                updatedEntity
            } else {
                // Inserting brand new look
                val timestamp = System.currentTimeMillis()
                val tempFile = File(thumbnailsDir, "look_new_${timestamp}.png")

                FileOutputStream(tempFile).use { out ->
                    if (!thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                        throw IOException("Failed to compress thumbnail")
                    }
                }

                val entityToInsert = SavedLookMapper.toEntity(
                    look = look,
                    thumbnailPath = tempFile.absolutePath,
                    id = 0,
                    createdAt = timestamp
                )
                val newId = savedLookDao.insertOrUpdate(entityToInsert)

                // Rename tempFile to include the real generated id
                val finalFile = File(thumbnailsDir, "look_${newId}_${timestamp}.png")
                val finalPath = if (tempFile.renameTo(finalFile)) {
                    finalFile.absolutePath
                } else {
                    tempFile.absolutePath
                }

                val finalEntity = entityToInsert.copy(id = newId, thumbnailPath = finalPath)
                savedLookDao.insertOrUpdate(finalEntity)
                finalEntity
            }
        }
    }

    override suspend fun deleteLook(look: SavedLookEntity): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val file = File(look.thumbnailPath)
            if (file.exists()) {
                file.delete()
            }
            savedLookDao.deleteLook(look)
        }
    }

    override suspend fun deleteLookById(id: Long): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val entity = savedLookDao.getLookById(id)
            if (entity != null) {
                val file = File(entity.thumbnailPath)
                if (file.exists()) {
                    file.delete()
                }
                savedLookDao.deleteLookById(id)
            }
        }
    }
}

