package org.anibeaver.anibeaver.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["entryId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = AnimeEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["entryId"]), Index(value = ["genreId"])]
)
data class EntryGenreEntity(
    val entryId: Int,
    val genreId: Int
)
