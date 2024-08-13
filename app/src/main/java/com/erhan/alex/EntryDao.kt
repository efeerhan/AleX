package com.erhan.alex

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EntryDao {
    @Query("SELECT * FROM Entries")
    fun getAll(): LiveData<List<Entry>>

    @Query("SELECT MAX(pic) FROM Entries")
    fun getMaxPic(): Int

    @Query("UPDATE Entries SET name=:newName,rating=:newRating, notes=:newNotes WHERE name == :oldName")
    fun update(oldName: String, newName: String, newRating: String, newNotes: String)

    @Insert
    fun insertAll(vararg users: Entry)

    @Query("DELETE FROM Entries WHERE name == :name")
    fun delete(name: String)
}