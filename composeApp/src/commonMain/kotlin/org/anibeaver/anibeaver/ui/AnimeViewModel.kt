package org.anibeaver.anibeaver.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.datastructures.EntryData
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.db.entities.AnimeEntryEntity
import org.anibeaver.anibeaver.db.getRoomDatabase

class AnimeViewModel(
    dataWrapper: DataWrapper
) : ViewModel() {
    val animeDao = getRoomDatabase(dataWrapper.databaseBuilder).getDao()

    val entryController = EntriesController

    fun saveAnimeEntry() {
        viewModelScope.launch {
            val entryId = animeDao.upsert(
                AnimeEntryEntity(
                    animeName = "Plastic Memories",
                    episodesProgress = 12,
                    episodesTotal = 24,
                    status = Status.Watching.id
                )
            )

            entryController.addEntry(entryId.toInt(), EntryData(
                title = "Plastic Memories",
                episodesProgress = 12,
                episodesTotal = 24,
            ))
        }
    }

    fun upsertAnimeEntry(entryId: Int?, entryData: EntryData) {
        viewModelScope.launch {
            val dbEntryId = animeDao.upsert(
                AnimeEntryEntity(
                    id = entryId ?: 0,
                    animeName = entryData.title ?: "",
                    releaseYear = entryData.releaseYear,
                    description = entryData.description,
                    rating = entryData.rating,
                    status = entryData.status.id,
                    releasingEvery = entryData.releasingEvery.id,
                    coverArtSource = entryData.coverArt.source,
                    coverArtLocalPath = entryData.coverArt.local_path,
                    bannerArtSource = entryData.bannerArt.source,
                    bannerArtLocalPath = entryData.bannerArt.local_path,
                    episodesTotal = entryData.episodesTotal,
                    episodesProgress = entryData.episodesProgress,
                    rewatches = entryData.rewatches,
                    type = entryData.type.id,
                )
            )

            EntriesController.updateEntry(dbEntryId.toInt(), entryData)
            EntriesController.entriesVersion++ // resort
        }
    }

    fun deleteAnimeEntry(entryId: Int?) {
        if (entryId == null) return

        viewModelScope.launch {
            animeDao.deleteById(entryId.toLong())
            EntriesController.deleteEntry(entryId)
        }
    }
}