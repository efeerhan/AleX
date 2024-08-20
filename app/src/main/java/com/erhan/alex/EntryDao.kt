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

    @Query("SELECT COUNT(name) FROM Entries")
    fun getCount(): Int

    @Query("UPDATE Entries SET name=:newName,bwhere=:newWhere,kind=:newKind,rating=:newRating,notes=:newNotes,date=:newDate WHERE id == :id")
    fun update(id: Int, newName: String, newWhere: String, newKind: String, newRating: Int, newNotes: String, newDate: String)

    @Insert
    fun insertAll(vararg users: Entry)

    @Query("DELETE FROM Entries WHERE id == :id")
    fun delete(id: Int)
}