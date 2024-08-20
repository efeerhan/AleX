package com.erhan.alex

import androidx.lifecycle.LiveData

class EntryRepository(private val entryDao: EntryDao) {
    val allEntries: LiveData<List<Entry>> = entryDao.getAll()

    fun insert(entry: Entry) {
        entryDao.insertAll(entry)
    }

    fun update(id: Int, newName: String, newWhere: String, newKind: String, newRating: Int, newNotes: String, newDate: String) {
        entryDao.update(id, newName, newWhere, newKind, newRating, newNotes, newDate)
    }

    fun delete(id: Int) {
        entryDao.delete(id)
    }
}