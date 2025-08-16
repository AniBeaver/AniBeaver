package org.anibeaver.anibeaver.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.api.RequestType
import org.anibeaver.anibeaver.api.ValueSetter
import org.anibeaver.anibeaver.api.jsonStructures.AutofillData
import org.anibeaver.anibeaver.api.jsonStructures.AutofillMediaQuery
import org.anibeaver.anibeaver.api.jsonStructures.AutofillTitle
import org.anibeaver.anibeaver.core.datastructures.Schedule

data class ParsedAutofillData(
    val name_jp_choices: List<String>,
    val name_rm_choices: List<String>,
    val name_en_choices: List<String>,
    val startYear: Int,
    val endYear: Int,
    val cover_link: String,
    var banner_link: String,
    val avg_score: Float,
    val studios: List<String>,
    val genres: List<String>,
    val tags: List<String>,
    val airingScheduleWeekday: Schedule,
)

val emptyParsedAutofillData =
    ParsedAutofillData(
        name_jp_choices = emptyList(),
        name_rm_choices = emptyList(),
        name_en_choices = emptyList(),
        startYear = 0,
        endYear = 0,
        cover_link = "",
        banner_link = "",
        avg_score = 0f,
        studios = emptyList(),
        genres = emptyList(),
        tags = emptyList(),
        airingScheduleWeekday = Schedule.Irregular,
    )

object AutofillController {
    fun idIsValid(mediaId: String): Boolean {
        return mediaId.isNotBlank() && mediaId.toIntOrNull() != null && mediaId.all { it.isDigit() }
    }

