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
        10 to "FFD700", // yellow
        9 to "FFA500",  // orange
        8 to "CDFF00",  // lime
        7 to "7FFF00",  // chartreuse
        6 to "00FF00",  // green
        5 to "32CD32",  // lime green
        4 to "228B22",  // forest green
        3 to "006400",  // dark green
        2 to "008B8B",  // dark cyan
        1 to "4682B4"   // steel blue
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
            settings = json.decodeFromString<Settings>(jsonString)
            println("[SettingsController] Settings loaded successfully")
        } catch (e: Exception) {
            println("[SettingsController] Failed to load settings: ${e.message}")
            // Keep default settings
        }
    }

    suspend fun saveSettings() {
        try {
            createSettingsDir()
            val jsonString = json.encodeToString(settings)
            settingsFile.write(jsonString.toByteArray())
            println("[SettingsController] Settings saved successfully")
        } catch (e: Exception) {
            println("[SettingsController] Failed to save settings: ${e.message}")
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

