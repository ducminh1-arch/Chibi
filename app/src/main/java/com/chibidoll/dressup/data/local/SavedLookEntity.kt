package com.chibidoll.dressup.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_looks")
data class SavedLookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val equippedItemIdsJson: String,
    val backgroundId: String? = null,
    val thumbnailPath: String,
    @ColumnInfo(name = "layerColorsJson", defaultValue = "{}")
    val layerColorsJson: String = "{}",
    @ColumnInfo(name = "presetId", defaultValue = "NULL")
    val presetId: String? = null
)
