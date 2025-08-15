package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import org.anibeaver.anibeaver.api.jsonStructures.Media

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

@Serializable
data class AutofillMediaQuery(
    val autofillData: AutofillData
)