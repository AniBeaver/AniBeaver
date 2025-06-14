package org.anibeaver.anibeaver.controller

import org.anibeaver.anibeaver.model.Entry
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.setValue
import org.anibeaver.anibeaver.controller.EntriesController

data class AnimeCard(val id: Int, val name: String, val labels: String, val description: String)

object CardsController {
    private val _cards = mutableStateListOf<AnimeCard>()
    val cards: SnapshotStateList<AnimeCard> get() = _cards

    init {
        syncWithEntries()
    }

    fun syncWithEntries() {
        _cards.clear()
        EntriesController.entries.forEach { entry ->
            _cards.add(
                AnimeCard(
                    id = entry.id,
                    name = entry.animeName,
                    labels = entry.genre,
                    description = entry.description
                )
            )
        }
    }
}
