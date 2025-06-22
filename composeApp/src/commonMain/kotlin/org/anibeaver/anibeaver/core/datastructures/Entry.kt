package org.anibeaver.anibeaver.core.datastructures

class Entry internal constructor(
    val animeName: String,
    val releaseYear: String,
    val studioIds: List<Int>,
    val genreIds: List<Int>,
    val description: String,
    val rating: Float,
    val status: String,
    val releasingEvery: String,
    val tagIds: List<Int>,
    internal val id: Int
) {
    fun getId(): Int = id
}
