package org.anibeaver.anibeaver.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.api.RequestType
import org.anibeaver.anibeaver.api.ValueSetter
import org.anibeaver.anibeaver.api.jsonStructures.AutofillData
import org.anibeaver.anibeaver.api.jsonStructures.AutofillMediaQuery
import org.anibeaver.anibeaver.api.jsonStructures.AutofillTitle
import org.anibeaver.anibeaver.core.datastructures.ReleaseSchedule
import org.anibeaver.anibeaver.api.ApiHandler

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext

data class ParsedAutofillData(
    val name_jp: String,
    val name_rm: String,
    val name_en: String,
    val startYear: Int,
    val endYear: Int,
    val cover_link: String,
    var banner_link: String,
    val avg_score: Float,
    val studios: List<String>,
    val genres: List<String>,
    val tags: List<String>,
    val airingScheduleWeekday: ReleaseSchedule,
    val eps_total: Int,
    val runtime: Int
)

val emptyParsedAutofillData =
    ParsedAutofillData(
        name_jp = "",
        name_rm = "",
        name_en = "",
        startYear = 0,
        endYear = 0,
        cover_link = "",
        banner_link = "",
        avg_score = 0f,
        studios = emptyList(),
        genres = emptyList(),
        tags = emptyList(),
        airingScheduleWeekday = ReleaseSchedule.Irregular,
        eps_total = 0,
        runtime = 0
    )

object AutofillController {
    fun idIsValid(mediaId: String): Boolean {
        return mediaId.isNotBlank() && mediaId.toIntOrNull() != null && mediaId.all { it.isDigit() }
        //TODO: actually check if an id like this exists
    }

    fun pullParsedAutofill(
        mediaIds: List<String>,
        onResult: (ParsedAutofillData) -> Unit,
        dataWrapper: DataWrapper,
        scope: CoroutineScope,
        priorityIndex: Int? = null
    ) {
        fun inferAiringScheduleWeekday(airingAts: List<Long>, airingScheduleErrorRatio: Float): ReleaseSchedule {
            val weekdays = ReleaseSchedule.entries.filter { it != ReleaseSchedule.Irregular }
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
            val irregular =
                airingWeekdays.isNotEmpty() && mismatchCount > airingWeekdays.size * airingScheduleErrorRatio
            return if (airingWeekdays.isEmpty() || mostCommonWeekday == null) ReleaseSchedule.Irregular
            else if (irregular) ReleaseSchedule.Irregular
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
            priorityIndex?.let { idx ->
                autofillDataList.getOrNull(idx)?.coverImage?.medium
            } ?: autofillDataList.firstNotNullOfOrNull { it.coverImage?.medium } ?: ""

        fun inferBannerLink(autofillDataList: List<AutofillData>): String =
            priorityIndex?.let { idx ->
                autofillDataList.getOrNull(idx)?.bannerImage
            } ?: autofillDataList.firstNotNullOfOrNull { it.bannerImage } ?: ""

        fun inferAvgScore(autofillDataList: List<AutofillData>): Float =
            autofillDataList.mapNotNull { it.meanScore }.let { if (it.isNotEmpty()) it.average().toFloat() else 0f }

        fun inferStudios(autofillDataList: List<AutofillData>): List<String> =
            autofillDataList.flatMap { data ->
                data.studios?.nodes?.filter { it.isAnimationStudio == true }?.mapNotNull { it.name } ?: emptyList()
            }.distinct()

        fun inferGenres(autofillDataList: List<AutofillData>): List<String> =
            autofillDataList.flatMap { it.genres ?: emptyList() }
                .distinct()

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

        fun inferEpsTotal(autofillDataList: List<AutofillData>): Int =
            autofillDataList.mapNotNull { it.episodes }.takeIf { it.isNotEmpty() }?.let { it.sum() } ?: 0

        fun inferMostCurrentAiringAts(autofillDataList: List<AutofillData>): List<Long> {
            val mostCurrentYear = autofillDataList.maxOfOrNull { it.seasonYear ?: Int.MIN_VALUE }
            val mostCurrent = autofillDataList.filter { it.seasonYear == mostCurrentYear }
            return mostCurrent.flatMap { it.airingSchedule?.nodes ?: emptyList() }
                .mapNotNull { it.airingAt }
            //TODO: Change infer airing and UI so instead of just a checkbox it has a series of radio buttons (just like Sync Name but under Sync Schedule) with an airing date for each reference's note (e.g "Season 1 airing Saturday", "Seasong 2 airing Sunday" etc. Also if the note is "Season" you can automatically shorten it to s. for these ui elements - just maybe, for now this works
        }

        fun inferNames(autofillDataList: List<AutofillData>, selector: (AutofillTitle) -> String?): String {
            val names = autofillDataList.mapNotNull { it.title?.let(selector)?.let { n -> cleanName(n) } }
                .filter { it.isNotBlank() }
            return if (names.isNotEmpty()) commonPrefix(names) else ""
        }

        fun inferRuntime(autofillDataList: List<AutofillData>): Int =
            autofillDataList.mapNotNull { data ->
                val eps = data.episodes ?: 0
                val dur = data.duration ?: 0
                if (eps > 0 && dur > 0) eps * dur else null
            }.sum()

        fun parseAutofillDataList(autofillDataList: List<AutofillData>): ParsedAutofillData {
            if (autofillDataList.isEmpty()) return emptyParsedAutofillData
            return ParsedAutofillData(
                name_jp = inferNames(autofillDataList) { it.native },
                name_rm = inferNames(autofillDataList) { it.romaji },
                name_en = inferNames(autofillDataList) { it.english },
                startYear = inferYearRange(autofillDataList).first,
                endYear = inferYearRange(autofillDataList).second,
                cover_link = inferCoverLink(autofillDataList),
                banner_link = inferBannerLink(autofillDataList),
                avg_score = inferAvgScore(autofillDataList),
                studios = inferStudios(autofillDataList),
                genres = inferGenres(autofillDataList),
                tags = inferTags(autofillDataList),
                airingScheduleWeekday = inferAiringScheduleWeekday(inferMostCurrentAiringAts(autofillDataList), 0.2f),
                eps_total = inferEpsTotal(autofillDataList),
                runtime = inferRuntime(autofillDataList)
            )
        }
        val apiHandler: ApiHandler = GlobalContext.get().get()

        val results = mutableListOf<AutofillData>()
        var completed = 0
        val validIds = mediaIds.filter { idIsValid(it) }
        if (validIds.isEmpty()) { //for efficiency's sake, not to launch unneeded coroutines
            onResult(parseAutofillDataList(results))
            return
        }
        scope.launch {
            for (mediaId in validIds) {
                apiHandler.makeRequest(
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
