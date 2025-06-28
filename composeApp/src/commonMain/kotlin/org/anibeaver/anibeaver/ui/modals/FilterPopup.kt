package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.FilterData
import org.anibeaver.anibeaver.core.datastructures.Schedule
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.ui.components.basic.FloatPicker
import org.anibeaver.anibeaver.ui.components.basic.YearPicker

private const val MIN_YEAR = 1900
private const val MAX_YEAR = 2100
private const val MIN_RATING = 0f
private const val MAX_RATING = 10f

@Composable
fun FilterPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (FilterData) -> Unit,
) {
    if (!show) return

    var selectedStatus by remember { mutableStateOf(Status.entries.toList()) }
    var selectedSchedule by remember { mutableStateOf(Schedule.entries.toList()) }
    var minYear by remember { mutableStateOf<String?>(MIN_YEAR.toString()) }
    var maxYear by remember { mutableStateOf<String?>(MAX_YEAR.toString()) }
    var minRating by remember { mutableStateOf<Float?>(MIN_RATING) }
    var maxRating by remember { mutableStateOf<Float?>(MAX_RATING) }
    var selectedTagIds by remember { mutableStateOf<List<Int>>(TagsController.tags.sortedBy { it.name }.map { it.getId() }) }
    val allTags = remember { TagsController.tags.sortedBy { it.name } }
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("General", "Tags")

    AlertDialog(
        modifier = Modifier.width(700.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    FilterData(
                        selectedStatus,
                        selectedSchedule,
                        minYear,
                        maxYear,
                        minRating,
                        maxRating,
                        selectedTagIds
                    )
                )
            }) {
                Text("Filter")
            }
        },
        dismissButton = {
            Button(onClick = {
                selectedStatus = Status.entries.toList()
                selectedSchedule = Schedule.entries.toList()
                minYear = null
                maxYear = null
                minRating = null
                maxRating = null
                selectedTagIds = allTags.map { it.getId() }
                onDismiss()
            }) {
                Text("Clear")
            }
        },
        title = { Text("Filter Entries") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title, style = MaterialTheme.typography.labelLarge) },
                                selectedContentColor = MaterialTheme.colorScheme.primary,
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                when (selectedTab) {
                    0 -> FilterGeneralTab(
                        selectedStatus = selectedStatus,
                        onStatusChange = { selectedStatus = it },
                        selectedSchedule = selectedSchedule,
                        onScheduleChange = { selectedSchedule = it },
                        minYear = minYear,
                        onMinYearChange = { minYear = it },
                        maxYear = maxYear,
                        onMaxYearChange = { maxYear = it },
                        minRating = minRating,
                        onMinRatingChange = { minRating = it },
                        maxRating = maxRating,
                        onMaxRatingChange = { maxRating = it },
                        onResetMinMax = {
                            minYear = MIN_YEAR.toString()
                            maxYear = MAX_YEAR.toString()
                            minRating = MIN_RATING
                            maxRating = MAX_RATING
                        }
                    )
                    1 -> TagCheckboxRow(
                        allTags = allTags,
                        selectedTagIds = selectedTagIds,
                        onChange = { selectedTagIds = it }
                    )
                }
            }
        }
    )
}

@Composable
private fun FilterGeneralTab(
    selectedStatus: List<Status>,
    onStatusChange: (List<Status>) -> Unit,
    selectedSchedule: List<Schedule>,
    onScheduleChange: (List<Schedule>) -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp, min = 400.dp)
            .verticalScroll(scrollState)
    ) {
        RatingFilterRow(
            minRating = minRating,
            onMinRatingChange = onMinRatingChange,
            maxRating = maxRating,
            onMaxRatingChange = onMaxRatingChange
        )
        YearFilterRow(
            minYear = minYear,
            onMinYearChange = onMinYearChange,
            maxYear = maxYear,
            onMaxYearChange = onMaxYearChange
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        FilterCheckboxRow(
            label = "Status",
            entries = Status.entries.toList(),
            selected = selectedStatus,
            onChange = onStatusChange
        )
        FilterCheckboxRow(
            label = "Schedule",
            entries = Schedule.entries.toList(),
            selected = selectedSchedule,
            onChange = onScheduleChange
        )
    }
}

@Composable
private fun RatingFilterRow(
    minRating: Float?,
    onMinRatingChange: (Float?) -> Unit,
    maxRating: Float?,
    onMaxRatingChange: (Float?) -> Unit
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
    minYear: String?,
    onMinYearChange: (String?) -> Unit,
    maxYear: String?,
    onMaxYearChange: (String?) -> Unit
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
            value = minYear ?: "",
            onValueChange = { onMinYearChange(it.ifBlank { null }) },
            onIncrement = {
                val year = (minYear?.toIntOrNull() ?: MIN_YEAR) + 1
                onMinYearChange(year.coerceAtMost((maxYear?.toIntOrNull() ?: MAX_YEAR)).toString())
            },
            onDecrement = {
                val year = (minYear?.toIntOrNull() ?: MIN_YEAR) - 1
                onMinYearChange(year.coerceAtLeast(MIN_YEAR).toString())
            },
            modifier = Modifier.weight(1f),
            label = "Min"
        )
        Spacer(Modifier.width(16.dp))
        YearPicker(
            value = maxYear ?: "",
            onValueChange = { onMaxYearChange(it.ifBlank { null }) },
            onIncrement = {
                val year = (maxYear?.toIntOrNull() ?: MAX_YEAR) + 1
                onMaxYearChange(year.coerceAtMost(MAX_YEAR).toString())
            },
            onDecrement = {
                val year = (maxYear?.toIntOrNull() ?: MAX_YEAR) - 1
                onMaxYearChange(year.coerceAtLeast((minYear?.toIntOrNull() ?: MIN_YEAR)).toString())
            },
            modifier = Modifier.weight(1f),
            label = "Max"
        )
    }
}

@Composable
private fun <T> FilterCheckboxRow(
    label: String,
    entries: List<T>,
    selected: List<T>,
    onChange: (List<T>) -> Unit
) where T : Enum<T> {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        checked = selected.contains(entry),
                        onCheckedChange = { checked ->
                            onChange(
                                if (checked) selected + entry else selected - entry
                            )
                        }
                    )
                    Text(entry.toString())
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
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tags", modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(onClick = { onChange(emptyList()) }, modifier = Modifier.height(32.dp)) {
                    Text("Uncheck all")
                }
                Button(onClick = { onChange(allTags.map { it.getId() }) }, modifier = Modifier.height(32.dp)) {
                    Text("Check all")
                }
            }
        }
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            allTags.forEach { tag ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedTagIds.contains(tag.getId()),
                        onCheckedChange = { checked ->
                            onChange(
                                if (checked) selectedTagIds + tag.getId() else selectedTagIds - tag.getId()
                            )
                        }
                    )
                    Text(
                        tag.name,
                        color = try {
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
