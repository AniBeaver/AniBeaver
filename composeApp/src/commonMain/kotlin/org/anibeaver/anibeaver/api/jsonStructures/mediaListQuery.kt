package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaListQuery(
    val data: MediaListQueryData
)

@Serializable
data class MediaListQueryData(
    @SerialName("MediaListCollection") val mediaListCollection: MediaListCollection
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
