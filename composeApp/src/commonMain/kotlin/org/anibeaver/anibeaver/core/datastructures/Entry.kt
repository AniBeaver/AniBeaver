package org.anibeaver.anibeaver.core.datastructures

enum class Status {
    Towatch, Watching, OnHold, Finished, Dropped;
    override fun toString(): String = when(this) {
        Towatch -> "Towatch"
        Watching -> "Watching"
        OnHold -> "On Hold"
        Finished -> "Finished"
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

class Entry internal constructor(
    val animeName: String,
    val releaseYear: String,
    val studioIds: List<Int>,
    val genreIds: List<Int>,
    val description: String,
    val rating: Float,
    val status: Status,
    val releasingEvery: Schedule,
    val tagIds: List<Int>,
    internal val id: Int
) {
    fun getId(): Int = id

    fun matchesFilter(filter: FilterData?): Boolean {
        if (filter == null) return true

        if (filter.selectedStatus.isNotEmpty() && !filter.selectedStatus.contains(status)) return false

        if (filter.selectedSchedule.isNotEmpty() && !filter.selectedSchedule.contains(releasingEvery)) return false

        val minYear = filter.minYear?.toIntOrNull() ?: Int.MIN_VALUE
        val maxYear = filter.maxYear?.toIntOrNull() ?: Int.MAX_VALUE
        val entryYear = releaseYear.toIntOrNull() ?: Int.MIN_VALUE
        if (entryYear < minYear || entryYear > maxYear) return false

        val minRating = filter.minRating ?: Float.MIN_VALUE
        val maxRating = filter.maxRating ?: Float.MAX_VALUE
        if (rating < minRating || rating > maxRating) return false

        val selectedCustomTags = filter.selectedTagIds.filter { Tag.getTypeById(it) == TagType.CUSTOM }
        val selectedStudioTags = filter.selectedTagIds.filter { Tag.getTypeById(it) == TagType.STUDIO }
        val selectedGenreTags = filter.selectedTagIds.filter { Tag.getTypeById(it) == TagType.GENRE }
        if (selectedCustomTags.isNotEmpty() && selectedCustomTags.none { it in tagIds }) return false
        if (selectedStudioTags.isNotEmpty() && selectedStudioTags.none { it in studioIds }) return false
        if (selectedGenreTags.isNotEmpty() && selectedGenreTags.none { it in genreIds }) return false
        return true
    }
}
