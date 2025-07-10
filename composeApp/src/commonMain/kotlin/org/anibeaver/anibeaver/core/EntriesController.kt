package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.core.datastructures.Entry
import org.anibeaver.anibeaver.core.datastructures.EntryData
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.core.datastructures.Schedule

object EntriesController {
    private var nextId = 1
    private val _entries = mutableListOf(
        //TODO: if some tag doesn't exist in tagscontroller, remove its id from all entries
        // TODO: initial fill from some source of truth (database or anilist servers), comment out those placeholders
        Entry(EntryData(
            "Fullmetal Alchemist: Brotherhood",
            "2009",
            listOf(18), // Bones studio id
            listOf(7, 8, 9), // Action, Adventure, Fantasy genre ids
            "Epic alchemy adventure.",
            9.5f,
            Status.Completed,
            Schedule.Sunday,
            listOf(10, 11) // Shounen, Classic custom tag ids
        ),nextId++),
        Entry(EntryData(
            "Steins;Gate",
            "2011",
            listOf(15), // White Fox studio id
            listOf(12, 13), // Sci-Fi, Thriller genre ids
            "Time travel thriller.",
            9.0f,
            Status.Completed,
            Schedule.Wednesday,
            listOf(14, 13) // Time Travel, Thriller custom tag ids
            ),nextId++
        ),
        Entry(EntryData(
            "Your Lie in April",
            "2014",
            listOf(16), // A-1 Pictures studio id
            listOf(5, 6, 1), // Drama, Romance, Music genre ids
            "Emotional music drama.",
            8.8f,
            Status.Completed,
            Schedule.Friday,
            listOf(1, 6) // Music, Romance custom tag ids
            ),nextId++
        ),
        Entry(EntryData(
            "Attack on Titan",
            "2013",
            listOf(19), // Wit Studio studio id
            listOf(7, 5, 9), // Action, Drama, Fantasy genre ids
            "Humanity vs Titans.",
            9.2f,
            Status.Completed,
            Schedule.Sunday,
            listOf(23) // Dark custom tag id (corrected from 7, 21)
            ),nextId++
        ),
        Entry(EntryData(
            "K-On!",
            "2009",
            listOf(20), // Kyoto Animation studio id
            listOf(1, 22, 4), // Music, Slice of Life, Comedy genre ids
            "Cute girls play music.",
            8.0f,
            Status.Completed,
            Schedule.Thursday,
            listOf(22, 1) // Slice of Life, Music custom tag ids
            ),nextId++
        ),
        Entry(EntryData(
            "Tag Overload!",
            "2022",
            listOf(18, 15, 16, 19, 20, 21, 22, 23, 24, 25),
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 ,41, 42,43,44,45,46,47,48,49,50),
            "An anime with a ridiculous number of tags for testing UI overflow and performance.",
            7.7f,
            Status.Completed,
            Schedule.Monday,
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)
            ),nextId++
        )
    )
    val entries: SnapshotStateList<Entry> = mutableStateListOf()
    init{
        for (e in _entries) {
            entries.add(e)
        }
    }

    fun addEntry(id: Int? = null, entryData: EntryData){
        addEntry(Entry(entryData, id))
    }
    private fun addEntry(entry: Entry) {
        _entries.add(entry)
        entries.add(entry)
    }

    fun updateEntry(id: Int?, entryData: EntryData = EntryData()) {
        val index = _entries.indexOfFirst { it.id == id }
        if (index != -1) {
            _entries[index].entryData = entryData
            entries[index].entryData = entryData
            debugPrintIds()
        } else {
            addEntry(id, entryData)
        }
    }

    fun deleteEntry(id: Int?) {
        require(id!=null){"id was null"}
        _entries.removeAll { it.id == id }
        entries.removeAll {it.id == id}
        //debugPrintIds()
    }

    fun getEntryDataById(id: Int?): EntryData?{
        return _entries.firstOrNull{it.id==id}?.entryData ?: null
    }

    fun debugPrintIds() {
        //println("[EntriesController] Current entry ids: " + _entries.map { it.id })
    }
}
