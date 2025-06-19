package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SaveMediaListQuery(
    val data: SaveMediaListQueryData
)

@Serializable
data class SaveMediaListQueryData(
    @SerialName("SaveMediaListEntry") val saveMediaListEntry: SaveMediaListEntry
)

@Serializable
data class SaveMediaListEntry(
    val id: Int,
    val notes: String? = null
)