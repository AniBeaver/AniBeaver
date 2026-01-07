package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileQuery(
    val data: ProfileQueryData
)

@Serializable
data class ProfileQueryData(
    @SerialName("Viewer") val profile: Profile
)

@Serializable
data class Profile(
    val id: Int,
    val name: String,
    val avatar: Avatar? = null,
    val bannerImage: String? = null,
    val statistics: Statistics? = null
)

@Serializable
data class Avatar(
    val large: String? = null,
    val medium: String? = null
)

@Serializable
data class Statistics(
    val anime: AnimeStats? = null,
    val manga: MangaStats? = null
)

@Serializable
data class AnimeStats(
    val count: Int? = null,
    val minutesWatched: Int? = null,
    val episodesWatched: Int? = null
)

@Serializable
data class MangaStats(
    val count: Int? = null,
    val chaptersRead: Int? = null,
    val volumesRead: Int? = null
)
