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
}
