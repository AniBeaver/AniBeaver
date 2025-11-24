package org.anibeaver.anibeaver.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.room.RoomDatabase
import org.koin.core.component.inject
import org.koin.core.component.KoinComponent

import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.EntryData
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.db.daos.TagDao
import org.anibeaver.anibeaver.db.entities.AnimeEntryEntity
import org.anibeaver.anibeaver.db.entities.EntryTagEntity
import org.anibeaver.anibeaver.db.entities.TagEntity
import org.anibeaver.anibeaver.db.getRoomDatabase
import org.anibeaver.anibeaver.db.AppDatabase

class AnimeViewModel (
) : ViewModel(), KoinComponent {
    private val databaseBuilder: RoomDatabase.Builder<AppDatabase> by inject()
    private val database = getRoomDatabase(databaseBuilder)
    val animeDao = database.getDao()
    private val tagDao: TagDao = database.tagDao()

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
            // Ensure all tags exist in the database first
            val allTagIds = entryData.tagIds + entryData.studioIds + entryData.authorIds + entryData.genreIds
            val tagsToUpsert = allTagIds.mapNotNull { tagId ->
                TagsController.tags.firstOrNull { it.id == tagId }?.let { tag ->
                    TagEntity(
                        id = tag.id,
                        name = tag.name,
                        color = tag.color,
                        type = tag.type
                    )
                }
            }
            if (tagsToUpsert.isNotEmpty()) {
                tagDao.upsertTags(tagsToUpsert)
            }

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
                    coverArtLocalPath = entryData.coverArt.localPath,
                    bannerArtSource = entryData.bannerArt.source,
                    bannerArtLocalPath = entryData.bannerArt.localPath,
                    customTagIds = entryData.tagIds,
                    studioTagIds = entryData.studioIds,
                    authorTagIds = entryData.authorIds,
                    genreTagIds = entryData.genreIds,
                    episodesTotal = entryData.episodesTotal,
                    episodesProgress = entryData.episodesProgress,
                    rewatches = entryData.rewatches,
                    type = entryData.type.id,
                )
            )

            tagDao.clearEntryTags(dbEntryId.toInt())
            val tagLinks = sequence {
                entryData.tagIds.forEach { yield(EntryTagEntity(dbEntryId.toInt(), it)) }
                entryData.studioIds.forEach { yield(EntryTagEntity(dbEntryId.toInt(), it)) }
                entryData.authorIds.forEach { yield(EntryTagEntity(dbEntryId.toInt(), it)) }
                entryData.genreIds.forEach { yield(EntryTagEntity(dbEntryId.toInt(), it)) }
            }.toList()
            tagDao.upsertEntryTags(tagLinks)

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