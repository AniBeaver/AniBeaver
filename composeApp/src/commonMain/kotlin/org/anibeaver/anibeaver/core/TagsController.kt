package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.datastructures.Tag

object TagsController {
    private var nextId = 1
    private val _tags = mutableStateListOf(
        Tag("Music", "#FFB300", nextId++),
        Tag("Intro", "#1976D2", nextId++),
        Tag("Fight Scenes", "#D32F2F", nextId++),
        Tag("Visuals", "#388E3C", nextId++),
        Tag("Comedy", "#FBC02D", nextId++),
        Tag("Drama", "#7B1FA2", nextId++),
        Tag("Romance", "#C2185B", nextId++)
    )
    val tags: SnapshotStateList<Tag> get() = _tags

    fun addTag(name: String, color: String) {
        _tags.add(Tag(name, color, nextId++))
    }

    fun removeTagById(id: Int) {
        _tags.removeAll { it.getId() == id }
    }

    fun clear() {
        _tags.clear()
    }
}