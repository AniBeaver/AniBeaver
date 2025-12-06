package org.anibeaver.anibeaver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.room.RoomDatabase
import org.koin.core.component.inject
import org.koin.core.component.KoinComponent

import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.Art
import org.anibeaver.anibeaver.core.datastructures.EntryData
import org.anibeaver.anibeaver.core.datastructures.EntryType
import org.anibeaver.anibeaver.core.datastructures.ReleaseSchedule
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.db.daos.TagDao
import org.anibeaver.anibeaver.db.getRoomDatabase
import org.anibeaver.anibeaver.db.AppDatabase

class AppViewModel(
) : ViewModel(), KoinComponent  {
    private val databaseBuilder: RoomDatabase.Builder<AppDatabase> by inject()
    private val database = getRoomDatabase(databaseBuilder)
    private val animeDao = database.getDao()
    private val tagDao: TagDao = database.tagDao()
    val entryController = EntriesController
    val entries = entryController.entries

    init {
        viewModelScope.launch {
            loadTags()
            getAnimeEntries()
        }
    }

    private suspend fun loadTags() {
        TagsController.clear()
        val tags = tagDao.getAllTags()
        TagsController.addAllTags(tags.map { tag ->
            org.anibeaver.anibeaver.core.datastructures.Tag(
                name = tag.name,
                color = tag.color,
                type = tag.type,
                id = tag.id
            )
        })
    }

    private suspend fun getAnimeEntries() {
        val entryRelations = tagDao.getEntriesWithTags()
        val tagsByEntry = entryRelations.associateBy { it.entry.id }
        val entries = animeDao.getAll()

        for (entry in entries) {
            if (entryController.entries.any { it.id == entry.id }) continue
            val relation = tagsByEntry[entry.id]
            entryController.addEntry(
                entry.id,
                EntryData(
                    title = entry.animeName,
                    releaseYear = entry.releaseYear,
                    studioIds = relation?.tagsByType(org.anibeaver.anibeaver.core.datastructures.TagType.STUDIO)?.map { it.id }
                        ?: entry.studioTagIds,
                    authorIds = relation?.tagsByType(org.anibeaver.anibeaver.core.datastructures.TagType.AUTHOR)?.map { it.id }
                        ?: entry.authorTagIds,
                    genreIds = relation?.tagsByType(org.anibeaver.anibeaver.core.datastructures.TagType.GENRE)?.map { it.id }
                        ?: entry.genreTagIds,
                    description = entry.description,
                    rating = entry.rating,
                    status = Status.fromId(entry.status) ?: EntryData().status,
                    releasingEvery = ReleaseSchedule.fromId(entry.releasingEvery) ?: EntryData().releasingEvery,
                    tagIds = relation?.tagsByType(org.anibeaver.anibeaver.core.datastructures.TagType.CUSTOM)?.map { it.id }
                        ?: entry.customTagIds,
                    coverArt = Art(source = entry.coverArtSource, localPath = entry.coverArtLocalPath),
                    bannerArt = Art(source = entry.bannerArtSource, localPath = entry.bannerArtLocalPath),
                    episodesTotal = entry.episodesTotal,
                    episodesProgress = entry.episodesProgress,
                    rewatches = entry.rewatches,
                    type = EntryType.fromId(entry.type) ?: EntryData().type
                )
            )
        }
    }
}