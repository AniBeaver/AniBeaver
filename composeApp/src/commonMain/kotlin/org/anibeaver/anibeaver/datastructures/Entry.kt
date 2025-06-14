package org.anibeaver.anibeaver.datastructures

//TODO: potentially tags to String[]
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
    internal val id: Int
) {
    fun getId(): Int = id
}
