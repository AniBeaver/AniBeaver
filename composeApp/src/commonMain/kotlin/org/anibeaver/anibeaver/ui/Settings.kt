package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.anibeaver.anibeaver.core.ImageController
import org.anibeaver.anibeaver.core.SettingsController
import org.anibeaver.anibeaver.core.Settings
import org.anibeaver.anibeaver.ui.components.basic.IntPicker
import org.anibeaver.anibeaver.ui.components.ColorPicker
import org.anibeaver.anibeaver.ui.components.showAlert
import org.anibeaver.anibeaver.ui.components.showConfirmation
import org.anibeaver.anibeaver.ui.theme.Typography

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: AnimeViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val currentSettings = SettingsController.settings

    var backupInterval by remember { mutableStateOf(currentSettings.backupIntervalMinutes) }
    var maxBackups by remember { mutableStateOf(currentSettings.maxBackupsToKeep) }
    var ratingColors by remember { mutableStateOf(currentSettings.ratingColors) }

    LaunchedEffect(currentSettings) {
        backupInterval = currentSettings.backupIntervalMinutes
        maxBackups = currentSettings.maxBackupsToKeep
        ratingColors = currentSettings.ratingColors
    }

    fun updateSettings() {
        SettingsController.updateSettings(
            Settings(
                backupIntervalMinutes = backupInterval,
                maxBackupsToKeep = maxBackups,
                ratingColors = ratingColors
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Settings", style = Typography.headlineLarge)

        Spacer(modifier = Modifier.height(8.dp))

        // Data Management
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                ImageController.cleanUpImagesDir()
                showAlert("Cleaned up unused images!")
            }) {
                Text("Clean Up Images")
            }

            Button(onClick = {
                coroutineScope.launch {
                    val result = viewModel.exportEntries()
                    if (result != null) {
                        showAlert("Entries exported successfully!")
                    } else {
                        showAlert("Export cancelled or failed.")
                    }
                }
            }) {
                Text("Export Entries")
            }

            Button(onClick = {
                if (viewModel.entryController.entries.isEmpty()) {
                    coroutineScope.launch {
                        val success = viewModel.importEntries()
                        if (success) {
                            showAlert("Entries imported successfully!")
                        } else {
                            showAlert("Failed to import entries.")
                        }
                    }
                } else {
                    showConfirmation(
                        message = "This will delete ALL entries and import new ones. Continue?",
                        onAccept = {
                            showConfirmation(
                                message = "Final warning! All entries will be deleted. Continue?",
                                onAccept = {
                                    coroutineScope.launch {
                                        viewModel.deleteAllEntries()
                                        val success = viewModel.importEntries()
                                        if (success) {
                                            showAlert("Entries imported!")
                                        } else {
                                            showAlert("Failed to import.")
                                        }
                                    }
                                }
                            )
                        }
                    )
                }
            }) {
                Text("Import Entries")
            }

            Button(onClick = {
                if (viewModel.entryController.entries.isEmpty()) {
                    showAlert("No entries to delete.")
                } else {
                    showConfirmation(
                        message = "Delete ALL entries? Cannot be undone!",
                        onAccept = {
                            showConfirmation(
                                message = "FINAL WARNING! All entries will be deleted!",
                                onAccept = {
                                    viewModel.deleteAllEntries()
                                    showAlert("All entries deleted.")
                                }
                            )
                        }
                    )
                }
            }) {
                Text("Delete All Entries")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Auto Backup
        Card {
            Row(
                Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IntPicker(
                    value = backupInterval,
                    onValueChange = {
                        backupInterval = it
                        updateSettings()
                    },
                    onIncrement = {
                        backupInterval = (backupInterval + 1).coerceAtMost(1440)
                        updateSettings()
                    },
                    onDecrement = {
                        backupInterval = (backupInterval - 1).coerceAtLeast(1)
                        updateSettings()
                    },
                    label = "Backup Interval (minutes)",
                    min = 1,
                    max = 1440,
                    modifier = Modifier.width(200.dp)
                )

                IntPicker(
                    value = maxBackups,
                    onValueChange = {
                        maxBackups = it
                        updateSettings()
                    },
                    onIncrement = {
                        maxBackups = (maxBackups + 1).coerceAtMost(100)
                        updateSettings()
                    },
                    onDecrement = {
                        maxBackups = (maxBackups - 1).coerceAtLeast(1)
                        updateSettings()
                    },
                    label = "Max Backups to Keep",
                    min = 1,
                    max = 100,
                    modifier = Modifier.width(200.dp)
                )

                Button(onClick = {
                    coroutineScope.launch {
                        viewModel.createBackup()
                        showAlert("Backup created successfully!")
                    }
                }) {
                    Text("Create Backup Now")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rating Colors
        Card {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Rating Colors", style = Typography.titleMedium)

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    maxItemsInEachRow = 3
                ) {
                    listOf(
                        10 to "9.1-10",
                        9 to "8.1-9",
                        8 to "7.1-8",
                        7 to "6.1-7",
                        6 to "5.1-6",
                        5 to "4.1-5",
                        4 to "3.1-4",
                        3 to "2.1-3",
                        2 to "1.1-2",
                        1 to "0.1-1",
                        0 to "No rating"
                    ).forEach { (rating, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.width(290.dp)
                        ) {
                            Text(label, modifier = Modifier.width(60.dp))
                            ColorPicker(
                                hex = "#${ratingColors[rating] ?: "FFFFFF"}",
                                onHexChange = { newHex ->
                                    val cleanHex = newHex.removePrefix("#")
                                    ratingColors = ratingColors + (rating to cleanHex)
                                    updateSettings()
                                },
                                modifier = Modifier.width(220.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

