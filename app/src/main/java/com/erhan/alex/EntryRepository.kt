package com.erhan.alex

import androidx.lifecycle.LiveData

class EntryRepository(private val entryDao: EntryDao) {
    val allEntries: LiveData<List<Entry>> = entryDao.getAll()

    fun insert(entry: Entry) {
        entryDao.insertAll(entry)
    }
}