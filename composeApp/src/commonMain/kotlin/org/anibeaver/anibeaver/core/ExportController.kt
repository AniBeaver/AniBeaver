package org.anibeaver.anibeaver.core

import io.github.vinceglb.filekit.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.anibeaver.anibeaver.core.datastructures.*
import org.anibeaver.anibeaver.db.entities.AnimeEntryEntity
import org.anibeaver.anibeaver.db.entities.ReferenceEntity
import org.anibeaver.anibeaver.db.entities.TagEntity

@Serializable
data class ExportData(
    val entries: List<ExportEntry>,
    val tags: List<ExportTag>,
    val version: Int = 1
)

@Serializable
data class ExportEntry(
    val id: Int,
    val title: String,
    val releaseYear: String,
    val description: String,
    val rating: Float,
    val status: Int,
    val releasingEvery: Int,
    val coverArtSource: String,
    val coverArtLocalPath: String,
    val bannerArtSource: String,
    val bannerArtLocalPath: String,
    val customTagIds: List<Int>,
    val studioTagIds: List<Int>,
    val authorTagIds: List<Int>,
    val genreTagIds: List<Int>,
    val episodesTotal: Int,
    val episodesProgress: Int,
    val rewatches: Int,
    val type: Int,
    val references: List<ExportReference>
)

@Serializable
data class ExportReference(
    val name: String,
    val anilistId: String,
    val orderIndex: Int
)

@Serializable
data class ExportTag(
    val id: Int,
    val name: String,
    val color: String,
    val type: String
)

object ExportController {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val backupsDir = FileKit.filesDir / "backups"
    private val lastBackupHashFile = backupsDir / ".last_backup_hash"

    suspend fun writeJsonToFile(file: PlatformFile, jsonString: String) {
        FileController.textToFile(file, jsonString)
    }

    suspend fun readJsonFromFile(file: PlatformFile): String {
        return FileController.textToFile(file)
    }

    fun convertToExportData(
        entries: List<AnimeEntryEntity>,
        references: Map<Int, List<ReferenceEntity>>,
        tags: List<TagEntity>
    ): ExportData {
        val exportEntries = entries.map { entry ->
            ExportEntry(
                id = entry.id,
                title = entry.animeName,
                releaseYear = entry.releaseYear,
                description = entry.description,
                rating = entry.rating,
                status = entry.status,
                releasingEvery = entry.releasingEvery,
                coverArtSource = entry.coverArtSource,
                coverArtLocalPath = entry.coverArtLocalPath,
                bannerArtSource = entry.bannerArtSource,
                bannerArtLocalPath = entry.bannerArtLocalPath,
                customTagIds = entry.customTagIds,
                studioTagIds = entry.studioTagIds,
                authorTagIds = entry.authorTagIds,
                genreTagIds = entry.genreTagIds,
                episodesTotal = entry.episodesTotal,
                episodesProgress = entry.episodesProgress,
                rewatches = entry.rewatches,
                type = entry.type,
                references = references[entry.id]?.map { ref ->
                    ExportReference(
                        name = ref.name,
                        anilistId = ref.anilistId,
                        orderIndex = ref.orderIndex
                    )
                } ?: emptyList()
            )
        }

        val exportTags = tags.map { tag ->
            ExportTag(
                id = tag.id,
                name = tag.name,
                color = tag.color,
                type = tag.type.name
            )
        }

        return ExportData(
            entries = exportEntries,
            tags = exportTags
        )
    }

    fun exportToJson(exportData: ExportData): String {
        return json.encodeToString(exportData)
    }

    fun importFromJson(jsonString: String): ExportData {
        return json.decodeFromString<ExportData>(jsonString)
    }

    fun convertFromExportData(
        exportData: ExportData
    ): Pair<List<AnimeEntryEntity>, Map<Int, List<ReferenceEntity>>> {
        val entries = exportData.entries.map { exportEntry ->
            AnimeEntryEntity(
                id = exportEntry.id,
                animeName = exportEntry.title,
                releaseYear = exportEntry.releaseYear,
                description = exportEntry.description,
                rating = exportEntry.rating,
                status = exportEntry.status,
                releasingEvery = exportEntry.releasingEvery,
                coverArtSource = exportEntry.coverArtSource,
                coverArtLocalPath = exportEntry.coverArtLocalPath,
                bannerArtSource = exportEntry.bannerArtSource,
                bannerArtLocalPath = exportEntry.bannerArtLocalPath,
                customTagIds = exportEntry.customTagIds,
                studioTagIds = exportEntry.studioTagIds,
                authorTagIds = exportEntry.authorTagIds,
                genreTagIds = exportEntry.genreTagIds,
                episodesTotal = exportEntry.episodesTotal,
                episodesProgress = exportEntry.episodesProgress,
                rewatches = exportEntry.rewatches,
                type = exportEntry.type
            )
        }

        val referencesMap = exportData.entries.associate { exportEntry ->
            exportEntry.id to exportEntry.references.map { ref ->
                ReferenceEntity(
                    entryId = exportEntry.id,
                    name = ref.name,
                    anilistId = ref.anilistId,
                    orderIndex = ref.orderIndex
                )
            }
        }

        return Pair(entries, referencesMap)
    }

