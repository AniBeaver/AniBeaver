package org.anibeaver.anibeaver.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.ExportController
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.EntryData
import org.anibeaver.anibeaver.core.datastructures.FilterData
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.db.AppDatabase
import org.anibeaver.anibeaver.db.daos.TagDao
import org.anibeaver.anibeaver.db.entities.AnimeEntryEntity
import org.anibeaver.anibeaver.db.entities.EntryTagEntity
import org.anibeaver.anibeaver.db.entities.ReferenceEntity
import org.anibeaver.anibeaver.db.entities.TagEntity
import org.anibeaver.anibeaver.db.getRoomDatabase
import org.anibeaver.anibeaver.ui.modals.defaultFilterData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AnimeViewModel(
) : ViewModel(), KoinComponent {
    private val databaseBuilder: RoomDatabase.Builder<AppDatabase> by inject()
    private val database = getRoomDatabase(databaseBuilder)
    val animeDao = database.getDao()
    private val tagDao: TagDao = database.tagDao()
    private val referenceDao = database.referenceDao()

    val entryController = EntriesController

    private val _animeFilterData = MutableStateFlow<FilterData?>(defaultFilterData)
    val animeFilterData: StateFlow<FilterData?> = _animeFilterData

    private val _mangaFilterData = MutableStateFlow<FilterData?>(defaultFilterData)
    val mangaFilterData: StateFlow<FilterData?> = _mangaFilterData

    private val _animeCollapsedStatuses = MutableStateFlow<Set<Int>>(emptySet())
    val animeCollapsedStatuses: StateFlow<Set<Int>> = _animeCollapsedStatuses

    private val _mangaCollapsedStatuses = MutableStateFlow<Set<Int>>(emptySet())
    val mangaCollapsedStatuses: StateFlow<Set<Int>> = _mangaCollapsedStatuses

    fun updateAnimeFilterData(filterData: FilterData?) {
        _animeFilterData.value = filterData
    }

    fun updateMangaFilterData(filterData: FilterData?) {
        _mangaFilterData.value = filterData
    }

    fun updateAnimeCollapsedStatuses(statuses: Set<Int>) {
        _animeCollapsedStatuses.value = statuses
    }

    fun updateMangaCollapsedStatuses(statuses: Set<Int>) {
        _mangaCollapsedStatuses.value = statuses
    }

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

            entryController.addEntry(
                entryId.toInt(), EntryData(
                    title = "Plastic Memories",
                    episodesProgress = 12,
                    episodesTotal = 24,
                )
            )
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

            referenceDao.deleteByEntryId(dbEntryId.toInt())
            val referenceEntities = entryData.references.mapIndexed { index, ref ->
                ReferenceEntity(
                    entryId = dbEntryId.toInt(),
                    note = ref.note,
                    anilistId = ref.alId,
                    name = ref.name,
                    orderIndex = index,
                    isPriority = ref.isPriority
                )
            }
            if (referenceEntities.isNotEmpty()) {
                referenceDao.insertAll(referenceEntities)
            }

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

    fun deleteTag(tagId: Int) {
        viewModelScope.launch {
            val tag = tagDao.getTagById(tagId)
            if (tag != null) {
                tagDao.deleteTag(tag)
            }
        }
    }

    fun updateTagType(tagId: Int, oldType: TagType, newType: TagType) {
        viewModelScope.launch {
            tagDao.upsertTag(TagEntity(
                id = tagId,
                name = TagsController.tags.first { it.id == tagId }.name,
                color = TagsController.tags.first { it.id == tagId }.color,
                type = newType
            ))

            val allEntries = animeDao.getAll()
            allEntries.forEach { entry ->
                val affectedEntry = when (oldType) {
                    TagType.CUSTOM -> tagId in entry.customTagIds
                    TagType.GENRE -> tagId in entry.genreTagIds
                    TagType.STUDIO -> tagId in entry.studioTagIds
                    TagType.AUTHOR -> tagId in entry.authorTagIds
                }

                if (affectedEntry) {
                    val updatedEntry = entry.copy(
                        customTagIds = when {
                            oldType == TagType.CUSTOM -> entry.customTagIds - tagId
                            newType == TagType.CUSTOM -> entry.customTagIds + tagId
                            else -> entry.customTagIds
                        },
                        genreTagIds = when {
                            oldType == TagType.GENRE -> entry.genreTagIds - tagId
                            newType == TagType.GENRE -> entry.genreTagIds + tagId
                            else -> entry.genreTagIds
                        },
                        studioTagIds = when {
                            oldType == TagType.STUDIO -> entry.studioTagIds - tagId
                            newType == TagType.STUDIO -> entry.studioTagIds + tagId
                            else -> entry.studioTagIds
                        },
                        authorTagIds = when {
                            oldType == TagType.AUTHOR -> entry.authorTagIds - tagId
                            newType == TagType.AUTHOR -> entry.authorTagIds + tagId
                            else -> entry.authorTagIds
                        }
                    )
                    animeDao.upsert(updatedEntry)
                }
            }
        }
    }

    fun deleteAllEntries() {
        viewModelScope.launch {
            animeDao.deleteAll()
            EntriesController.clearAllEntries()
        }
    }

    //TODO: is this all (IMPORT/EXPORT funct) really supposed to be here? Maybe ExportController instead
    suspend fun exportEntries(): String? {
        val entries = animeDao.getAll()
        val referencesMap = entries.associate { entry ->
            entry.id to referenceDao.getByEntryId(entry.id)
        }
        val tags = tagDao.getAllTags()

        return ExportController.exportToFile(entries, referencesMap, tags)
    }

    suspend fun createBackup() {
        val entries = animeDao.getAll()
        val referencesMap = entries.associate { entry ->
            entry.id to referenceDao.getByEntryId(entry.id)
        }
        val tags = tagDao.getAllTags()

        ExportController.createBackup(entries, referencesMap, tags)
    }

    suspend fun importEntries(): Result<Unit> {
        val importResult = ExportController.importFromFile()
        if (importResult.isFailure) {
            return Result.failure(importResult.exceptionOrNull()!!)
        }

        val exportData = importResult.getOrNull()!!

        return try {
            animeDao.deleteAll()
            EntriesController.clearAllEntries()

            val tags = ExportController.convertTagsFromExportData(exportData.tags)
            if (tags.isNotEmpty()) {
                tagDao.upsertTags(tags)
            }

            val (entries, referencesMap) = ExportController.convertFromExportData(exportData)

            entries.forEach { entry ->
                importSingleEntry(entry, referencesMap[entry.id] ?: emptyList())
            }

            reloadTagsAndEntries()

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            val stackTrace = e.stackTraceToString()
                .lines()
                .filter { !it.contains("org.anibeaver.anibeaver.core") && !it.contains("org.anibeaver.anibeaver.ui") }
                .take(10)
                .joinToString("\n")
            Result.failure(Exception("${e.message}\n\nStack trace:\n$stackTrace"))
        }
    }

    private suspend fun reloadTagsAndEntries() {
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

        val entryRelations = tagDao.getEntriesWithTags()
        val tagsByEntry = entryRelations.associateBy { it.entry.id }
        val entries = animeDao.getAll()

        for (entry in entries) {
            if (EntriesController.entries.any { it.id == entry.id }) continue
            val relation = tagsByEntry[entry.id]
            val references = referenceDao.getByEntryId(entry.id).map {
                println("[DEBUG] Loading reference: note='${it.note}', alId='${it.anilistId}', name='${it.name}'")
                org.anibeaver.anibeaver.core.datastructures.Reference(
                    note = it.note,
                    alId = it.anilistId,
                    name = it.name,
                    isPriority = it.isPriority
                )
            }
            EntriesController.addEntry(
                entry.id,
                EntryData(
                    title = entry.animeName,
                    releaseYear = entry.releaseYear,
                    studioIds = relation?.tagsByType(org.anibeaver.anibeaver.core.datastructures.TagType.STUDIO)
                        ?.map { it.id } ?: emptyList(),
                    authorIds = relation?.tagsByType(org.anibeaver.anibeaver.core.datastructures.TagType.AUTHOR)
                        ?.map { it.id } ?: emptyList(),
                    genreIds = relation?.tagsByType(org.anibeaver.anibeaver.core.datastructures.TagType.GENRE)
                        ?.map { it.id } ?: emptyList(),
                    description = entry.description,
                    rating = entry.rating,
                    status = Status.fromId(entry.status) ?: EntryData().status,
                    releasingEvery = org.anibeaver.anibeaver.core.datastructures.ReleaseSchedule.fromId(entry.releasingEvery)
                        ?: EntryData().releasingEvery,
                    tagIds = relation?.tagsByType(org.anibeaver.anibeaver.core.datastructures.TagType.CUSTOM)
                        ?.map { it.id } ?: emptyList(),
                    coverArt = org.anibeaver.anibeaver.core.datastructures.Art(
                        source = entry.coverArtSource,
                        localPath = entry.coverArtLocalPath
                    ),
                    bannerArt = org.anibeaver.anibeaver.core.datastructures.Art(
                        source = entry.bannerArtSource,
                        localPath = entry.bannerArtLocalPath
                    ),
                    episodesTotal = entry.episodesTotal,
                    episodesProgress = entry.episodesProgress,
                    rewatches = entry.rewatches,
                    type = org.anibeaver.anibeaver.core.datastructures.EntryType.fromId(entry.type) ?: EntryData().type,
                    references = references
                )
            )
        }
    }

    private suspend fun importSingleEntry(entry: AnimeEntryEntity, references: List<ReferenceEntity>) {
        val entryId = animeDao.upsert(entry)

        val tagLinks = buildTagLinks(entry, entryId.toInt())
        if (tagLinks.isNotEmpty()) {
            tagDao.upsertEntryTags(tagLinks)
        }

        if (references.isNotEmpty()) {
            referenceDao.insertAll(references.map { it.copy(entryId = entryId.toInt()) })
        }
    }

    private fun buildTagLinks(entry: AnimeEntryEntity, entryId: Int): List<EntryTagEntity> {
        return sequence {
            entry.customTagIds.forEach { yield(EntryTagEntity(entryId, it)) }
            entry.studioTagIds.forEach { yield(EntryTagEntity(entryId, it)) }
            entry.authorTagIds.forEach { yield(EntryTagEntity(entryId, it)) }
            entry.genreTagIds.forEach { yield(EntryTagEntity(entryId, it)) }
        }.toList()
    }
}