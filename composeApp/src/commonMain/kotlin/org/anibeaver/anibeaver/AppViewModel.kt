package org.anibeaver.anibeaver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.room.RoomDatabase
import org.koin.core.component.inject
import org.koin.core.component.KoinComponent

import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.datastructures.EntryData
import org.anibeaver.anibeaver.core.datastructures.EntryType
import org.anibeaver.anibeaver.core.datastructures.ReleaseSchedule
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.db.getRoomDatabase
import org.anibeaver.anibeaver.db.AppDatabase

class AppViewModel(
) : ViewModel(), KoinComponent  {
    private val databaseBuilder: RoomDatabase.Builder<AppDatabase> by inject()
    val animeDao = getRoomDatabase(databaseBuilder).getDao()
    val entryController = EntriesController
    val entries = entryController.entries

    init {
        getAnimeEntries()
    }

    fun getAnimeEntries() {
        viewModelScope.launch {
            val entries = animeDao.getAll()

            for (entry in entries) {
                println("Loaded entry from DB: $entry")
                if (entryController.entries.any { it.id == entry.id }) continue

                entryController.addEntry(entry.id,
                    EntryData(
                        title = entry.animeName,
                        releaseYear = entry.releaseYear,
                        description = entry.description,
                        rating = entry.rating,
                        status = Status.fromId(entry.status) ?: EntryData().status,
                        releasingEvery = ReleaseSchedule.fromId(entry.releasingEvery) ?: EntryData().releasingEvery,
                        coverArt = EntryData().coverArt.copy(),
                        bannerArt = EntryData().bannerArt.copy(),
                        episodesTotal = entry.episodesTotal,
                        episodesProgress = entry.episodesProgress,
                        rewatches = entry.rewatches,
                        type = EntryType.fromId(entry.type) ?: EntryData().type
                    )
                )
            }
        }
    }
}