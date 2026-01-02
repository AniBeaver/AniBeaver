package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PageQuery(
    val data: PageQueryData
)

@Serializable
data class PageQueryData(
    @SerialName("Page") val page: Page
)

@Serializable
data class Page(
    val media: List<Media>
)
