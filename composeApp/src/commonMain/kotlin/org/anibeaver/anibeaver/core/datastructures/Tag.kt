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
    id: Int 
) {
    internal val id: Int = id
        get() = field
    
    companion object {
        private val idToTypeCache = mutableMapOf<Int, TagType>()
        fun getTypeById(id: Int): TagType? {
            idToTypeCache[id]?.let { return it }
            val type = TagsController.tags.find { it.id == id }?.type
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