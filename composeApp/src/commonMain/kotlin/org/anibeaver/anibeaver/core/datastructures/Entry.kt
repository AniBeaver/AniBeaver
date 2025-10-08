package org.anibeaver.anibeaver.core.datastructures

import org.anibeaver.anibeaver.core.EntriesController

class Entry internal constructor(
    var entryData: EntryData = EntryData(),
    id: Int? = null
) {
    internal val id: Int = id ?: retrieveValidId()
        get() = field

    private fun retrieveValidId(): Int{
        return EntriesController.getValidEntryId()
    }

    fun matchesFilter(filterData: FilterData?): Boolean {
        if (filterData == null) return true

        if (filterData.selectedStatus.isNotEmpty() && !filterData.selectedStatus.contains(entryData.status)) return false

        if (filterData.selectedSchedule.isNotEmpty() && !filterData.selectedSchedule.contains(entryData.releasingEvery)) return false

        val minYear = filterData.minYear?.toIntOrNull() ?: Int.MIN_VALUE
        val maxYear = filterData.maxYear?.toIntOrNull() ?: Int.MAX_VALUE
        val entryYear = entryData.releaseYear.toIntOrNull() ?: Int.MIN_VALUE
        if (entryYear < minYear || entryYear > maxYear) return false

        val minRating = filterData.minRating ?: Float.MIN_VALUE
        val maxRating = filterData.maxRating ?: Float.MAX_VALUE
        if (entryData.rating < minRating || entryData.rating > maxRating) return false

        val selectedCustomTags = filterData.selectedTagIds.filter { Tag.getTypeById(it) == TagType.CUSTOM }
        val selectedStudioTags = filterData.selectedTagIds.filter { Tag.getTypeById(it) == TagType.STUDIO }
        val selectedGenreTags = filterData.selectedTagIds.filter { Tag.getTypeById(it) == TagType.GENRE }
        if (selectedCustomTags.isNotEmpty() && selectedCustomTags.none { it in entryData.tagIds }) return false
        if (selectedStudioTags.isNotEmpty() && selectedStudioTags.none { it in entryData.studioIds }) return false
        if (selectedGenreTags.isNotEmpty() && selectedGenreTags.none { it in entryData.genreIds }) return false
        return true
    }
}

class EntryData internal constructor(
    val animeName: String = "",
    val releaseYear: String = "2000",
    val studioIds: List<Int> = emptyList(),
    val genreIds: List<Int> = emptyList(),
    val description: String = "",
    val rating: Float = 0f,
    val status: Status = Status.Watching,
    val releasingEvery: Schedule = Schedule.Irregular,
    val tagIds: List<Int> = emptyList(),
    val references: List<Reference> = emptyList(),
    val coverArt: Art = Art("", ""),
    val bannerArt: Art = Art("", ""),
    val episodesTotal: Int = 0,
    val episodesProgress: Int = 0,
    val rewatches: Int = 0,
    val type: EntryType = EntryType.Anime
)

enum class EntryType {
    Anime, Manga
}

enum class Status {
    Planning, Watching, Paused, Completed, Dropped;
    override fun toString(): String = when(this) {
        Planning -> "Planning"
        Watching -> "Watching"
        Paused -> "On Hold"
        Completed -> "Completed"
        Dropped -> "Dropped"
    }
}

enum class Schedule {
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday, Irregular;
    override fun toString(): String = when(this) {
        Monday -> "Monday"
        Tuesday -> "Tuesday"
        Wednesday -> "Wednesday"
        Thursday -> "Thursday"
        Friday -> "Friday"
        Saturday -> "Saturday"
        Sunday -> "Sunday"
        Irregular -> "Irregular"
    }
}

data class FilterData(
    val selectedStatus: List<Status>,
    val selectedSchedule: List<Schedule>,
    val minYear: String?,
    val maxYear: String?,
    val minRating: Float?,
    val maxRating: Float?,
    val selectedTagIds: List<Int>
)

enum class SortingBy {
    Rating,
    Status, //watching, on hold, planning, completed, dropped
    Rewatches,
    Length,
    Year;
    override fun toString(): String = when (this) {
        Rating -> "Rating"
        Status -> "Status"
        Rewatches -> "Rewatches"
        Year -> "Year"
        Length -> "Length"
    }
}

enum class SortingType {
    Ascending,
    Descending;
    override fun toString(): String = when (this) {
        Ascending -> "Ascending"
        Descending -> "Descending"
    }
}

data class Art(
    val source: String, //either link or "custom"
    val local_path: String //if empty TODO: download image from link and save local_path maybe
)