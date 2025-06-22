package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.datastructures.Entry
import org.anibeaver.anibeaver.datastructures.Tag
import org.anibeaver.anibeaver.datastructures.TagType

object EntriesController {
    private var nextId = 1
    private val _entries = mutableStateListOf(
        // TODO: initial fill from some source of truth (database or anilist servers), comment out those placeholders
        Entry(
            "Fullmetal Alchemist: Brotherhood",
            "2009",
            18, // Bones studio id
            listOf(7, 8, 9), // Action, Adventure, Fantasy genre ids
            "Epic alchemy adventure.",
            9.5f,
            "Finished",
            "Sunday",
            listOf(10, 11), // Shounen, Classic custom tag ids
            nextId++
        ),
        Entry(
            "Steins;Gate",
            "2011",
            15, // White Fox studio id
            listOf(12, 13), // Sci-Fi, Thriller genre ids
            "Time travel thriller.",
            9.0f,
            "Finished",
            "Wednesday",
            listOf(14, 13), // Time Travel, Thriller custom tag ids
            nextId++
        ),
        Entry(
            "Your Lie in April",
            "2014",
            16, // A-1 Pictures studio id
            listOf(5, 6, 1), // Drama, Romance, Music genre ids
            "Emotional music drama.",
            8.8f,
            "Finished",
            "Friday",
            listOf(1, 6), // Music, Romance custom tag ids
            nextId++
        ),
        Entry(
            "Attack on Titan",
            "2013",
            19, // Wit Studio studio id
            listOf(7, 5, 9), // Action, Drama, Fantasy genre ids
            "Humanity vs Titans.",
            9.2f,
            "Finished",
            "Sunday",
            listOf(7, 21), // Action, Dark custom tag ids
            nextId++
        ),
        Entry(
            "K-On!",
            "2009",
            20, // Kyoto Animation studio id
            listOf(1, 22, 4), // Music, Slice of Life, Comedy genre ids
            "Cute girls play music.",
            8.0f,
            "Finished",
            "Thursday",
            listOf(22, 1), // Slice of Life, Music custom tag ids
            nextId++
        )
    )
    val entries: SnapshotStateList<Entry> get() = _entries

    fun packEntry(
        animeName: String,
        releaseYear: String,
        studioId: Int,
        genreIds: List<Int>,
        description: String,
        rating: Float,
        status: String,
        releasingEvery: String,
        tagIds: List<Int>
    ): Entry {
        return Entry(animeName, releaseYear, studioId, genreIds, description, rating, status, releasingEvery, tagIds, nextId++)
    }

    fun addEntry(entry: Entry) {
        _entries.add(entry)
        debugPrintIds()
    }

    fun updateEntry(entry: Entry) {
        val index = _entries.indexOfFirst { it.getId() == entry.getId() }
        if (index != -1) {
            _entries[index] = entry
            debugPrintIds()
        } else {
            println("No entry to update with id ${entry.getId()} found")
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
