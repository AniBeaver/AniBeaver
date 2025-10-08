package org.anibeaver.anibeaver.core.datastructures

data class AutofillResultSelection(
    val name: String?,
    val year: Int?,
    val studios: List<String>,
    val genres: List<String>,
    val tags: List<String>,
    val cover: String?,
    val banner: String?,
    val airingSchedule: Schedule,
    val episodes: Int?
)