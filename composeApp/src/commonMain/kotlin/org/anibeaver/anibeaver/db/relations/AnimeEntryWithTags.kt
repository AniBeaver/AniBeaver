package org.anibeaver.anibeaver.db.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.db.entities.AnimeEntryEntity
import org.anibeaver.anibeaver.db.entities.EntryTagEntity
import org.anibeaver.anibeaver.db.entities.TagEntity

data class AnimeEntryWithTags(
    @Embedded
    val entry: AnimeEntryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EntryTagEntity::class,
            parentColumn = "entryId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
) {
    fun tagsByType(type: TagType): List<TagEntity> = tags.filter { it.type == type }
}
