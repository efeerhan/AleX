package com.erhan.alex

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EntryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EntryRepository
    val allEntries: LiveData<List<Entry>>

    init {
        val entryDao = AppDatabase.getDatabase(application).entryDao()
        repository = EntryRepository(entryDao)
        allEntries = repository.allEntries
    }

    fun insert(entry: Entry) = viewModelScope.launch {
        repository.insert(entry)
    }

    fun update(entry: Entry) = viewModelScope.launch {
        repository.update(entry)
    }
}