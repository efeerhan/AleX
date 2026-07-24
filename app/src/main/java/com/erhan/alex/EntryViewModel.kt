package com.erhan.alex

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EntryRepository
    val allEntries: LiveData<List<Entry>>

    /** Entries not yet confirmed in the cloud; drives the main-screen backup indicator. */
    val pendingPushCount: LiveData<Int> = PendingPushes.count

    init {
        val entryDao = AppDatabase.getDatabase(application).entryDao()
        repository = EntryRepository(entryDao, application)
        allEntries = repository.allEntries
    }

    /** Bootstrap-or-reconcile, run once per app start. Safe to call when signed out. */
    fun onAppStart() = viewModelScope.launch(Dispatchers.IO) {
        repository.onAppStart()
    }

    fun insert(entry: Entry) = viewModelScope.launch {
        repository.insert(entry)
    }

    fun update(id: Int, uuid: String, newName: String, newWhere: String, newKind: String, newNotes: String, newDate: String) = viewModelScope.launch {
        repository.update(id, uuid, newName, newWhere, newKind, newNotes, newDate)
    }

    fun delete(id: Int, uuid: String) = viewModelScope.launch {
        repository.delete(id, uuid)
    }

    fun onSignedIn() = viewModelScope.launch(Dispatchers.IO) {
        repository.onSignedIn()
    }

    fun restoreFromCloud() = viewModelScope.launch(Dispatchers.IO) {
        repository.restoreFromCloud()
    }

    fun reconcile() = viewModelScope.launch(Dispatchers.IO) {
        repository.reconcile()
    }
}
