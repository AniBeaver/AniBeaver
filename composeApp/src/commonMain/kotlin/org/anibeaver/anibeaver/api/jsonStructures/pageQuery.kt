package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val media: List<PageMedia>
)

@Serializable
data class PageMedia(
    val id: Int,
    val title: PageTitle
)

@Serializable
data class PageTitle(
    val english: String?,
    val native: String?
)
