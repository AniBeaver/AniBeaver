package org.anibeaver.anibeaver.core.datastructures

enum class TagType {
    CUSTOM,
    GENRE,
    STUDIO
}

class Tag(
    val name: String,
    val color: String,
    val type: TagType,
    internal val id: Int
) {
    fun getId(): Int = id
}