package org.anibeaver.anibeaver.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AnimeEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryId: Int,
    val name: String,
    val anilistId: String
)
