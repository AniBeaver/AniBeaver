package org.anibeaver.anibeaver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.datastructures.EntryData
import org.anibeaver.anibeaver.db.getRoomDatabase

class AppViewModel(
    val dataWrapper: DataWrapper
) : ViewModel() {
    val animeDao = getRoomDatabase(dataWrapper.databaseBuilder).getDao()
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
                        status = EntryData().status.fromString(entry.status) ?: EntryData().status,
                        releasingEvery = EntryData().releasingEvery.fromString(entry.releasingEvery) ?: EntryData().releasingEvery,
                        coverArt = EntryData().coverArt.copy(),
                        bannerArt = EntryData().bannerArt.copy(),
                        episodesTotal = entry.episodesTotal,
                        episodesProgress = entry.episodesProgress,
                        rewatches = entry.rewatches,
                        //type = EntryData().type.fromString(entry.type) ?: EntryData().type
                    )
                )
            }
        }
    }
}