    fun pullParsedAutofill(
        mediaIds: List<String>,
        onResult: (ParsedAutofillData) -> Unit,
        dataWrapper: DataWrapper,
        scope: CoroutineScope
    ) {
        fun inferAiringScheduleWeekday(airingAts: List<Long>): Schedule {
            val airingScheduleErrorRatio = 0.2f
            val weekdays = Schedule.entries.filter { it != Schedule.Irregular }
            val secondsInDay = 86400L
            val airingWeekdays = airingAts.mapNotNull { ts ->
                // Calculate weekday from epoch seconds (1970-01-01 is a Thursday)
                // 1970-01-01 = Thursday, Schedule.Thursday.ordinal = 3
                val daysSinceEpoch = ts / secondsInDay
                val weekdayIndex = ((daysSinceEpoch + 3) % 7).toInt() // +3 to align epoch to Thursday
                weekdays.getOrNull(if (weekdayIndex < 0) weekdayIndex + 7 else weekdayIndex)
            }
            val mostCommonWeekday = airingWeekdays.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
            val mismatchCount = airingWeekdays.count { it != mostCommonWeekday }
            val irregular = airingWeekdays.isNotEmpty() && mismatchCount > airingWeekdays.size * airingScheduleErrorRatio
            return if (airingWeekdays.isEmpty() || mostCommonWeekday == null) Schedule.Irregular
                   else if (irregular) Schedule.Irregular
                   else mostCommonWeekday
        }

        fun cleanName(name: String?): String? {
            return name?.replace(Regex("(?i)season\\s*\\d*"), "")
                ?.replace(Regex(":+$"), "")
                ?.trim()
        }

        fun commonPrefix(strings: List<String>): String {
            if (strings.isEmpty()) return ""
            val minLen = strings.minOf { it.length }
            var end = 0
            while (end < minLen && strings.all { it[end] == strings[0][end] }) {
                end++
            }
            return strings[0].substring(0, end).trim().replace(Regex(":+$"), "")
        }

        fun inferYearRange(autofillDataList: List<AutofillData>): Pair<Int, Int> {
            val years = autofillDataList.mapNotNull { it.seasonYear }
            return Pair(years.minOrNull() ?: 0, years.maxOrNull() ?: 0)
        }

        fun inferCoverLink(autofillDataList: List<AutofillData>): String =
            autofillDataList.firstNotNullOfOrNull { it.coverImage?.medium } ?: ""

        fun inferBannerLink(autofillDataList: List<AutofillData>): String =
            autofillDataList.firstNotNullOfOrNull { it.bannerImage } ?: ""

        fun inferAvgScore(autofillDataList: List<AutofillData>): Float =
            autofillDataList.mapNotNull { it.meanScore }.let { if (it.isNotEmpty()) it.average().toFloat() else 0f }

        fun inferStudios(autofillDataList: List<AutofillData>): List<String> =
            autofillDataList.flatMap { data ->
                data.studios?.nodes?.filter { it.isAnimationStudio == true }?.mapNotNull { it.name } ?: emptyList()
            }.distinct()

        fun inferGenres(autofillDataList: List<AutofillData>): List<String> =
            autofillDataList.flatMap { it.tags ?: emptyList() }
                .filter { it.name != null && it.rank != null }
                .sortedByDescending { it.rank }
                .map { it.name!! }
                .distinct()
                .take(5)

        fun inferTags(autofillDataList: List<AutofillData>): List<String> =
            autofillDataList.flatMap { it.tags ?: emptyList() }
                .filter { it.rank != null }
                .sortedByDescending { it.rank }
                .mapNotNull { it.name }
                .distinct()
                .take(5)

        fun inferAiringAts(autofillDataList: List<AutofillData>): List<Long> =
            autofillDataList.flatMap { it.airingSchedule?.nodes ?: emptyList() }
                .mapNotNull { it.airingAt }

        fun inferNames(autofillDataList: List<AutofillData>, selector: (AutofillTitle) -> String?): List<String> {
            val names = autofillDataList.mapNotNull { it.title?.let(selector)?.let { n -> cleanName(n) } }.filter { it.isNotBlank() }
            return if (names.isNotEmpty()) listOf(commonPrefix(names)) else emptyList()
        }

        fun parseAutofillDataList(autofillDataList: List<AutofillData>): ParsedAutofillData {
            if (autofillDataList.isEmpty()) {
                return emptyParsedAutofillData
            }

            val name_rm_common = inferNames(autofillDataList) { it.romaji }
            val name_en_common = inferNames(autofillDataList) { it.english }
            val name_jp_common = inferNames(autofillDataList) { it.native }
            val (startYear, endYear) = inferYearRange(autofillDataList)
            val cover_link = inferCoverLink(autofillDataList)
            val banner_link = inferBannerLink(autofillDataList)
            val avg_score = inferAvgScore(autofillDataList)

            val studios = inferStudios(autofillDataList)
            val genres = inferGenres(autofillDataList)
            val tags = inferTags(autofillDataList)
            val airingAts = inferAiringAts(autofillDataList)
            val airingScheduleWeekday = inferAiringScheduleWeekday(airingAts)

            return ParsedAutofillData(
                name_jp_choices = name_jp_common,
                name_rm_choices = name_rm_common,
                name_en_choices = name_en_common,
                startYear = startYear,
                endYear = endYear,
                cover_link = cover_link,
                banner_link = banner_link,
                avg_score = avg_score,
                studios = studios,
                genres = genres,
                tags = tags,
                airingScheduleWeekday = airingScheduleWeekday,
            )
        }
        val results = mutableListOf<AutofillData>()
        var completed = 0
        val validIds = mediaIds.filter { idIsValid(it) }
        if (validIds.isEmpty()) { //for efficiency's sake
            onResult(parseAutofillDataList(results))
            return
        }
        scope.launch {
            for (mediaId in validIds) {
                dataWrapper.apiHandler.makeRequest(
                    variables = mapOf("id" to mediaId),
                    valueSetter = ValueSetter { mediaQuery: AutofillMediaQuery ->
                        results.add(mediaQuery.data.media)
                        completed++
                        if (completed == validIds.size) {
                            onResult(parseAutofillDataList(results))
                        }
                    },
                    requestType = RequestType.AUTOFILL_MEDIA
                )
            }
        }
    }
}
