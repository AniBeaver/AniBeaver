package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.FilterData
import org.anibeaver.anibeaver.core.datastructures.ReleaseSchedule
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.ui.components.basic.FloatPicker
import org.anibeaver.anibeaver.ui.components.basic.YearPicker

private const val MIN_YEAR = 1900
private const val MAX_YEAR = 2100
private const val MIN_RATING = 0f
private const val MAX_RATING = 10f

object FilterDefaults {
    val DEFAULT_MIN_YEAR = MIN_YEAR.toString()
    val DEFAULT_MAX_YEAR = MAX_YEAR.toString()
    val DEFAULT_MIN_RATING = MIN_RATING
    val DEFAULT_MAX_RATING = MAX_RATING
    fun defaultStatus() = Status.entries.toList()
    fun defaultSchedule() = ReleaseSchedule.entries.toList()
    fun defaultTagIds() = TagsController.tags.sortedBy { it.name }.map { it.id }

    fun resetFilter(onChange: (FilterData) -> Unit) {
        onChange(
            FilterData(
                selectedStatus = defaultStatus(),
                selectedSchedule = defaultSchedule(),
                minYear = DEFAULT_MIN_YEAR,
                maxYear = DEFAULT_MAX_YEAR,
                minRating = DEFAULT_MIN_RATING,
                maxRating = DEFAULT_MAX_RATING,
                selectedTagIds = defaultTagIds()
            )
        )
    }
}

@Composable
fun FilterPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (FilterData) -> Unit,
    initialFilter: FilterData? = null,
) {
    if (!show) return

    data class FilterUiState(
        var selectedStatus: List<Status>,
        var selectedSchedule: List<ReleaseSchedule>,
        var minYear: String?,
        var maxYear: String?,
        var minRating: Float?,
        var maxRating: Float?,
        var selectedTagIds: List<Int>,
        var selectedTab: Int
    )

    val allTags = remember { TagsController.tags.sortedBy { it.name } }
    var state by remember {
        mutableStateOf(
            FilterUiState(
                selectedStatus = initialFilter?.selectedStatus ?: defaultFilterData.selectedStatus,
                selectedSchedule = initialFilter?.selectedSchedule ?: defaultFilterData.selectedSchedule,
                minYear = initialFilter?.minYear ?: defaultFilterData.minYear,
                maxYear = initialFilter?.maxYear ?: defaultFilterData.maxYear,
                minRating = initialFilter?.minRating ?: defaultFilterData.minRating,
                maxRating = initialFilter?.maxRating ?: defaultFilterData.maxRating,
                selectedTagIds = initialFilter?.selectedTagIds ?: defaultFilterData.selectedTagIds,
                selectedTab = 0
            )
        )
    }
    val tabTitles = listOf("General", "Tags")

    AlertDialog(
        modifier = Modifier.width(700.dp).height(800.dp),
        onDismissRequest = {}, confirmButton = {
            Button(
                onClick = {
                    onConfirm(

                        FilterData(
                            state.selectedStatus,
                            state.selectedSchedule,
                            state.minYear,
                            state.maxYear,
                            state.minRating,
                            state.maxRating,
                            state.selectedTagIds
                        )

                    )
                }) {
                Text("Filter")
            }
        }, dismissButton = {
            Button(
                onClick = {
                    state = state.copy(
                        selectedStatus = FilterDefaults.defaultStatus(),
                        selectedSchedule = FilterDefaults.defaultSchedule(),
                        minYear = FilterDefaults.DEFAULT_MIN_YEAR,
                        maxYear = FilterDefaults.DEFAULT_MAX_YEAR,
                        minRating = FilterDefaults.DEFAULT_MIN_RATING,
                        maxRating = FilterDefaults.DEFAULT_MAX_RATING,
                        selectedTagIds = listOf() //TODO : there's a duplicate of this state object, get from same source
                    )
                }) {
                Text("Reset all filters")
            }
        }, title = { Text("Filter Entries") }, text = {
            Column(
                modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()
                ) {
                    TabRow(
                        selectedTabIndex = state.selectedTab,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = state.selectedTab == index,
                                onClick = { state = state.copy(selectedTab = index) },
                                text = { Text(title, style = MaterialTheme.typography.labelLarge) },
                                selectedContentColor = MaterialTheme.colorScheme.primary,
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f, fill = true)) {
                    when (state.selectedTab) {
                        0 -> FilterGeneralTab(
                            selectedStatus = state.selectedStatus,
                            onStatusChange = { state = state.copy(selectedStatus = it) },
                            selectedSchedule = state.selectedSchedule,
                            onScheduleChange = { state = state.copy(selectedSchedule = it) },
                            minYear = state.minYear,
                            onMinYearChange = { state = state.copy(minYear = it) },
                            maxYear = state.maxYear,
                            onMaxYearChange = { state = state.copy(maxYear = it) },
                            minRating = state.minRating,
                            onMinRatingChange = { state = state.copy(minRating = it) },
                            maxRating = state.maxRating,
                            onMaxRatingChange = { state = state.copy(maxRating = it) },
                            onResetMinMax = {
                                state = state.copy(
                                    minYear = FilterDefaults.DEFAULT_MIN_YEAR,
                                    maxYear = FilterDefaults.DEFAULT_MAX_YEAR,
                                    minRating = FilterDefaults.DEFAULT_MIN_RATING,
                                    maxRating = FilterDefaults.DEFAULT_MAX_RATING
                                )
                            })

                        1 -> TagCheckboxRow(
                            allTags = allTags,
                            selectedTagIds = state.selectedTagIds,
                            onChange = { state = state.copy(selectedTagIds = it) })
                    }
                }
            }
        })
}

