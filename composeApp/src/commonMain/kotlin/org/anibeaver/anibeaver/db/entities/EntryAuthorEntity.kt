package org.anibeaver.anibeaver.db.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["entryId", "authorId"],
    foreignKeys = [
        ForeignKey(
            entity = AnimeEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["entryId"]), Index(value = ["authorId"])]
)

data class EntryAuthorEntity(
    val entryId: Int,
    val authorId: Int
)