    fun convertTagsFromExportData(exportTags: List<ExportTag>): List<TagEntity> {
        return exportTags.map { exportTag ->
            TagEntity(
                id = exportTag.id,
                name = exportTag.name,
                color = exportTag.color,
                type = TagType.valueOf(exportTag.type)
            )
        }
    }

    suspend fun exportToFile(
        entries: List<AnimeEntryEntity>,
        referencesMap: Map<Int, List<ReferenceEntity>>,
        tags: List<TagEntity>
    ): String? {
        val exportData = convertToExportData(entries, referencesMap, tags)
        val jsonString = exportToJson(exportData)

        val directory = FileController.chooseDirectory() ?: return null
        val fileName = "anibeaver_export_${System.currentTimeMillis()}.json"
        val file = directory / fileName

        writeJsonToFile(file, jsonString)
        return file.path
    }

    suspend fun importFromFile(): ExportData? {
        val file = FileController.chooseFile(listOf("json")) ?: return null

        return try {
            val jsonString = readJsonFromFile(file)
            importFromJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createBackupsDir() {
        FileController.createDirectory(backupsDir)
    }

    private fun getBackupFiles(): List<PlatformFile> {
        return FileController.listFiles(backupsDir) { file ->
            file.name.startsWith("backup_") && file.name.endsWith(".json")
        }.sortedBy { it.name }
    }

    private fun deleteOldestBackup(backupFiles: List<PlatformFile>) {
        if (backupFiles.isNotEmpty()) {
            FileController.deleteFile(backupFiles.first())
        }
    }

    suspend fun createBackup(
        entries: List<AnimeEntryEntity>,
        referencesMap: Map<Int, List<ReferenceEntity>>,
        tags: List<TagEntity>
    ) {
        println("Backup made")
        createBackupsDir()

        val backupFiles = getBackupFiles()
        val maxBackups = SettingsController.getMaxBackupsToKeep()
        if (backupFiles.size >= maxBackups) {
            deleteOldestBackup(backupFiles)
        }

        val exportData = convertToExportData(entries, referencesMap, tags)
        val jsonString = exportToJson(exportData)

        val fileName = "backup_${System.currentTimeMillis()}.json"
        val file = backupsDir / fileName

        writeJsonToFile(file, jsonString)
        saveContentHash(jsonString)
    }

    private suspend fun getLastBackupHash(): String? {
        return try {
            if (lastBackupHashFile.exists()) {
                FileController.textToFile(lastBackupHashFile)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun saveContentHash(jsonString: String) {
        try {
            createBackupsDir()
            val hash = jsonString.hashCode().toString()
            FileController.textToFile(lastBackupHashFile, hash)
        } catch (_: Exception) {
        }
    }

    private suspend fun hasContentChanged(
        entries: List<AnimeEntryEntity>,
        referencesMap: Map<Int, List<ReferenceEntity>>,
        tags: List<TagEntity>
    ): Boolean {
        val exportData = convertToExportData(entries, referencesMap, tags)
        val jsonString = exportToJson(exportData)
        val currentHash = jsonString.hashCode().toString()
        val lastHash = getLastBackupHash()

        return lastHash == null || currentHash != lastHash
    }

    private suspend fun shouldCreateAutoBackup(
        entries: List<AnimeEntryEntity>,
        referencesMap: Map<Int, List<ReferenceEntity>>,
        tags: List<TagEntity>
    ): Boolean {
        if (!hasContentChanged(entries, referencesMap, tags)) {
            return false
        }

        return true
    }

    suspend fun autoBackupIfNeeded(
        entries: List<AnimeEntryEntity>,
        referencesMap: Map<Int, List<ReferenceEntity>>,
        tags: List<TagEntity>
    ) {
        if (shouldCreateAutoBackup(entries, referencesMap, tags)) {
            createBackup(entries, referencesMap, tags)
        }
    }
}

