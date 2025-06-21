package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.datastructures.Tag

object TagsController {
    private var nextId = 1
    private val _tags = mutableStateListOf<Tag>()
    val tags: SnapshotStateList<Tag> get() = _tags

    init {
        listOf(
            //TODO: again, placeholder values
            Tag("Music", "#FFB300", nextId++),
            Tag("Intro", "#1976D2", nextId++),
            Tag("Fight Scenes", "#D32F2F", nextId++),
            Tag("Comedy", "#FBC02D", nextId++),
            Tag("Drama", "#7B1FA2", nextId++),
            Tag("Romance", "#C2185B", nextId++)
        ).forEach { _tags.add(it) }
    }

    private fun debugPrint() {
        println("[TagsController] Current tag ids: " + _tags.map { it.getId() })
    }

    fun addTag(name: String, color: String) {
        _tags.add(Tag(name, color, nextId++))
        debugPrint()
    }

    fun removeTagById(id: Int) {
        _tags.removeAll { it.getId() == id }
        debugPrint()
    }

    fun clear() {
        _tags.clear()
    }

    fun updateTag(id: Int, name: String, color: String) {
        val index = _tags.indexOfFirst { it.getId() == id }
        if (index != -1) {
            _tags[index] = Tag(name, color, id)
            debugPrint()
        }
    }
}