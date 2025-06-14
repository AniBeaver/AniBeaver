package org.anibeaver.anibeaver.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.setValue

data class AnimeCard(val id: Int, val name: String, val labels: String)

object CardsController {
    // Use a state list and expose it as a public property
    private val _cards = mutableStateListOf(
        AnimeCard(1, "Fullmetal Alchemist: Brotherhood", "Action, Adventure, Fantasy"),
        AnimeCard(2, "Steins;Gate", "Sci-Fi, Thriller"),
        AnimeCard(3, "Your Lie in April", "Drama, Romance, Music"),
        AnimeCard(4, "Attack on Titan", "Action, Drama, Fantasy"),
        AnimeCard(5, "K-On!", "Music, Slice of Life, Comedy")
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
