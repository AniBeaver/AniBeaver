package org.anibeaver.anibeaver.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.anibeaver.anibeaver.core.datastructures.ReleaseSchedule
import org.anibeaver.anibeaver.core.datastructures.Status


@Entity
data class AnimeEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val animeName: String = "",
    val releaseYear: String = "2000",

    val description: String = "",
    val rating: Float = 0f,
    val status: Int = Status.Watching.id,
    val releasingEvery: Int = ReleaseSchedule.Irregular.id,
    val coverArtSource: String = "",
    val coverArtLocalPath: String = "",
    val bannerArtSource: String = "",
    val bannerArtLocalPath: String = "",
    val episodesTotal: Int = 0,
    val episodesProgress: Int = 0,
    val rewatches: Int = 0,
    val type: Int = 0
)