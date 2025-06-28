package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.datastructures.FilterData
import org.anibeaver.anibeaver.core.datastructures.Schedule
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.ui.components.basic.FloatPicker
import org.anibeaver.anibeaver.ui.components.basic.YearPicker

@Composable
fun FilterPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (FilterData) -> Unit,
) {
    if (!show) return

    var selectedStatus by remember { mutableStateOf(Status.entries.toList()) }
    var selectedSchedule by remember { mutableStateOf(Schedule.entries.toList()) }
    var minYear by remember { mutableStateOf<String?>(null) }
    var maxYear by remember { mutableStateOf<String?>(null) }
    var minRating by remember { mutableStateOf<Float?>(null) }
    var maxRating by remember { mutableStateOf<Float?>(null) }

    AlertDialog(
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
                        maxRating
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
                onDismiss()
            }) {
                Text("Clear and Close")
            }
        },
        title = { Text("Filter Entries") },
        text = {
            FilterPopupContent(
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
                onMaxRatingChange = { maxRating = it }
            )
        }
    )
}

@Composable
private fun FilterPopupContent(
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
    onMaxRatingChange: (Float?) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp, min = 400.dp)
            .verticalScroll(scrollState)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Rating:", modifier = Modifier.weight(1f))
            FloatPicker(
                value = minRating ?: 0f,
                onValueChange = { onMinRatingChange(it) },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            Text("to", modifier = Modifier.weight(1f))
            FloatPicker(
                value = maxRating ?: 10f,
                onValueChange = { onMaxRatingChange(it) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Year", modifier = Modifier.weight(1f))
            YearPicker(
                value = minYear ?: "",
                onValueChange = { onMinYearChange(it.ifBlank { null }) },
                onIncrement = {},
                onDecrement = {},
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            Text("to", modifier = Modifier.weight(1f))
            YearPicker(
                value = maxYear ?: "",
                onValueChange = { onMaxYearChange(it.ifBlank { null }) },
                onIncrement = {},
                onDecrement = {},
                modifier = Modifier.weight(1f)
            )
        }

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
                Button(onClick = { onChange(emptyList()) }, modifier = Modifier) {
                    Text("Uncheck all")
                }
                Button(onClick = { onChange(entries) }, modifier = Modifier) {
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
