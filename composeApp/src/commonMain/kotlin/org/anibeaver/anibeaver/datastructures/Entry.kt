package org.anibeaver.anibeaver.datastructures

class Entry internal constructor(
    val animeName: String,
    val releaseYear: String,
    val studioId: Int, // Tag id for studio
    val genreIds: List<Int>, // Tag ids for genres
    val description: String,
    val rating: Float,
    val status: String,
    val releasingEvery: String,
    val tagIds: List<Int>, // Tag ids for custom tags
    internal val id: Int
) {
    fun getId(): Int = id
}
