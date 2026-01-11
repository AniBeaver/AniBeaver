package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.api.jsonStructures.AutofillData
import org.anibeaver.anibeaver.core.AutofillController
import org.anibeaver.anibeaver.core.ParsedAutofillData
import org.anibeaver.anibeaver.core.datastructures.AutofillResultSelection
import org.anibeaver.anibeaver.core.datastructures.Reference
import org.anibeaver.anibeaver.core.datastructures.ReleaseSchedule
import org.anibeaver.anibeaver.ui.components.references.ReferenceRow
import org.anibeaver.anibeaver.ui.components.showConfirmation

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
    onPullFromAniList: (Int, (ParsedAutofillData) -> Unit) -> Unit,
    forManga: Boolean
) {
    var priorityIndex by remember(references) {
        mutableStateOf(references.indexOfFirst { it.isPriority }.takeIf { it >= 0 } ?: 0)
    }
    var autofillData by remember { mutableStateOf<ParsedAutofillData?>(null) }
    var autofillDataList by remember { mutableStateOf<List<AutofillData>>(emptyList()) }
    var showSelector by remember { mutableStateOf(false) }
    var selectedNameIdx by remember { mutableStateOf(0) }
    var yearRadioIdx by remember { mutableStateOf(0) }

    var selectedStudios by remember { mutableStateOf(emptySet<String>()) }
    var selectedAuthor by remember { mutableStateOf(emptySet<String>()) }
    var selectedGenres by remember { mutableStateOf(emptySet<String>()) }
    var selectedTags by remember { mutableStateOf(emptySet<String>()) }
    var coverChecked by remember { mutableStateOf(true) }
    var bannerChecked by remember { mutableStateOf(true) }
    var airingChecked by remember { mutableStateOf(true) }
    var epsChecked by remember { mutableStateOf(true) }
    var nameChecked by remember { mutableStateOf(true) }
    var yearChecked by remember { mutableStateOf(true) }

    var hasEverPulled by remember { mutableStateOf(false) }
    var isOutdated by remember { mutableStateOf(false) }
    var lastReferencesSnapshot by remember { mutableStateOf(references) }
    var lastPriorityIndex by remember { mutableStateOf(priorityIndex) }

    if (!show) return

    val totalEpisodes = autofillData?.eps_total ?: 0
//    val runtime = autofillData?.runtime ?: 0 // FIXME: why this unused?

    LaunchedEffect(references, priorityIndex) {
        if (autofillData != null) {
            val currentValidIds = references
                .filter { it.alId.isNotBlank() && AutofillController.idIsValid(it.alId) }
                .map { it.alId }
                .toSet()

            val previousValidIds = lastReferencesSnapshot
                .filter { it.alId.isNotBlank() && AutofillController.idIsValid(it.alId) }
                .map { it.alId }
                .toSet()

            val priorityChanged = priorityIndex != lastPriorityIndex
            val idsChanged = currentValidIds != previousValidIds

            if (idsChanged || priorityChanged) {
                isOutdated = true
            }
        }
        lastReferencesSnapshot = references
        lastPriorityIndex = priorityIndex
    }

    fun onPull(data: ParsedAutofillData) {
        autofillData = data
        showSelector = true
        isOutdated = false
        lastReferencesSnapshot = references
        lastPriorityIndex = priorityIndex
        if (!hasEverPulled) {
            hasEverPulled = true
            selectedStudios = data.studios.filter { it.isNotBlank() }.toSet()
            selectedAuthor = data.author.filter { it.isNotBlank() }.toSet()
        }
    }

    LaunchedEffect(show) {
        if (show && autofillData == null && references.isNotEmpty() &&
            references.any { it.alId.isNotBlank() && AutofillController.idIsValid(it.alId) }
        ) {
            onPullFromAniList(priorityIndex) { data -> onPull(data) }
        }
    }

    val nameOptions =
        autofillData?.let { listOf(it.name_en, it.name_rm, it.name_jp).filter { name -> name.isNotBlank() }.distinct() }
            ?: emptyList()

    AlertDialog(
        modifier = Modifier.width(600.dp),
        onDismissRequest = {},
        confirmButton = {

            if (autofillData != null) {
                Button(
                    onClick = {
                        val year = if (yearRadioIdx == 0) autofillData!!.startYear else autofillData!!.endYear
                        val name = nameOptions.getOrNull(selectedNameIdx) ?: ""
                        val selection = AutofillResultSelection(
                            year = year,
                            name = name,
                            studios = selectedStudios.toList(),
                            author = selectedAuthor.toList(),
                            genres = selectedGenres.toList(),
                            tags = selectedTags.toList(),
                            cover = if (coverChecked) autofillData!!.cover_link else null,
                            banner = if (bannerChecked) autofillData!!.banner_link else null,
                            airingSchedule = if (airingChecked) autofillData!!.airingScheduleWeekday else ReleaseSchedule.Monday,
                            episodes = if (epsChecked) totalEpisodes else null
                        )
                        showConfirmation(
                            message = "Apply autofill data to this entry? This will overwrite existing data.",
                            onAccept = {
                                onConfirm(selection)
                            }
                        )
                    },
                    enabled = !isOutdated
                ) {
                    Text("Update Entry With These")
                }
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
                    key(idx) {
                        ReferenceRow(
                            alId = ref.alId,
                            refNote = ref.note,
                            cachedName = ref.name,
                            onAlIdChange = { newAlIdStr ->
                                val newList = references.toMutableList()
                                val current = newList[idx]
                                newList[idx] = Reference(current.note, newAlIdStr, current.name, current.isPriority)
                                onConfirmReorder(newList)
                            },
                            onRefNoteChange = { newNote ->
                                val newList = references.toMutableList()
                                val current = newList[idx]
                                newList[idx] = Reference(newNote, current.alId, current.name, current.isPriority)
                                onConfirmReorder(newList)
                            },
                            onNameChange = { newName ->
                                val newList = references.toMutableList()
                                val current = newList[idx]
                                newList[idx] = Reference(current.note, current.alId, newName, current.isPriority)
                                onConfirmReorder(newList)
                            },
                            onAlIdAndNameChange = { newAlId, newName ->
                                val newList = references.toMutableList()
                                val current = newList[idx]
                                newList[idx] = Reference(current.note, newAlId, newName, current.isPriority)
                                onConfirmReorder(newList)
                            },
                            onDelete = {
                                val newList = references.toMutableList()
                                newList.removeAt(idx)
                                onConfirmReorder(newList)
                            },
                            onMoveUp = if (idx > 0) {
                                {
                                    val newList = references.toMutableList()
                                    val item = newList.removeAt(idx)
                                    newList.add(idx - 1, item)
                                    onConfirmReorder(newList)
                                }
                            } else null,
                            onMoveDown = if (idx < references.lastIndex) {
                                {
                                    val newList = references.toMutableList()
                                    val item = newList.removeAt(idx)
                                    newList.add(idx + 1, item)
                                    onConfirmReorder(newList)
                                }
                            } else null,
                            isPriority = idx == priorityIndex,
                            onPrioritySelected = {
                                priorityIndex = idx
                                val updatedList = references.mapIndexed { i, ref ->
                                    ref.copy(isPriority = i == idx)
                                }
                                onConfirmReorder(updatedList)
                            },
                            forManga = forManga
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        onAddReference(
                            Reference(
                                note = "Season",
                                alId = "",
                                name = ""
                            )
                        )
                    }) { Text("Add Reference") }
                    Spacer(modifier = Modifier.weight(1f))
                    if (isOutdated) {
                        Text(
                            "Outdated information.\nPlease re-pull:",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Right,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Button(
                        onClick = {
                            onPullFromAniList(priorityIndex) { data -> onPull(data) }
                        },
                        enabled = references.isNotEmpty() &&
                                references.any { it.alId.isNotBlank() } &&
                                references.all { it.alId.isBlank() || AutofillController.idIsValid(it.alId) },
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text("Pull from AniList")
                    }
                }
                if (showSelector && autofillData != null) {
                    AutofillSelectorUI(
                        autofillData!!,
                        autofillDataList,
                        selectedNameIdx,
                        onSelectedNameIdxChange = { selectedNameIdx = it },
                        yearRadioIdx = yearRadioIdx,
                        onYearRadioIdxChange = { yearRadioIdx = it },
                        nameOptions = nameOptions,
                        selectedStudios = selectedStudios,
                        onSelectedStudiosChange = { selectedStudios = it },
                        selectedAuthor = selectedAuthor,
                        onSelectedAuthorChange = { selectedAuthor = it },
                        selectedGenres = selectedGenres,
                        onSelectedGenresChange = { selectedGenres = it },
                        selectedTags = selectedTags,
                        onSelectedTagsChange = { selectedTags = it },
                        coverChecked = coverChecked,
                        onCoverCheckedChange = { coverChecked = it },
                        bannerChecked = bannerChecked,
                        onBannerCheckedChange = { bannerChecked = it },
                        airingChecked = airingChecked,
                        onAiringCheckedChange = { airingChecked = it },
                        epsChecked = epsChecked,
                        onEpsCheckedChange = { epsChecked = it },
                        totalEpisodes = totalEpisodes,
                        nameChecked = nameChecked,
                        onNameCheckedChange = { nameChecked = it },
                        yearChecked = yearChecked,
                        onYearCheckedChange = { yearChecked = it },
                        forManga = forManga,
                        isOutdated = isOutdated
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
    onYearRadioIdxChange: (Int) -> Unit,
    nameOptions: List<String>,
    selectedStudios: Set<String>,
    onSelectedStudiosChange: (Set<String>) -> Unit,
    selectedAuthor: Set<String>,
    onSelectedAuthorChange: (Set<String>) -> Unit,
    selectedGenres: Set<String>,
    onSelectedGenresChange: (Set<String>) -> Unit,
    selectedTags: Set<String>,
    onSelectedTagsChange: (Set<String>) -> Unit,
    coverChecked: Boolean,
    onCoverCheckedChange: (Boolean) -> Unit,
    bannerChecked: Boolean,
    onBannerCheckedChange: (Boolean) -> Unit,
    airingChecked: Boolean,
    onAiringCheckedChange: (Boolean) -> Unit,
    epsChecked: Boolean,
    onEpsCheckedChange: (Boolean) -> Unit,
    totalEpisodes: Int,
    nameChecked: Boolean,
    onNameCheckedChange: (Boolean) -> Unit,
    yearChecked: Boolean,
    onYearCheckedChange: (Boolean) -> Unit,
    forManga: Boolean,
    isOutdated: Boolean
) {

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.heightIn(max = 400.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(8.dp)
                .alpha(if (isOutdated) 0.5f else 1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "Which of the pulled data to sync?",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.offset(x = (-10).dp)) {
                Checkbox(checked = nameChecked, onCheckedChange = { checked -> onNameCheckedChange(checked) })
                Text("Sync name:", style = MaterialTheme.typography.titleMedium)
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                nameOptions.forEachIndexed { idx, value ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedNameIdx == idx,
                            onClick = { onSelectedNameIdxChange(idx) },
                            enabled = nameChecked
                        )
                        Text(value)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.offset(x = (-10).dp)) {
                Checkbox(checked = yearChecked, onCheckedChange = { checked -> onYearCheckedChange(checked) })
                Text("Sync year:", style = MaterialTheme.typography.titleMedium)
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = yearRadioIdx == 0,
                        onClick = { onYearRadioIdxChange(0) },
                        enabled = yearChecked
                    )
                    Text("Start: ${autofill.startYear}")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = yearRadioIdx == 1,
                        onClick = { onYearRadioIdxChange(1) },
                        enabled = yearChecked
                    )
                    Text("End: ${autofill.endYear}")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Text("General:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        onAiringCheckedChange(false)
                        onCoverCheckedChange(false)
                        onBannerCheckedChange(false)
                        onEpsCheckedChange(false)
                    },
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Uncheck all")
                }
                Button(
                    onClick = {
                        onAiringCheckedChange(true)
                        onCoverCheckedChange(true)
                        onBannerCheckedChange(true)
                        onEpsCheckedChange(true)
                    },
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Check all")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = coverChecked, onCheckedChange = { checked -> onCoverCheckedChange(checked) })
                    Text("Cover")
                    Spacer(modifier = Modifier.width(8.dp))
                    Checkbox(checked = bannerChecked, onCheckedChange = { checked -> onBannerCheckedChange(checked) })
                    Text("Banner")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = epsChecked, onCheckedChange = { checked -> onEpsCheckedChange(checked) })
                    Text((if (forManga) "Ch. Total:" else "Eps. Total:") + " $totalEpisodes")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = airingChecked, onCheckedChange = { checked -> onAiringCheckedChange(checked) })
                    Text("Releasing ${autofill.airingScheduleWeekday}")
                }
            }
            if (!forManga) {
                SectionWithCheckAll(
                    label = "Studios",
                    labelBold = true,
                    allItems = autofill.studios,
                    selectedItems = selectedStudios,
                    onSelectionChange = onSelectedStudiosChange
                )
            } else {
                SectionWithCheckAll(
                    label = "Authors",
                    labelBold = true,
                    allItems = autofill.author,
                    selectedItems = selectedAuthor,
                    onSelectionChange = onSelectedAuthorChange
                )
            }
            SectionWithCheckAll(
                label = "Genres",
                labelBold = true,
                allItems = autofill.genres,
                selectedItems = selectedGenres,
                onSelectionChange = onSelectedGenresChange
            )
            SectionWithCheckAll(
                label = "Suggested tags",
                labelBold = true,
                allItems = autofill.tags, //TODO genres = suggested tags for some reason FIXME !!!
                selectedItems = selectedTags,
                onSelectionChange = onSelectedTagsChange
            )
            Text(
                "Note: the community score is: ${formatOneDecimal(autofill.avg_score)}%. The " + if (forManga) "manga has a total of ${autofill.runtime} volumes." else "anime has a total runtime of ~${
                    formatMinutes(
                        autofill.runtime
                    )
                }h."
            )
        }
    }
}

fun formatMinutes(minutes: Int): String {
    val hours = minutes / 60
    var minutes = (minutes % 60).toString()
    if (minutes.length < 2) {
        minutes = "0$minutes"
    }

    return ("$hours:$minutes")
}

fun formatOneDecimal(value: Float): String {
    val rounded = (value * 10).toInt() / 10.0
    return if (rounded % 1.0 == 0.0) {
        "${rounded.toInt()}.0"
    } else {
        rounded.toString()
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
