package com.erhan.alex

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Entries")
data class Entry (
    @PrimaryKey var name: String,
    var date: String,
    var rating: Int,
    var notes: String,
    @ColumnInfo (name = "pic") var pic: Int
)