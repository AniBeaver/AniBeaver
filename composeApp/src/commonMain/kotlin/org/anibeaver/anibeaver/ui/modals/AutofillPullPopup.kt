package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.api.jsonStructures.AutofillData
import org.anibeaver.anibeaver.core.ParsedAutofillData
import org.anibeaver.anibeaver.core.datastructures.AutofillResultSelection
import org.anibeaver.anibeaver.core.datastructures.Reference
import org.anibeaver.anibeaver.core.datastructures.Schedule
import org.anibeaver.anibeaver.ui.components.references.ReferenceRow

@Composable
fun AutofillPopup(
    show: Boolean,
    references: List<Reference>,
    onAddReference: (Reference) -> Unit,
    onDeleteReference: (Reference) -> Unit,
    onUpdateReference: (Reference, Reference) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (AutofillResultSelection?) -> Unit,
    onConfirmReorder: (List<Reference>) -> Unit,
    onPullFromAniList: (priorityIndex: Int, onPulled: (ParsedAutofillData) -> Unit) -> Unit
) {
    if (!show) return

    var priorityIndex by remember { mutableStateOf(0) }
    var autofillData by remember { mutableStateOf<ParsedAutofillData?>(null) }
    var autofillDataList by remember { mutableStateOf<List<AutofillData>>(emptyList()) }
    var showSelector by remember { mutableStateOf(false) }
    var selectedNameIdx by remember { mutableStateOf(0) }
    var yearRadioIdx by remember { mutableStateOf(0) } // 0 = start, 1 = end

    AlertDialog(
        modifier = Modifier.width(600.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            if (autofillData == null) {
                Button(onClick = { onConfirm(null) }) {
                    Text("Autofill")
                }
            } else {
                AutofillConfirmButton(
                    autofillData = autofillData!!,
                    autofillDataList = autofillDataList,
                    selectedNameIdx = selectedNameIdx,
                    yearRadioIdx = yearRadioIdx,
                    onSelectedNameIdxChange = { selectedNameIdx = it },
                    onYearRadioIdxChange = { yearRadioIdx = it },
                    onDone = { selection ->
                        autofillData = null
                        autofillDataList = emptyList()
                        showSelector = false
                        onConfirm(selection)
                    }
                )
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Close for now") }
        },
        title = { Text("Manage AL Autofill") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Add any number of references here (e.g a single series or all seasons of a series) to automatically extract (common) data and fill in the selected entry inputs. The radio button selects the priority series.")
                references.forEachIndexed { idx, ref ->
                    ReferenceRow(
                        alId = ref.alId,
                        refNote = ref.note,
                        onAlIdChange = { newAlIdStr -> onUpdateReference(ref, Reference(ref.note, newAlIdStr)) },
                        onRefNoteChange = { newNote -> onUpdateReference(ref, Reference(newNote, ref.alId)) },
                        onDelete = { onDeleteReference(ref) },
                        onMoveUp = if (idx > 0) {
                            {
                                val newList = references.toMutableList()
                                newList.removeAt(idx)
                                newList.add(idx - 1, ref)
                                onConfirmReorder(newList)
                            }
                        } else null,
                        onMoveDown = if (idx < references.lastIndex) {
                            {
                                val newList = references.toMutableList()
                                newList.removeAt(idx)
                                newList.add(idx + 1, ref)
                                onConfirmReorder(newList)
                            }
                        } else null,
                        isPriority = idx == priorityIndex,
                        onPrioritySelected = { priorityIndex = idx }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { onAddReference(Reference("", "")) }) { Text("Add Reference") }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {
                        onPullFromAniList(priorityIndex) { data ->
                            autofillData = data
                            showSelector = true
                        }
                    }) { Text("Pull from AniList") }
                }
                if (showSelector && autofillData != null) {
                    AutofillSelectorUI(
                        autofillData!!,
                        autofillDataList,
                        selectedNameIdx,
                        onSelectedNameIdxChange = { selectedNameIdx = it },
                        yearRadioIdx = yearRadioIdx,
                        onYearRadioIdxChange = { yearRadioIdx = it }
                    )
                }
            }
        }
    )
}

