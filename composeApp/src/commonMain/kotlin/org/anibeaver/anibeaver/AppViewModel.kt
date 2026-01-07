package org.anibeaver.anibeaver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.RoomDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.ExportController
import org.anibeaver.anibeaver.core.SettingsController
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.*
import org.anibeaver.anibeaver.db.AppDatabase
import org.anibeaver.anibeaver.db.daos.TagDao
import org.anibeaver.anibeaver.db.getRoomDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppViewModel(
) : ViewModel(), KoinComponent {
    private val databaseBuilder: RoomDatabase.Builder<AppDatabase> by inject()
    private val database = getRoomDatabase(databaseBuilder)
    private val animeDao = database.getDao()
    private val tagDao: TagDao = database.tagDao()
    private val referenceDao = database.referenceDao()
    val entryController = EntriesController
    val entries = entryController.entries

    init {
        viewModelScope.launch {
            SettingsController.loadSettings()
            loadTags()
            getAnimeEntries()
            performAutoBackup()
            startPeriodicBackup()
        }
    }

    private suspend fun loadTags() {
        TagsController.clear()
        val tags = tagDao.getAllTags()
        TagsController.addAllTags(tags.map { tag ->
            Tag(
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
            val references = referenceDao.getByEntryId(entry.id).map {
                Reference(
                    note = it.note,
                    alId = it.anilistId,
                    name = it.name,
                    isPriority = it.isPriority
                )
            }
            entryController.addEntry(
                entry.id,
                EntryData(
                    title = entry.animeName,
                    releaseYear = entry.releaseYear,
                    studioIds = relation?.tagsByType(TagType.STUDIO)
                        ?.map { it.id }
                        ?: entry.studioTagIds,
                    authorIds = relation?.tagsByType(TagType.AUTHOR)
                        ?.map { it.id }
                        ?: entry.authorTagIds,
                    genreIds = relation?.tagsByType(TagType.GENRE)
                        ?.map { it.id }
                        ?: entry.genreTagIds,
                    description = entry.description,
                    rating = entry.rating,
                    status = Status.fromId(entry.status) ?: EntryData().status,
                    releasingEvery = ReleaseSchedule.fromId(entry.releasingEvery) ?: EntryData().releasingEvery,
                    tagIds = relation?.tagsByType(TagType.CUSTOM)
                        ?.map { it.id }
                        ?: entry.customTagIds,
                    coverArt = Art(source = entry.coverArtSource, localPath = entry.coverArtLocalPath),
                    bannerArt = Art(source = entry.bannerArtSource, localPath = entry.bannerArtLocalPath),
                    episodesTotal = entry.episodesTotal,
                    episodesProgress = entry.episodesProgress,
                    rewatches = entry.rewatches,
                    type = EntryType.fromId(entry.type) ?: EntryData().type,
                    references = references
                )
            )
        }
    }

    private suspend fun performAutoBackup() {
        try {
            val entries = animeDao.getAll()
            val referencesMap = entries.associate { entry ->
                entry.id to referenceDao.getByEntryId(entry.id)
            }
            val tags = tagDao.getAllTags()

            ExportController.autoBackupIfNeeded(entries, referencesMap, tags)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startPeriodicBackup() {
        viewModelScope.launch {
            while (isActive) {
                val intervalMinutes = SettingsController.getBackupIntervalMinutes()
                delay(intervalMinutes * 60 * 1000L)
                performAutoBackup()
            }
        }
    }
}