package org.anibeaver.anibeaver.core

import io.github.vinceglb.filekit.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.anibeaver.anibeaver.core.datastructures.TagType
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
    val note: String,
    val anilistId: String,
    val name: String = "",
    val orderIndex: Int,
    val isPriority: Boolean = false
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
        encodeDefaults = true
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
                        note = ref.note,
                        anilistId = ref.anilistId,
                        name = ref.name,
                        orderIndex = ref.orderIndex,
                        isPriority = ref.isPriority
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
                    note = ref.note,
                    anilistId = ref.anilistId,
                    name = ref.name,
                    orderIndex = ref.orderIndex,
                    isPriority = ref.isPriority
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

    suspend fun importFromFile(): Result<ExportData> {
        val file = FileController.chooseFile(listOf("json"))
            ?: return Result.failure(Exception("No file selected"))

        return try {
            val jsonString = readJsonFromFile(file)
            val data = importFromJson(jsonString)
            Result.success(data)
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMessage = e.message ?: e.toString()
            val simplifiedMessage = errorMessage
                .replace("org.anibeaver.anibeaver.core.", "")
                .replace("org.anibeaver.anibeaver.", "")
                .replace("kotlinx.serialization.", "")
            Result.failure(Exception(simplifiedMessage))
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
        return hasContentChanged(entries, referencesMap, tags)
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

