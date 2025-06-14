package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import org.anibeaver.anibeaver.controller.EditEntryController
import org.anibeaver.anibeaver.controller.CardsController
import org.anibeaver.anibeaver.controller.AnimeCard
import org.anibeaver.anibeaver.ui.components.EntryCard
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.ui.theme.Typography

@Composable
@Preview
fun AnimeScreen(
    navController: NavHostController = rememberNavController()
) {
    var showPopup by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<org.anibeaver.anibeaver.model.Entry?>(null) }
    // Use CardsController.cards directly, do not create a local copy
    Column(Modifier.fillMaxSize()) {
        Text("Anime", style = Typography.headlineLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { navController.navigate(Screens.Home.name) }) { Text("Go to Home") }
            Button(onClick = {
                editingEntry = null
                showPopup = true
            }) { Text("New Entry") }
            Button(onClick = {
                val entry = org.anibeaver.anibeaver.controller.EntriesController.createEntry(
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
                org.anibeaver.anibeaver.controller.EntriesController.addEntry(entry)
                org.anibeaver.anibeaver.controller.CardsController.syncWithEntries()
            }) { Text("Add Placeholder Entry") }
            Button(onClick = {
                org.anibeaver.anibeaver.controller.CardsController.syncWithEntries()
            }) { Text("Sync") }
        }
        Spacer(Modifier.height(16.dp))
        if (showPopup) {
            EditEntryPopup(
                show = showPopup,
                onDismiss = { showPopup = false },
                onConfirm = { entryData ->
                    if (editingEntry == null) {
                        val entry = org.anibeaver.anibeaver.controller.EntriesController.createEntry(
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
                        org.anibeaver.anibeaver.controller.EntriesController.addEntry(entry)
                    } else {
                        org.anibeaver.anibeaver.controller.EntriesController.updateEntryById(
                            editingEntry!!.getId(),
                            entryData
                        )
                    }
                    org.anibeaver.anibeaver.controller.CardsController.syncWithEntries()
                    showPopup = false
                },
                initialEntry = editingEntry
            )
        }
        CardsController.cards.chunked(3).forEach { rowCards ->
            Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowCards.forEach { card ->
                    EntryCard(
                        id = card.id,
                        name = card.name,
                        labels = card.labels,
                        description = card.description,
                        onEdit = {
                            val entry = org.anibeaver.anibeaver.controller.EntriesController.entries.find { it.getId() == card.id }
                            if (entry != null) {
                                editingEntry = entry
                                showPopup = true
                            }
                        },
                        onDelete = {
                            org.anibeaver.anibeaver.controller.EntriesController.removeEntryById(card.id)
                            org.anibeaver.anibeaver.controller.CardsController.syncWithEntries()
                        }
                    )
                    Spacer(Modifier.width(12.dp))
                }
                repeat(3 - rowCards.size) { Spacer(Modifier.width(432.dp)) }
            }
        }
    }
}