package com.erhan.alex

import androidx.lifecycle.LiveData

class EntryRepository(private val entryDao: EntryDao) {
    val allEntries: LiveData<List<Entry>> = entryDao.getAll()

    fun insert(entry: Entry) {
        entryDao.insertAll(entry)
    }

    fun update(oldName: String, newName: String, newRating: String, newNotes: String) {
        entryDao.update(oldName, newName, newRating, newNotes)
    }

    fun delete(id: Int) {
        entryDao.delete(id)
    }
}