package org.anibeaver.anibeaver.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.vinceglb.filekit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Settings(
    val backupIntervalMinutes: Int = 8,
    val maxBackupsToKeep: Int = 6,
    val ratingColors: Map<Int, String> = mapOf(
        10 to "ffff00",
        9 to "e6bb00",
        8 to "99ff33",
        7 to "3bdb3b",
        6 to "069c06",
        5 to "008B8B",
        4 to "4682B4",
        3 to "4169E1",
        2 to "1E90FF",
        1 to "87CEEB",
        0 to "660066",
        -1 to "FFFFFF"
    )
)

object SettingsController {
    private val filesDir = FileKit.filesDir
    private val settingsDir = filesDir / "settings"
    private val settingsFile = settingsDir / "settings.json"

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    var settings by mutableStateOf(Settings())
        private set

    private fun createSettingsDir() {
        FileController.createDirectory(settingsDir)
    }

    suspend fun loadSettings() {
        try {
            if (!settingsFile.exists()) {
                // Create default settings file
                saveSettings()
                return
            }

            val jsonString = settingsFile.readBytes().decodeToString()
            val loadedSettings = json.decodeFromString(Settings.serializer(), jsonString)

            // Merge loaded settings with defaults to ensure all keys are present
            val defaultRatingColors = Settings().ratingColors
            val mergedRatingColors = defaultRatingColors + loadedSettings.ratingColors

            settings = loadedSettings.copy(ratingColors = mergedRatingColors)
            println("[SettingsController] Settings loaded successfully")
        } catch (e: Exception) {
            println("[SettingsController] Failed to load settings: ${e.message}")
            e.printStackTrace()
            // Keep default settings
        }
    }

    suspend fun saveSettings() {
        try {
            createSettingsDir()
            val jsonString = json.encodeToString(Settings.serializer(), settings)
            settingsFile.write(jsonString.toByteArray())
            println("[SettingsController] Settings saved successfully")
        } catch (e: Exception) {
            println("[SettingsController] Failed to save settings: ${e.message}")
            e.printStackTrace()
        }
    }

    fun updateSettings(newSettings: Settings) {
        settings = newSettings
        CoroutineScope(Dispatchers.IO).launch {
            saveSettings()
        }
    }

    fun getBackupIntervalMinutes(): Int = settings.backupIntervalMinutes
    fun getMaxBackupsToKeep(): Int = settings.maxBackupsToKeep
    fun getRatingColor(rating: Int): String? = settings.ratingColors[rating]
}

