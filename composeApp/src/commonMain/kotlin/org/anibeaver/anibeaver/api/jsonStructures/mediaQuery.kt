package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MediaQuery(
    val data: MediaQueryData
)

@Serializable
data class MediaQueryData(
    @SerialName("Media") val media: Media
)

@Serializable
data class Media(
    val id: Int,
    val title: Title
)

@Serializable
data class Title(
    val english: String?
)