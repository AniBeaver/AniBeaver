package org.anibeaver.anibeaver.core.datastructures

import org.anibeaver.anibeaver.core.TagsController

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

    companion object {
        private val idToTypeCache = mutableMapOf<Int, TagType>()
        fun getTypeById(id: Int): TagType? {
            idToTypeCache[id]?.let { return it }
            val type = TagsController.tags.find { it.getId() == id }?.type
            if (type != null) {
                idToTypeCache[id] = type
            }
            return type
        }
        fun clearTypeCache() {
            idToTypeCache.clear()
        }
    }
}