@Composable
private fun FilterGeneralTab(
    selectedStatus: List<Status>,
    onStatusChange: (List<Status>) -> Unit,
    selectedSchedule: List<ReleaseSchedule>,
    onScheduleChange: (List<ReleaseSchedule>) -> Unit,
    minYear: String?,
    onMinYearChange: (String?) -> Unit,
    maxYear: String?,
    onMaxYearChange: (String?) -> Unit,
    minRating: Float?,
    onMinRatingChange: (Float?) -> Unit,
    maxRating: Float?,
    onMaxRatingChange: (Float?) -> Unit,
    onResetMinMax: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        RatingFilterRow(
            minRating = minRating,
            onMinRatingChange = onMinRatingChange,
            maxRating = maxRating,
            onMaxRatingChange = onMaxRatingChange
        )
        YearFilterRow(
            minYear = minYear, onMinYearChange = onMinYearChange, maxYear = maxYear, onMaxYearChange = onMaxYearChange
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        FilterCheckboxRow(
            label = "Status", entries = Status.entries.toList(), selected = selectedStatus, onChange = onStatusChange
        )
        FilterCheckboxRow(
            label = "Schedule",
            entries = ReleaseSchedule.entries.toList(),
            selected = selectedSchedule,
            onChange = onScheduleChange
        )
    }
}

@Composable
private fun RatingFilterRow(
    minRating: Float?, onMinRatingChange: (Float?) -> Unit, maxRating: Float?, onMaxRatingChange: (Float?) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.width(100.dp)) {
            Text("Rating:")
            Button(
                onClick = { onMinRatingChange(MIN_RATING); onMaxRatingChange(MAX_RATING) },
                modifier = Modifier.align(Alignment.Start).padding(top = 4.dp).height(32.dp)
            ) {
                Text("Reset")
            }
        }
        FloatPicker(
            value = minRating ?: MIN_RATING,
            onValueChange = { onMinRatingChange(it) },
            modifier = Modifier.weight(1f),
            label = "Min"
        )
        Spacer(Modifier.width(16.dp))
        FloatPicker(
            value = maxRating ?: MAX_RATING,
            onValueChange = { onMaxRatingChange(it) },
            modifier = Modifier.weight(1f),
            label = "Max"
        )
    }
}

