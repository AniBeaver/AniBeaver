package org.anibeaver.anibeaver.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.anibeaver.anibeaver.core.datastructures.TagType

@Entity
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String,
    val type: TagType
)

