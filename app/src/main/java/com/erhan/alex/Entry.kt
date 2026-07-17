package com.erhan.alex

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity (tableName = "Entries", indices = [Index(value = ["uuid"], unique = true)])
data class Entry (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var bwhere: String,
    var kind: String,
    var date: String,
    var notes: String,
    @ColumnInfo (name = "pic") var pic: Int,
    // Stable key used as the Firestore document ID and the Cloud Storage/local image filename,
    // since the autoIncrement id/pic scheme can collide across devices.
    var uuid: String = UUID.randomUUID().toString(),
    var updatedAt: Long = System.currentTimeMillis()
)