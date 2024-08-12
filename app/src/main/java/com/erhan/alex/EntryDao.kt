package com.erhan.alex

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EntryDao {
    @Query("SELECT * FROM Entries")
    fun getAll(): LiveData<List<Entry>>

    @Query("SELECT MAX(pic) FROM Entries")
    fun getMaxPic(): Int

    @Insert
    fun insertAll(vararg users: Entry)

    @Delete
    fun delete(user: Entry)
}