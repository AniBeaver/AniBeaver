package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.ParsedAutofillData
import org.anibeaver.anibeaver.core.datastructures.Reference
import org.anibeaver.anibeaver.core.datastructures.Schedule
import org.anibeaver.anibeaver.ui.components.references.ReferenceRow
import androidx.compose.foundation.layout.FlowRow

@Composable
fun AutofillPullPopup(
    show: Boolean,
    references: List<Reference>,
    onAddReference: (Reference) -> Unit,
    onDeleteReference: (Reference) -> Unit,
    onUpdateReference: (Reference, Reference) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (priorityReference: Reference?) -> Unit,
    onConfirmReorder: (List<Reference>) -> Unit,
    onPullFromAniList: (priorityIndex: Int, onPulled: (ParsedAutofillData) -> Unit) -> Unit
) {
    if (!show) return

    var priorityIndex by remember { mutableStateOf(0) }
    var autofillData by remember { mutableStateOf<ParsedAutofillData?>(null) }
    var showSelector by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = Modifier.width(600.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            if (autofillData == null) {
                Button(onClick = { onConfirm(references.getOrNull(priorityIndex)) }) {
                    Text("Autofill")
                }
            } else {
                AutofillConfirmButton(
                    autofillData = autofillData!!,
                    onDone = {
                        autofillData = null
                        showSelector = false
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
                    AutofillSelectorUI(autofillData!!)
                }
            }
        }
    )
}

@Composable
private fun AutofillSelectorUI(autofill: ParsedAutofillData) {
    val nameOptions = listOfNotNull(
        autofill.name_en_choices.firstOrNull(),
        autofill.name_rm_choices.firstOrNull(),
        autofill.name_jp_choices.firstOrNull()
    )
    var selectedNameIdx by remember { mutableStateOf(0) }
    var yearChecked by remember { mutableStateOf(true) }
    var selectedStudios by remember { mutableStateOf(autofill.studios.toSet()) }
    val allStudios = autofill.studios
    var selectedGenres by remember { mutableStateOf(autofill.genres.toSet()) }
    val allGenres = autofill.genres
    var selectedTags by remember { mutableStateOf(autofill.tags.toSet()) }
    val allTags = autofill.tags
    var coverChecked by remember { mutableStateOf(true) }
    var bannerChecked by remember { mutableStateOf(true) }
    var airingChecked by remember { mutableStateOf(true) }
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
                            onClick = { selectedNameIdx = idx }
                        )
                        Text(value)
                    }
                }
            }
            Text("Sync:", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = yearChecked, onCheckedChange = { yearChecked = it })
                    Text("Year: ${autofill.startYear}")
                }
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
            Text("Note: the community score is: ${autofill.avg_score}. This series finished airing at ${autofill.endYear}")
        }
    }
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
            Button(onClick = { onSelectionChange(emptySet()) }, modifier = Modifier.height(32.dp)) { Text("Uncheck all") }
            Button(onClick = { onSelectionChange(allItems.toSet()) }, modifier = Modifier.height(32.dp)) { Text("Check all") }
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
    onDone: () -> Unit
) {
    val nameOptions = listOfNotNull(
        autofillData.name_en_choices.firstOrNull(),
        autofillData.name_rm_choices.firstOrNull(),
        autofillData.name_jp_choices.firstOrNull()
    )
    var selectedNameIdx by remember { mutableStateOf(0) }
    var yearChecked by remember { mutableStateOf(true) }
    var selectedStudios by remember { mutableStateOf(autofillData.studios.toSet()) }
    var selectedGenres by remember { mutableStateOf(autofillData.genres.toSet()) }
    var selectedTags by remember { mutableStateOf(autofillData.tags.toSet()) }
    var coverChecked by remember { mutableStateOf(true) }
    var bannerChecked by remember { mutableStateOf(true) }
    var airingChecked by remember { mutableStateOf(true) }
    Button(onClick = {
        val selection = AutofillResultSelection(
            selectedName = nameOptions.getOrNull(selectedNameIdx) ?: "",
            year = if (yearChecked) autofillData.startYear else null,
            studios = selectedStudios.toList(),
            genres = selectedGenres.toList(),
            tags = selectedTags.toList(),
            cover = if (coverChecked) autofillData.cover_link else null,
            banner = if (bannerChecked) autofillData.banner_link else null,
            airingSchedule = if (airingChecked) autofillData.airingScheduleWeekday else Schedule.Monday
        )
        onDone()
        // TODO: Call a parent callback with selection - to fill in in the edit entry dialogue (main thing)
    }) {
        Text("Update Entry With These")
    }
}

data class AutofillResultSelection(
    val selectedName: String,
    val year: Int?,
    val studios: List<String>,
    val genres: List<String>,
    val tags: List<String>,
    val cover: String?,
    val banner: String?,
    val airingSchedule: Schedule
)
