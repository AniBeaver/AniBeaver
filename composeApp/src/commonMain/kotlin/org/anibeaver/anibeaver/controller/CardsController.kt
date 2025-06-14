package org.anibeaver.anibeaver.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.setValue

data class AnimeCard(val name: String, val labels: String)

object CardsController {
    // Use a state list and expose it as a public property
    private val _cards = mutableStateListOf(
        AnimeCard("Fullmetal Alchemist: Brotherhood", "Action, Adventure, Fantasy"),
        AnimeCard("Steins;Gate", "Sci-Fi, Thriller"),
        AnimeCard("Your Lie in April", "Drama, Romance, Music"),
        AnimeCard("Attack on Titan", "Action, Drama, Fantasy"),
        AnimeCard("K-On!", "Music, Slice of Life, Comedy")
    )
    val cards: SnapshotStateList<AnimeCard> get() = _cards

    fun addCard(card: AnimeCard) {
        _cards.add(card)
    }

    fun removeCard(card: AnimeCard) {
        _cards.remove(card)
    }

    fun clear() {
        _cards.clear()
    }
}