@Composable
private fun YearFilterRow(
    minYear: String?, onMinYearChange: (String?) -> Unit, maxYear: String?, onMaxYearChange: (String?) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.width(100.dp)) {
            Text("Year:")
            Button(
                onClick = { onMinYearChange(MIN_YEAR.toString()); onMaxYearChange(MAX_YEAR.toString()) },
                modifier = Modifier.align(Alignment.Start).padding(top = 4.dp).height(32.dp)
            ) {
                Text("Reset")
            }
        }
        YearPicker(
            value = minYear ?: "", onValueChange = { onMinYearChange(it.ifBlank { null }) }, onIncrement = {
                val year = (minYear?.toIntOrNull() ?: MIN_YEAR) + 1
                onMinYearChange(year.coerceAtMost((maxYear?.toIntOrNull() ?: MAX_YEAR)).toString())
            }, onDecrement = {
                val year = (minYear?.toIntOrNull() ?: MIN_YEAR) - 1
                onMinYearChange(year.coerceAtLeast(MIN_YEAR).toString())
            }, modifier = Modifier.weight(1f), label = "Min"
        )
        Spacer(Modifier.width(16.dp))
        YearPicker(
            value = maxYear ?: "", onValueChange = { onMaxYearChange(it.ifBlank { null }) }, onIncrement = {
                val year = (maxYear?.toIntOrNull() ?: MAX_YEAR) + 1
                onMaxYearChange(year.coerceAtMost(MAX_YEAR).toString())
            }, onDecrement = {
                val year = (maxYear?.toIntOrNull() ?: MAX_YEAR) - 1
                onMaxYearChange(year.coerceAtLeast((minYear?.toIntOrNull() ?: MIN_YEAR)).toString())
            }, modifier = Modifier.weight(1f), label = "Max"
        )
    }
}

@Composable
private fun <T> FilterCheckboxRow(
    label: String, entries: List<T>, selected: List<T>, onChange: (List<T>) -> Unit
) where T : Enum<T> {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { //TODO: replace these with SectionWithCheckall
                Button(onClick = { onChange(emptyList()) }, modifier = Modifier.height(32.dp)) {
                    Text("Uncheck all")
                }
                Button(onClick = { onChange(entries.toList()) }, modifier = Modifier.height(32.dp)) {
                    Text("Check all")
                }

            }
        }
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            entries.forEach { entry ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selected.contains(entry), onCheckedChange = { checked ->
                            onChange(
                                if (checked) selected + entry else selected - entry
                            )
                        })
                    Text(entry.toString())
                }
            }
        }
    }
}

@Composable
private fun TagCheckboxSection( //TODO: could also be a SectionWithCheckAll, refactor
    label: String,
    tags: List<org.anibeaver.anibeaver.core.datastructures.Tag>,
    selectedTagIds: List<Int>,
    onChange: (List<Int>) -> Unit
) {
    val sectionTagIds = tags.map { it.id }
    selectedTagIds.filter { it in sectionTagIds }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = {
                        onChange(selectedTagIds - sectionTagIds)
                    }, modifier = Modifier.height(32.dp)
                ) {
                    Text("Uncheck all")
                }
                Button(
                    onClick = {
                        onChange((selectedTagIds - sectionTagIds) + sectionTagIds)
                    }, modifier = Modifier.height(32.dp)
                ) {
                    Text("Check all")
                }
            }
        }
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            tags.forEach { tag ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedTagIds.contains(tag.id), onCheckedChange = { checked ->
                            onChange(
                                if (checked) selectedTagIds + tag.id else selectedTagIds - tag.id
                            )
                        })
                    Text(
                        tag.name, color = try {
                            Color(tag.color.removePrefix("#").toLong(16) or 0xFF000000)
                        } catch (_: Exception) {
                            Color.Unspecified
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TagCheckboxRow(
    allTags: List<org.anibeaver.anibeaver.core.datastructures.Tag>,
    selectedTagIds: List<Int>,
    onChange: (List<Int>) -> Unit
) {
    val genres = allTags.filter { it.type == TagType.GENRE }
    val customs = allTags.filter { it.type == TagType.CUSTOM }
    val studios = allTags.filter { it.type == TagType.STUDIO }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TagCheckboxSection("Genres", genres, selectedTagIds, onChange)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        TagCheckboxSection("Custom tags", customs, selectedTagIds, onChange)
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        TagCheckboxSection("Studios", studios, selectedTagIds, onChange)
    }
}

val defaultFilterData: FilterData
    get() = FilterData(
        selectedStatus = FilterDefaults.defaultStatus(),
        selectedSchedule = FilterDefaults.defaultSchedule(),
        minYear = FilterDefaults.DEFAULT_MIN_YEAR,
        maxYear = FilterDefaults.DEFAULT_MAX_YEAR,
        minRating = FilterDefaults.DEFAULT_MIN_RATING,
        maxRating = FilterDefaults.DEFAULT_MAX_RATING,
        selectedTagIds = listOf() //instead of FilterDefaults.defaultTagIds() because they're supposed to be unchecked by default
    )
