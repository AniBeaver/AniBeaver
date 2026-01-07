package org.anibeaver.anibeaver.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.core.datastructures.Entry
import org.anibeaver.anibeaver.core.datastructures.EntryData

object EntriesController {
    private var lastId = 0
    var entriesVersion by mutableStateOf(0) //updating forces resort/recomposition

    //TODO: if some tag doesn't exist in tagscontroller, remove its id from all entries
    // TODO: initial fill from some source of truth (database or anilist servers), comment out those placeholders
    private val _entries = HashMap<Int, Entry>()
    val entries: SnapshotStateList<Entry> = mutableStateListOf()

    fun addEntry(id: Int? = null, entryData: EntryData) {
        addEntry(Entry(entryData, id))
    }

    private fun addEntry(entry: Entry) {
        _entries.put(entry.id, entry)
        entries.add(entry)
        entriesVersion++
    }

    //update Entry and add if non-existing
    fun updateEntry(id: Int?, entryData: EntryData = EntryData()) {
        // if id is null, treat as a new entry
        if (id == null) {
            addEntry(null, entryData)
            return
        }

        if (_entries.containsKey(id)) {
            // create a fresh Entry instance and replace it in both the map and the SnapshotStateList
            val newEntry = Entry(entryData, id)
            _entries[id] = newEntry

            val index = entries.indexOfFirst { it.id == id }
            if (index == -1) {
                // fallback: add to list if missing
                entries.add(newEntry)
            } else {
                // replace element so SnapshotStateList notifies Compose observers
                entries[index] = newEntry
            }
        } else {
            addEntry(id, entryData)
        }
    }

    /**
     * WARNING: This only clears the in-memory entry.
     * Call through AnimeViewModel.deleteAnimeEntry() to also delete from database.
     */
    internal fun deleteEntry(id: Int?) {
        if (id == null) {
            return
        }
        _entries.remove(id)
        entries.removeAll { it.id == id }
        entriesVersion++
    }

    /**
     * WARNING: This only clears the in-memory entries.
     * Call through AnimeViewModel.deleteAllEntries() to also delete from database.
     */
    internal fun clearAllEntries() {
        _entries.clear()
        entries.clear()
        entriesVersion++
        lastId = 0
    }

    fun getValidEntryId(): Int {
        lastId = lastId + 1
        return lastId
    }

    fun getEntryDataById(id: Int?): EntryData? {
        return _entries.get(id)?.entryData ?: null
    }

    fun debugPrintIds() {
        //println("[EntriesController] Current entry ids: " + _entries.map { it.id })
    }
}
