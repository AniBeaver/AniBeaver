package org.anibeaver.anibeaver.model

// Entry is now a class with id assigned only by EntriesController
class Entry internal constructor(
    val animeName: String,
    val releaseYear: String,
    val studioName: String,
    val genre: String,
    val description: String,
    val rating: Float,
    val status: String,
    val releasingEvery: String,
    val tags: String,
    internal val id: Int // Only EntriesController can set this
) {
    // Public getter for id, but no public setter
    fun getId(): Int = id
}
