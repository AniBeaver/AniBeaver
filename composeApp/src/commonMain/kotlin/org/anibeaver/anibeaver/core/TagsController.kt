package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.core.datastructures.Tag
import org.anibeaver.anibeaver.core.datastructures.TagType

object TagsController {
    private var nextId = 1
    private val _tags = mutableStateListOf<Tag>()
    val tags: SnapshotStateList<Tag> get() = _tags
    // TODO: initial fill from some source of truth (database or anilist servers), comment out those placeholders

    init {
        listOf(
            Tag("Music", "#FFB300", TagType.CUSTOM, nextId++),
            Tag("Intro", "#1976D2", TagType.CUSTOM, nextId++),
            Tag("Fight Scenes", "#D32F2F", TagType.CUSTOM, nextId++),
            Tag("Comedy", "#FBC02D", TagType.GENRE, nextId++),
            Tag("Drama", "#7B1FA2", TagType.GENRE, nextId++),
            Tag("Romance", "#C2185B", TagType.GENRE, nextId++),
            Tag("Action", "#FF0000", TagType.GENRE, nextId++),
            Tag("Adventure", "#00FF00", TagType.GENRE, nextId++),
            Tag("Fantasy", "#0000FF", TagType.GENRE, nextId++),
            Tag("Shounen", "#AAAAAA", TagType.CUSTOM, nextId++),
            Tag("Classic", "#BBBBBB", TagType.CUSTOM, nextId++),
            Tag("Sci-Fi", "#CCCCCC", TagType.GENRE, nextId++),
            Tag("Thriller", "#DDDDDD", TagType.GENRE, nextId++),
            Tag("Time Travel", "#EEEEEE", TagType.CUSTOM, nextId++),
            Tag("White Fox", "#123456", TagType.STUDIO, nextId++),
            Tag("A-1 Pictures", "#039BE5", TagType.STUDIO, nextId++),
            Tag("MAPPA", "#039BE5", TagType.STUDIO, nextId++),
            Tag("Madhouse", "#8E24AA", TagType.STUDIO, nextId++),
            Tag("Bones", "#654321", TagType.STUDIO, nextId++),
            Tag("Wit Studio", "#FEDCBA", TagType.STUDIO, nextId++),
            Tag("Kyoto Animation", "#ABCDEF", TagType.STUDIO, nextId++),
            Tag("Slice of Life", "#F0F0F0", TagType.GENRE, nextId++),
            Tag("Dark", "#111111", TagType.CUSTOM, nextId++),
            Tag("Music", "#FFB300", TagType.GENRE, nextId++)
        ).forEach { _tags.add(it) }
    }

    private fun debugPrint() {
        println("[TagsController] Current tag ids: " + _tags.map { it.getId() })
    }

    fun addTag(name: String, color: String, type: TagType): Int {
        val tag = Tag(name, color, type, nextId++)
        _tags.add(tag)
        debugPrint()
        return tag.getId()
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