package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.core.datastructures.Art
import org.anibeaver.anibeaver.core.datastructures.Entry
import org.anibeaver.anibeaver.core.datastructures.EntryData
import org.anibeaver.anibeaver.core.datastructures.Reference
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.core.datastructures.Schedule

object EntriesController {
    private var lastId = 0

    //TODO: if some tag doesn't exist in tagscontroller, remove its id from all entries
    // TODO: initial fill from some source of truth (database or anilist servers), comment out those placeholders
    private val _entries = HashMap<Int, Entry>()
    val entries: SnapshotStateList<Entry> = mutableStateListOf()
    init{
        addEntry(Entry(EntryData(
            "Fullmetal Alchemist: Brotherhood",
            "2009",
            listOf(18), // Bones studio id
            listOf(7, 8, 9), // Action, Adventure, Fantasy genre ids
            "Epic alchemy adventure.",
            9.5f,
            Status.Completed,
            Schedule.Sunday,
            listOf(10, 11), // Shounen, Classic custom tag ids
            references = listOf(
                Reference("Season 1", "5114")
            ),
            coverArt = Art("", ""),
            bannerArt = Art("", ""),
            episodesTotal = 64,
            episodesProgress = 64,
            rewatches = 1
        )))
        addEntry(Entry(EntryData(
            "Steins;Gate",
            "2011",
            listOf(15), // White Fox studio id
            listOf(12, 13), // Sci-Fi, Thriller genre ids
            "Time travel thriller.",
            9.0f,
            Status.Completed,
            Schedule.Wednesday,
            listOf(14, 13), // Time Travel, Thriller custom tag ids
            references = listOf(
                Reference("Main", "9253")
            ),
            coverArt = Art("", ""),
            bannerArt = Art("", ""),
            episodesTotal = 24,
            episodesProgress = 24,
            rewatches = 1
        )))
        addEntry(Entry(EntryData(
            "Your Lie in April",
            "2014",
            listOf(16), // A-1 Pictures studio id
            listOf(5, 6, 1), // Drama, Romance, Music genre ids
            "Emotional music drama.",
            8.8f,
            Status.Completed,
            Schedule.Friday,
            listOf(1, 6), // Music, Romance custom tag ids
            references = listOf(
                Reference("Main", "20954")
            ),
            coverArt = Art("", ""),
            bannerArt = Art("", ""),
            episodesTotal = 22,
            episodesProgress = 22,
            rewatches = 1
        )))
        addEntry(Entry(EntryData(
            "Attack on Titan",
            "2013",
            listOf(19), // Wit Studio studio id
            listOf(7, 5, 9), // Action, Drama, Fantasy genre ids
            "Humanity vs Titans.",
            9.2f,
            Status.Completed,
            Schedule.Sunday,
            listOf(23), // Dark custom tag id (corrected from 7, 21)
            references = listOf(
                Reference("Season 1", "16498"),
                Reference("Season 2", "20958")
            ),
            coverArt = Art("", ""),
            bannerArt = Art("", ""),
            episodesTotal = 0,
            episodesProgress = 0,
            rewatches = 0
        )))
        addEntry(Entry(EntryData(
            "K-On!",
            "2009",
            listOf(20), // Kyoto Animation studio id
            listOf(1, 22, 4), // Music, Slice of Life, Comedy genre ids
            "Cute girls play music.",
            8.0f,
            Status.Completed,
            Schedule.Thursday,
            listOf(22, 1), // Slice of Life, Music custom tag ids
            references = emptyList(),
            coverArt = Art("", ""),
            bannerArt = Art("", ""),
            episodesTotal = 0,
            episodesProgress = 0,
            rewatches = 0
        )))
        addEntry(Entry(EntryData(
            "Tag Overload!",
            "2022",
            listOf(18, 15, 16, 19, 20, 21, 22, 23, 24, 25),
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40 ,41, 42,43,44,45,46,47,48,49,50),
            "An anime with a ridiculous number of tags for testing UI overflow and performance.",
            7.7f,
            Status.Completed,
            Schedule.Monday,
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
            references = emptyList(),
            coverArt = Art("", ""),
            bannerArt = Art("", ""),
            episodesTotal = 0,
            episodesProgress = 0,
            rewatches = 0
        )))
    }

    fun addEntry(id: Int? = null, entryData: EntryData){
        addEntry(Entry(entryData, id))
    }
    private fun addEntry(entry: Entry) {
        _entries.put(entry.id, entry)
        entries.add(entry)
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

    fun deleteEntry(id: Int?) {
        if(id == null){
            return
        }
        _entries.remove(id)
        entries.removeAll {it.id == id}
        //debugPrintIds()
    }

    fun getValidEntryId() : Int{
        lastId = lastId+1
        return lastId
    }

    fun getEntryDataById(id: Int?): EntryData?{
        return _entries.get(id)?.entryData ?: null
    }

    fun debugPrintIds() {
        //println("[EntriesController] Current entry ids: " + _entries.map { it.id })
    }
}