@Composable
private fun AutofillSelectorUI(
    autofill: ParsedAutofillData,
    autofillDataList: List<AutofillData>,
    selectedNameIdx: Int,
    onSelectedNameIdxChange: (Int) -> Unit,
    yearRadioIdx: Int,
    onYearRadioIdxChange: (Int) -> Unit
) {
    val nameOptions = listOf(autofill.name_en, autofill.name_rm, autofill.name_jp)
        .filter { it.isNotBlank() }
        .distinct()
    var selectedStudios by remember { mutableStateOf(autofill.studios.toSet()) }
    val allStudios = autofill.studios
    var selectedGenres by remember { mutableStateOf(autofill.genres.toSet()) }
    val allGenres = autofill.genres
    var selectedTags by remember { mutableStateOf(autofill.tags.toSet()) }
    val allTags = autofill.tags
    var coverChecked by remember { mutableStateOf(true) }
    var bannerChecked by remember { mutableStateOf(true) }
    var airingChecked by remember { mutableStateOf(true) }
    val totalEpisodes by remember { mutableStateOf(autofill.eps_total) }
    var epsChecked by remember { mutableStateOf(true) }
    var runtime by remember { mutableStateOf(autofill.runtime) }
    val scrollState = rememberScrollState()


    Box(modifier = Modifier.heightIn(max = 400.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Sync name", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                nameOptions.forEachIndexed { idx, value ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedNameIdx == idx,
                            onClick = { onSelectedNameIdxChange(idx) }
                        )
                        Text(value)
                    }
                }
            }
            Text("Sync year", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = yearRadioIdx == 0,
                    onClick = { onYearRadioIdxChange(0) }
                )
                Text("Start: ${autofill.startYear}")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = yearRadioIdx == 1,
                    onClick = { onYearRadioIdxChange(1) }
                )
                Text("End: ${autofill.endYear}")
            }
            Text("Sync:", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = airingChecked, onCheckedChange = { airingChecked = it })
                    Text("Airing ${autofill.airingScheduleWeekday}")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = coverChecked, onCheckedChange = { coverChecked = it })
                    Text("Cover")
                    Spacer(modifier = Modifier.width(8.dp))
                    Checkbox(checked = bannerChecked, onCheckedChange = { bannerChecked = it })
                    Text("Banner")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = epsChecked, onCheckedChange = { epsChecked = it })
                    Text("Eps. Total: $totalEpisodes")
                }
            }
            SectionWithCheckAll(
                label = "Studios",
                labelBold = true,
                allItems = allStudios,
                selectedItems = selectedStudios,
                onSelectionChange = { selectedStudios = it }
            )
            SectionWithCheckAll(
                label = "Genres",
                labelBold = true,
                allItems = allGenres,
                selectedItems = selectedGenres,
                onSelectionChange = { selectedGenres = it }
            )
            SectionWithCheckAll(
                label = "Suggested tags",
                labelBold = true,
                allItems = allTags,
                selectedItems = selectedTags,
                onSelectionChange = { selectedTags = it }
            )
            Text(
                "Note: the community score is: ${autofill.avg_score}%. The series has a runtime of ${
                    formatMinutes(
                        autofill.runtime
                    )
                }h."
            )
        }
    }
}

fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60 // since both are ints, you get an int
    var minutes = (minutes % 60).toString()
    if (minutes.length < 2) {
        minutes = "0$minutes"
    }

    return ("$hours:$minutes")
}

@Composable
private fun SectionWithCheckAll(
    label: String,
    labelBold: Boolean = false,
    allItems: List<String>,
    selectedItems: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Button(
                onClick = { onSelectionChange(emptySet()) },
                modifier = Modifier.height(32.dp)
            ) { Text("Uncheck all") }
            Button(
                onClick = { onSelectionChange(allItems.toSet()) },
                modifier = Modifier.height(32.dp)
            ) { Text("Check all") }
        }
    }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        allItems.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selectedItems.contains(item),
                    onCheckedChange = { checked ->
                        onSelectionChange(
                            if (checked) selectedItems + item else selectedItems - item
                        )
                    }
                )
                Text(item)
            }
        }
    }
}

@Composable
private fun AutofillConfirmButton(
    autofillData: ParsedAutofillData,
    autofillDataList: List<AutofillData>,
    selectedNameIdx: Int,
    yearRadioIdx: Int,
    onSelectedNameIdxChange: (Int) -> Unit,
    onYearRadioIdxChange: (Int) -> Unit,
    onDone: (AutofillResultSelection) -> Unit
) {
    val nameOptions = listOf(autofillData.name_en, autofillData.name_rm, autofillData.name_jp)
        .filter { it.isNotBlank() }
        .distinct()
    var selectedStudios by remember { mutableStateOf(autofillData.studios.toSet()) }
    var selectedGenres by remember { mutableStateOf(autofillData.genres.toSet()) }
    var selectedTags by remember { mutableStateOf(autofillData.tags.toSet()) }
    var coverChecked by remember { mutableStateOf(true) }
    var bannerChecked by remember { mutableStateOf(true) }
    var airingChecked by remember { mutableStateOf(true) }
    val totalEpisodes = autofillData.eps_total
    var epsChecked by remember { mutableStateOf(true) }
    Button(onClick = {
        val year = if (yearRadioIdx == 0) autofillData.startYear else autofillData.endYear
        val selection = AutofillResultSelection(
            name = nameOptions.getOrNull(selectedNameIdx) ?: "",
            year = year,
            studios = selectedStudios.toList(),
            genres = selectedGenres.toList(),
            tags = selectedTags.toList(),
            cover = if (coverChecked) autofillData.cover_link else null,
            banner = if (bannerChecked) autofillData.banner_link else null,
            airingSchedule = if (airingChecked) autofillData.airingScheduleWeekday else Schedule.Monday,
            episodes = if (epsChecked) totalEpisodes else null
        )
        onDone(selection)
    }) {
        Text("Update Entry With These")
    }
}
