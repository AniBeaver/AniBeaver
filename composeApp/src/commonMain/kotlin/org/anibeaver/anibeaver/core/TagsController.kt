package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.datastructures.Tag
import org.anibeaver.anibeaver.datastructures.TagType

object TagsController {
    private var nextId = 1
    private val _tags = mutableStateListOf<Tag>()
    val tags: SnapshotStateList<Tag> get() = _tags

    init {
        listOf(
            //TODO: again, placeholder values
            Tag("Music", "#FFB300", TagType.CUSTOM, nextId++),
            Tag("Intro", "#1976D2", TagType.CUSTOM, nextId++),
            Tag("Fight Scenes", "#D32F2F", TagType.CUSTOM, nextId++),
            Tag("Comedy", "#FBC02D", TagType.GENRE, nextId++),
            Tag("Drama", "#7B1FA2", TagType.GENRE, nextId++),
            Tag("Romance", "#C2185B", TagType.GENRE, nextId++),
            Tag("MAPPA", "#43A047", TagType.STUDIO, nextId++),
            Tag("A-1 Pictures", "#039BE5", TagType.STUDIO, nextId++),
            Tag("Madhouse", "#8E24AA", TagType.STUDIO, nextId++)
        ).forEach { _tags.add(it) }
    }

    private fun debugPrint() {
        println("[TagsController] Current tag ids: " + _tags.map { it.getId() })
    }

    fun addTag(name: String, color: String, type: TagType) {
        _tags.add(Tag(name, color, type, nextId++))
        debugPrint()
    }

    fun removeTagById(id: Int) {
        _tags.removeAll { it.getId() == id }
        debugPrint()
    }

    fun clear() {
        _tags.clear()
    }

    fun updateTag(id: Int, name: String, color: String, type: TagType) {
        val index = _tags.indexOfFirst { it.getId() == id }
        if (index != -1) {
            _tags[index] = Tag(name, color, type, id)
            debugPrint()
        }
    }
}