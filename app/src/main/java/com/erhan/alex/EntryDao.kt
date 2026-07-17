package com.erhan.alex

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

data class UuidTimestamp(val uuid: String, val updatedAt: Long)

@Dao
interface EntryDao {
    @Query("SELECT * FROM Entries")
    fun getAll(): LiveData<List<Entry>>

    @Query("SELECT * FROM Entries")
    suspend fun getAllOnce(): List<Entry>

    @Query("SELECT pic FROM Entries WHERE id==:id")
    fun getPicByID(id: Int): Int

    @Query("SELECT MAX(pic) FROM Entries")
    fun getMaxPic(): Int

    @Query("SELECT COUNT(name) FROM Entries")
    fun getCount(): Int

    @Query("SELECT uuid, updatedAt FROM Entries")
    suspend fun getAllUuidTimestamps(): List<UuidTimestamp>

    @Query("UPDATE Entries SET name=:newName,bwhere=:newWhere,kind=:newKind,notes=:newNotes,date=:newDate,updatedAt=:newUpdatedAt WHERE id == :id")
    fun update(id: Int, newName: String, newWhere: String, newKind: String, newNotes: String, newDate: String, newUpdatedAt: Long)

    @Insert
    fun insertAll(vararg users: Entry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: Entry)

    @Query("DELETE FROM Entries WHERE id == :id")
    fun delete(id: Int)
}