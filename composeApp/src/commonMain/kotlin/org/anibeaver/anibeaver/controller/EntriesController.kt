package org.anibeaver.anibeaver.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.model.Entry

object EntriesController {
    private var nextId = 1
    private val _entries = mutableStateListOf(
        Entry("Fullmetal Alchemist: Brotherhood", "2009", "Bones", "Action, Adventure, Fantasy", "Epic alchemy adventure.", 9.5f, "Finished", "Sunday", "Shounen, Classic", nextId++),
        Entry("Steins;Gate", "2011", "White Fox", "Sci-Fi, Thriller", "Time travel thriller.", 9.0f, "Finished", "Wednesday", "Time Travel, Thriller", nextId++),
        Entry("Your Lie in April", "2014", "A-1 Pictures", "Drama, Romance, Music", "Emotional music drama.", 8.8f, "Finished", "Friday", "Music, Romance", nextId++),
        Entry("Attack on Titan", "2013", "Wit Studio", "Action, Drama, Fantasy", "Humanity vs Titans.", 9.2f, "Finished", "Sunday", "Action, Dark", nextId++),
        Entry("K-On!", "2009", "Kyoto Animation", "Music, Slice of Life, Comedy", "Cute girls play music.", 8.0f, "Finished", "Thursday", "Slice of Life, Music", nextId++)
    )
    val entries: SnapshotStateList<Entry> get() = _entries

    fun createEntry(
        animeName: String,
        releaseYear: String,
        studioName: String,
        genre: String,
        description: String,
        rating: Float,
        status: String,
        releasingEvery: String,
        tags: String
    ): Entry {
        return Entry(animeName, releaseYear, studioName, genre, description, rating, status, releasingEvery, tags, nextId++)
    }

    fun addEntry(entry: Entry) {
        _entries.add(entry)
        debugPrintIds()
    }

    fun updateEntry(index: Int, entry: Entry) {
        if (index in _entries.indices) {
            _entries[index] = entry
            debugPrintIds()
        }
    }

    fun updateEntryById(id: Int, data: Entry) {
        val idx = _entries.indexOfFirst { it.getId() == id }
        if (idx != -1) {
            _entries[idx] = Entry(
                data.animeName,
                data.releaseYear,
                data.studioName,
                data.genre,
                data.description,
                data.rating,
                data.status,
                data.releasingEvery,
                data.tags,
                id
            )
            debugPrintIds()
        }
    }

    fun removeEntryById(id: Int) {
        _entries.removeAll { it.getId() == id }
        debugPrintIds()
    }

    fun clear() {
        _entries.clear()
        debugPrintIds()
    }

    fun debugPrintIds() {
        println("[EntriesController] Current entry ids: " + _entries.map { it.getId() })
    }
}
