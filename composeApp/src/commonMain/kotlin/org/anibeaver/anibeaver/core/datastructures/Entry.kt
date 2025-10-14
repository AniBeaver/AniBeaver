package org.anibeaver.anibeaver.core.datastructures

import org.anibeaver.anibeaver.core.EntriesController

class Entry internal constructor(
    var entryData: EntryData = EntryData(), id: Int? = null
) {
    internal val id: Int = id ?: retrieveValidId()
        get() = field

    private fun retrieveValidId(): Int {
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
    val title: String? = "",
    val releaseYear: String = "2000",
    val studioIds: List<Int> = emptyList(),
    val genreIds: List<Int> = emptyList(),
    val description: String = "",
    val rating: Float = 0f,
    val status: Status = Status.Watching,
    val releasingEvery: ReleaseSchedule = ReleaseSchedule.Irregular,
    val tagIds: List<Int> = emptyList(),
    val references: List<Reference> = emptyList(),
    val coverArt: Art = Art("", ""),
    val bannerArt: Art = Art("", ""),
    val episodesTotal: Int = 0,
    val episodesProgress: Int = 0,
    val rewatches: Int = 0,
    val type: EntryType = EntryType.Anime
)

enum class EntryType(val id: Int) {
    Anime(0),
    Manga(1);

    companion object {
        fun fromId(id: Int): EntryType? = entries.find { it.id == id }
    }
}

enum class Status(val id: Int, val displayName: String) {
    Planning(0, "Planning"),
    Watching(1, "Watching"),
    Paused(2, "On Hold"),
    Completed(3, "Completed"),
    Dropped(4, "Dropped");

    override fun toString() = displayName

    companion object {
        fun fromId(id: Int): Status? = entries.find { it.id == id }
        fun fromString(value: String): Status? = entries.find { it.displayName.equals(value, ignoreCase = true) }
    }
}

enum class ReleaseSchedule(val id: Int, val displayName: String) {
    Monday(0, "Monday"),
    Tuesday(1, "Tuesday"),
    Wednesday(2, "Wednesday"),
    Thursday(3, "Thursday"),
    Friday(4, "Friday"),
    Saturday(5, "Saturday"),
    Sunday(6, "Sunday"),
    Irregular(7, "Irregular");

    override fun toString() = displayName

    companion object {
        fun fromId(id: Int): ReleaseSchedule? = entries.find { it.id == id }
        fun fromString(value: String): ReleaseSchedule? =
            entries.find { it.displayName.equals(value, ignoreCase = true) }
    }
}


data class FilterData(
    val selectedStatus: List<Status>,
    val selectedSchedule: List<ReleaseSchedule>,
    val minYear: String?,
    val maxYear: String?,
    val minRating: Float?,
    val maxRating: Float?,
    val selectedTagIds: List<Int>
)

enum class SortingBy {
    Rating, Rewatches, Length, Year, Title;

    override fun toString(): String = when (this) {
        Rating -> "Rating"
        Rewatches -> "Rewatches"
        Year -> "Year"
        Length -> "Length"
        Title -> "Title"
    }
}

enum class SortingType {
    Ascending, Descending;

    override fun toString(): String = when (this) {
        Ascending -> "Ascending"
        Descending -> "Descending"
    }
}

data class Art(
    val source: String, //either link or "custom"
    val local_path: String //if empty TODO: download image from link and save local_path maybe
)