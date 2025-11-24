package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.anibeaver.anibeaver.core.datastructures.Tag
import org.anibeaver.anibeaver.core.datastructures.TagType

object TagsController {
    private var nextId = 1
    private val _tags = mutableStateListOf<Tag>()
    val tags: SnapshotStateList<Tag> get() = _tags

    init {
        // Tags are loaded from the database via AppViewModel.loadTags()
    }

    private fun debugPrint() {
        //println("[TagsController] Current tag ids: " + _tags.map { it.id })
    }

    fun getTagIdByNameAndType(name: String, type: TagType): Int {
        return _tags.find { it.name == name && it.type == type }?.id ?: -1
    }

    fun addTag(name: String, color: String, type: TagType, idOverride: Int? = null): Int {
        val assignedId = idOverride ?: nextId
        if (idOverride != null) {
            nextId = maxOf(nextId, idOverride + 1)
        } else {
            nextId++
        }
        val tag = Tag(name, color, type, assignedId)
        _tags.add(tag)
        debugPrint()
        return tag.id
    }

    fun addAllTags(tags: Collection<Tag>) {
        tags.forEach { tag ->
            if (_tags.any { it.id == tag.id }) return@forEach
            _tags.add(tag)
            nextId = maxOf(nextId, tag.id + 1)
        }
    }

    fun safeCreateByName(name: String, color: String, type: TagType): Int {
        val existing = _tags.firstOrNull { it.name.equals(name, ignoreCase = true) && it.type == type }
        return existing?.id ?: addTag(name, color, type)
    }

    fun removeTagById(id: Int) {
        _tags.removeAll { it.id == id }
        debugPrint()
    }

    fun clear() {
        _tags.clear()
        nextId = 1
        Tag.clearTypeCache()
    }

    fun updateTag(id: Int, name: String, color: String, type: TagType) {
        val index = _tags.indexOfFirst { it.id == id }
        if (index != -1) {
            _tags[index] = Tag(name, color, type, id)
            debugPrint()
        }
    }
}