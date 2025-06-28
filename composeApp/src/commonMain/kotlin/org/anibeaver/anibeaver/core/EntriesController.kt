package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.core.datastructures.Entry
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.core.datastructures.Schedule

object EntriesController {
    private var nextId = 1
    private val _entries = mutableStateListOf(
        //TODO: if some tag doesn't exist in tagscontroller, remove its id from all entries
        // TODO: initial fill from some source of truth (database or anilist servers), comment out those placeholders
        Entry(
            "Fullmetal Alchemist: Brotherhood",
            "2009",
            listOf(18), // Bones studio id
            listOf(7, 8, 9), // Action, Adventure, Fantasy genre ids
            "Epic alchemy adventure.",
            9.5f,
            Status.Finished,
            Schedule.Sunday,
            listOf(10, 11), // Shounen, Classic custom tag ids
            nextId++
        ),
        Entry(
            "Steins;Gate",
            "2011",
            listOf(15), // White Fox studio id
            listOf(12, 13), // Sci-Fi, Thriller genre ids
            "Time travel thriller.",
            9.0f,
            Status.Finished,
            Schedule.Wednesday,
            listOf(14, 13), // Time Travel, Thriller custom tag ids
            nextId++
        ),
        Entry(
            "Your Lie in April",
            "2014",
            listOf(16), // A-1 Pictures studio id
            listOf(5, 6, 1), // Drama, Romance, Music genre ids
            "Emotional music drama.",
            8.8f,
            Status.Finished,
            Schedule.Friday,
            listOf(1, 6), // Music, Romance custom tag ids
            nextId++
        ),
        Entry(
            "Attack on Titan",
            "2013",
            listOf(19), // Wit Studio studio id
            listOf(7, 5, 9), // Action, Drama, Fantasy genre ids
            "Humanity vs Titans.",
            9.2f,
            Status.Finished,
            Schedule.Sunday,
            listOf(23), // Dark custom tag id (corrected from 7, 21)
            nextId++
        ),
        Entry(
            "K-On!",
            "2009",
            listOf(20), // Kyoto Animation studio id
            listOf(1, 22, 4), // Music, Slice of Life, Comedy genre ids
            "Cute girls play music.",
            8.0f,
            Status.Finished,
            Schedule.Thursday,
            listOf(22, 1), // Slice of Life, Music custom tag ids
            nextId++
        ),
        Entry(
            "Tag Overload!",
            "2022",
            listOf(18, 15, 16, 19, 20, 21, 22, 23, 24, 25),
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
            "An anime with a ridiculous number of tags for testing UI overflow and performance.",
            7.7f,
            Status.Finished,
            Schedule.Monday,
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
            nextId++
        )
    )
    val entries: SnapshotStateList<Entry> get() = _entries

    fun packEntry(
        animeName: String,
        releaseYear: String,
        studioIds: List<Int>,
        genreIds: List<Int>,
        description: String,
        rating: Float,
        status: Status,
        releasingEvery: Schedule,
        tagIds: List<Int>
    ): Entry {
        return Entry(animeName, releaseYear, studioIds, genreIds, description, rating, status, releasingEvery, tagIds, nextId++)
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
