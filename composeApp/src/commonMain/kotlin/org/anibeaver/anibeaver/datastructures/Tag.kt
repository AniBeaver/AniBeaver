package org.anibeaver.anibeaver.datastructures

class Tag(
    val name: String,
    val color: String,
    internal val id: Int
) {
    fun getId(): Int = id
}

