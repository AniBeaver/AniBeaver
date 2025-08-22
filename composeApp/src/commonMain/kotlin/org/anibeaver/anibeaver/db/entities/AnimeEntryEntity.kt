package org.anibeaver.anibeaver.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import org.anibeaver.anibeaver.core.datastructures.Schedule
import org.anibeaver.anibeaver.core.datastructures.Status


@Entity
data class AnimeEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val animeName: String = "",
    val releaseYear: String = "2000",

    // FIXME: extract to separate tables
    // val studioIds: List<Int> = emptyList(),
    // val genreIds: List<Int> = emptyList(),

    val description: String = "",
    val rating: Float = 0f,
    val status: String = Status.Watching.toString(),
    val releasingEvery: String = Schedule.Irregular.toString(),

    // FIXME: extract to separate tables
    //val tagIds: List<Int> = emptyList(),
    //val references: List<String> = emptyList()
)