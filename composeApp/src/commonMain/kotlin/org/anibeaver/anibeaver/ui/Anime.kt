package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.ui.components.EntryCard
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.anibeaver.anibeaver.Screens
import androidx.compose.foundation.layout.BoxWithConstraints
import kotlin.math.max

@Composable
@Preview
fun AnimeScreen(
    navController: NavHostController = rememberNavController()
) {
    var showPopup by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<org.anibeaver.anibeaver.datastructures.Entry?>(null) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val cardWidth = 350.dp
        val cardSpacing = 12.dp
        val totalWidth = maxWidth
        val columns = max(1, ((totalWidth + cardSpacing) / (cardWidth + cardSpacing)).toInt())

        // Buttons
        Column(Modifier.fillMaxSize()) {
            Text("Anime", style = Typography.headlineLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController.navigate(Screens.Home.name) }) { Text("Go to Home") }
                Button(onClick = {
                    editingEntry = null
                    showPopup = true
                }) { Text("New Entry") }
                Button(onClick = {
                    val entry = EntriesController.packEntry(
                        animeName = "Placeholder Anime",
                        releaseYear = "2025",
                        studioName = "Placeholder Studio",
                        genre = "Genre",
                        description = "This is a placeholder entry.",
                        rating = 8.5f,
                        status = "Unknown",
                        releasingEvery = "Never",
                        tags = "placeholder, ph"
                    )
                    EntriesController.addEntry(entry)
                }) { Text("Add Placeholder Entry") }
            }
            Spacer(Modifier.height(16.dp))
            if (showPopup) {
                EditEntryPopup(
                    show = showPopup,
                    onDismiss = { showPopup = false },
                    onConfirm = { entryData ->
                        if (editingEntry == null) {
                            val entry = EntriesController.packEntry(
                                animeName = entryData.animeName,
                                releaseYear = entryData.releaseYear,
                                studioName = entryData.studioName,
                                genre = entryData.genre,
                                description = entryData.description,
                                rating = entryData.rating,
                                status = entryData.status,
                                releasingEvery = entryData.releasingEvery,
                                tags = entryData.tags
                            )
                            EntriesController.addEntry(entry)
                        } else {
                            val updatedEntry = org.anibeaver.anibeaver.datastructures.Entry(
                                animeName = entryData.animeName,
                                releaseYear = entryData.releaseYear,
                                studioName = entryData.studioName,
                                genre = entryData.genre,
                                description = entryData.description,
                                rating = entryData.rating,
                                status = entryData.status,
                                releasingEvery = entryData.releasingEvery,
                                tags = entryData.tags,
                                id = editingEntry!!.getId()
                            )
                            EntriesController.updateEntry(updatedEntry)
                        }
                        showPopup = false
                    },
                    initialEntry = editingEntry
                )
            }

            // Grid
            EntriesController.entries.chunked(columns).forEach { rowEntries ->
                Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(cardSpacing)) {

                    Spacer(Modifier.width(cardSpacing))

                    rowEntries.forEach { entry ->
                        EntryCard(
                            id = entry.getId(),
                            name = entry.animeName,
                            tags = listOfNotNull(entry.genre, entry.releaseYear, entry.studioName)
                                .plus(entry.tags?.split(",") ?: emptyList())
                                .joinToString(", "),
                            description = entry.description,
                            onEdit = {
                                editingEntry = entry
                                showPopup = true
                            },
                            onDelete = {
                                EntriesController.removeEntryById(entry.getId())
                            }
                        )
                        Spacer(Modifier.width(cardSpacing))
                    }
                    repeat(columns - rowEntries.size) { Spacer(Modifier.width(cardWidth + cardSpacing)) }
                }
            }
        }
    }
}