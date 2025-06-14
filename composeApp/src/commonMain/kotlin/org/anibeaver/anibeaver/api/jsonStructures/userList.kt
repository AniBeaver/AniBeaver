package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.Serializable

@Serializable
data class MediaListResponse(
    val data: Data
)

@Serializable
data class Data(
    val MediaListCollection: MediaListCollection
)

@Serializable
data class MediaListCollection(
    val lists: List<MediaList>
)

@Serializable
data class MediaList(
    val entries: List<MediaEntry>
)
@Serializable
data class MediaEntry(
    val media: Media
)

@Serializable
data class Media(
    val title: Title
)

@Serializable
data class Title(
    val english: String?
)