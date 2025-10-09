package org.anibeaver.anibeaver.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.anibeaver.anibeaver.core.datastructures.Schedule
import org.anibeaver.anibeaver.core.datastructures.Status


@Entity
data class AnimeEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val animeName: String = "",
    val releaseYear: String = "2000",

    val description: String = "",
    val rating: Float = 0f,
    val status: String = Status.Watching.toString(),
    val releasingEvery: String = Schedule.Irregular.toString(),
    val coverArtSource: String = "",
    val coverArtLocalPath: String = "",
    val bannerArtSource: String = "",
    val bannerArtLocalPath: String = "",
    val episodesTotal: Int = 0,
    val episodesProgress: Int = 0,
    val rewatches: Int = 0,
    val type: String = "Anime" // EntryType enum as string
)