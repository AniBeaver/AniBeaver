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

//Autofill FIXME: I'm not sure whether I did this optimally
@Serializable
data class AutofillMediaQuery(
    val data: AutofillMediaQueryData
)

@Serializable
data class AutofillMediaQueryData(
    @SerialName("Media") val media: AutofillData
)

@Serializable
data class AutofillData(
    @SerialName("meanScore") val meanScore: Int? = null,
    @SerialName("bannerImage") val bannerImage: String? = null,
    @SerialName("airingSchedule") val airingSchedule: AiringSchedule? = null,
    @SerialName("seasonYear") val seasonYear: Int? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("studios") val studios: Studios? = null,
    @SerialName("tags") val tags: List<Tag>? = null, //FIXME: here genres are missing
    @SerialName("coverImage") val coverImage: CoverImage? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("title") val title: AutofillTitle? = null,
    @SerialName("duration") val duration: Int? = null,
    @SerialName("episodes") val episodes: Int? = null
)

@Serializable
data class AiringSchedule(
    val nodes: List<AiringNode>? = null
)

@Serializable
data class AiringNode(
    val airingAt: Long? = null
)

@Serializable
data class Studios(
    val nodes: List<StudioNode>? = null,
)

@Serializable
data class StudioNode(
    val name: String? = null,
    val isAnimationStudio: Boolean? = null
)

@Serializable
data class Tag(
    val name: String? = null,
    val rank: Int? = null
)

@Serializable
data class CoverImage(
    val medium: String? = null,
    val color: String? = null
)

@Serializable
data class AutofillTitle(
    val romaji: String? = null,
    val english: String? = null,
    val native: String? = null
)