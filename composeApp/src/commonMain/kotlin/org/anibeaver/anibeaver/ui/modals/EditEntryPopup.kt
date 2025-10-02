package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.core.AutofillController
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.*
import org.anibeaver.anibeaver.ui.components.basic.*
import org.anibeaver.anibeaver.ui.components.tag_chips.TagChipInput

//TODO: tiny windows not supported still
@Composable
fun EditEntryPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (EntryData) -> Unit,
    forceShowAutofillPopup: Boolean,
    alIdToBePassed: String, //only if forceShowAutofillPopup
    initialValues: EntryData? = null,
    dataWrapper: DataWrapper
) {

    var animeName by remember { mutableStateOf(initialValues?.animeName ?: "") }
    var releaseYear by remember { mutableStateOf(initialValues?.releaseYear ?: "2010") }
    var studioIds by remember { mutableStateOf(initialValues?.studioIds ?: emptyList()) }
    var genreIds by remember { mutableStateOf(initialValues?.genreIds ?: emptyList()) }
    var description by remember { mutableStateOf(initialValues?.description ?: "") }
    var rating by remember { mutableStateOf(initialValues?.rating ?: 8.5f) }
    var status by remember { mutableStateOf(initialValues?.status ?: Status.Planning) }
    var releasingEvery by remember { mutableStateOf(initialValues?.releasingEvery ?: Schedule.Monday) }
    var tagsIds by remember { mutableStateOf(initialValues?.tagIds ?: emptyList()) }
    var references by remember { mutableStateOf(initialValues?.references ?: emptyList()) }
    var showNewTagPopup by remember { mutableStateOf(false) }
    var newTagInitialType by remember { mutableStateOf(TagType.CUSTOM) }
    var showAutofillPopup by remember { mutableStateOf(false) }
    val onManageAutofillClicked = { showAutofillPopup = true }
    var episodesTotal by remember { mutableStateOf(initialValues?.episodesTotal ?: 1) }
    var episodesProgress by remember { mutableStateOf(initialValues?.episodesProgress ?: 0) }
    var rewatches by remember { mutableStateOf(initialValues?.rewatches ?: 1) }

    // Reset fields when initialValues changes (for editing)
    LaunchedEffect(initialValues) {
        animeName = initialValues?.animeName ?: ""
        releaseYear = initialValues?.releaseYear ?: "2010"
        studioIds = initialValues?.studioIds ?: emptyList()
        genreIds = initialValues?.genreIds ?: emptyList()
        description = initialValues?.description ?: ""
        rating = initialValues?.rating ?: 8.5f
        status = initialValues?.status ?: Status.Planning
        releasingEvery = initialValues?.releasingEvery ?: Schedule.Monday
        tagsIds = initialValues?.tagIds ?: emptyList()
        references = initialValues?.references ?: emptyList()
        episodesTotal = initialValues?.episodesTotal ?: 0
        episodesProgress = initialValues?.episodesProgress ?: 0
        rewatches = initialValues?.rewatches ?: 1

        if (forceShowAutofillPopup) {
            showAutofillPopup = true
            if (references.isEmpty()) references = listOf(Reference("Se1", alIdToBePassed))
        }
    }

    //for tab navigation
    val focusManager = LocalFocusManager.current
    val animeNameRequester = remember { FocusRequester() }
    val releaseYearRequester = remember { FocusRequester() }
    val genreRequester = remember { FocusRequester() }
    val studioNameRequester = remember { FocusRequester() }
    val tagsRequester = remember { FocusRequester() }
    val statusRequester = remember { FocusRequester() }
    val releasingEveryRequester = remember { FocusRequester() }
    val descriptionRequester = remember { FocusRequester() }


    fun applyTagToThisEntry(tagId: Int, tagType: TagType) {
        when (tagType) {
            TagType.GENRE -> if (tagId !in genreIds) genreIds = genreIds + tagId
            TagType.STUDIO -> if (tagId !in studioIds) studioIds = studioIds + tagId
            TagType.CUSTOM -> if (tagId !in tagsIds) tagsIds = tagsIds + tagId
        }
    }

    fun applyAutofillSelection(selection: AutofillResultSelection) {
        fun massCreateAndApplyTags(tagList: List<String>, newTagType: TagType) {
            for (newTagName in tagList) {
                //TODO: in other places: maybe don't allow creating a tag by the same name as an existing one; Also add tag searching/sorting in tag menu (UI)

                val newTagId = TagsController.safeCreateTagByName(newTagName, "#ffffff", newTagType)
                applyTagToThisEntry(newTagId, newTagType)
            }
        }

        println(selection)
        animeName = selection.name
        releaseYear = selection.year?.toString() ?: ""
        releasingEvery = selection.airingSchedule
        if (selection.episodes != null) {
            episodesTotal = selection.episodes
        }
        //TODO: banner and cover
        //TODO: Studio, genre and custom tag creation
        massCreateAndApplyTags(selection.genres, TagType.GENRE)
        massCreateAndApplyTags(selection.studios, TagType.STUDIO)
        massCreateAndApplyTags(selection.tags, TagType.CUSTOM)

    }


    if (show) {
        val coroutineScope = rememberCoroutineScope()
        if (showAutofillPopup) {
            AutofillPopup(
                show = showAutofillPopup,
                references = references,
                onAddReference = { newRef -> references = references + newRef },
                onDeleteReference = { ref -> references = references.filter { it != ref } },
                onUpdateReference = { oldRef, newRef ->
                    references = references.map { if (it == oldRef) newRef else it }
                },
                onDismiss = { showAutofillPopup = false },
                onConfirm = { selection ->
                    showAutofillPopup = false
                    selection?.let { applyAutofillSelection(it) }
                },
                onConfirmReorder = { newList -> references = newList },
                autoTriggerPull = forceShowAutofillPopup,
                onPullFromAniList = { priorityIndex, onPulled ->
                    val referenceIds = references.map { it.alId }
                    AutofillController.pullParsedAutofill(
                        referenceIds,
                        { result -> onPulled(result) },
                        dataWrapper,
                        coroutineScope,
                        priorityIndex
                    )
                }
            )
        }
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = {
                    onConfirm(
                        EntryData(
                            animeName = animeName,
                            releaseYear = releaseYear,
                            studioIds = studioIds,
                            genreIds = genreIds,
                            description = description,
                            rating = rating,
                            status = status,
                            releasingEvery = releasingEvery,
                            tagIds = tagsIds,
                            references = references,
                            coverArt = Art("", ""),
                            bannerArt = Art("", ""),
                            episodesTotal = episodesTotal,
                            episodesProgress = episodesProgress,
                            rewatches = rewatches
                        )
                    )
                }) {
                    Text("Confirm/Create")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Dismiss/Close")
                }
            },
            title = { Text("Edit Entry") },
            text = {
                Column(
                    Modifier.fillMaxSize()
                        .verticalScroll(
                            rememberScrollState()
                        )
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.padding(end = 24.dp)) {
                                ImageInput(
                                    modifier = Modifier.size(width = 96.dp, height = 96.dp),
                                    onClick = { /* TODO: Handle image selection */ }
                                )
                                ImageInput(
                                    modifier = Modifier.size(width = 96.dp, height = 32.dp).padding(top = 8.dp),
                                    onClick = { /* TODO: Handle image selection */ }
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = animeName,
                                    onValueChange = { animeName = it },
                                    label = { Text("Anime Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FloatPicker(
                                        value = rating,
                                        onValueChange = { rating = it },
                                        label = "Rating",
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Button(onClick = onManageAutofillClicked, modifier = Modifier.weight(1f)) {
                                        Text("Manage AL Autofill")
                                    }
                                }
                            }
                        }
                        //TODO: add smart interactions: e.g setting to Completed should set episode progress to episode count - or maybe not, because of potential rewatches). So maybe not after all.
                        Row(
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IntPicker(
                                value = episodesProgress,
                                onValueChange = { episodesProgress = it },
                                onIncrement = { episodesProgress += 1 },
                                onDecrement = { episodesProgress = (episodesProgress - 1).coerceAtLeast(0) },
                                label = "Ep. Progress",
                                modifier = Modifier.weight(1f)
                            )
                            IntPicker(
                                value = episodesTotal,
                                onValueChange = { episodesTotal = it },
                                onIncrement = { episodesTotal += 1 },
                                onDecrement = { episodesTotal = (episodesTotal - 1).coerceAtLeast(0) },
                                label = "Eps Total",
                                modifier = Modifier.weight(1f)
                            )
                            IntPicker(
                                value = rewatches,
                                onValueChange = { rewatches = it },
                                onIncrement = { rewatches += 1 },
                                onDecrement = { rewatches = (rewatches - 1).coerceAtLeast(0) },
                                label = "Rewatches",
                                modifier = Modifier.weight(1f)
                            )

                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            SimpleDropdown(
                                options = Status.entries.toList(),
                                selectedOption = status,
                                onOptionSelected = { status = it },
                                label = "Status",
                                modifier = Modifier.weight(1f).focusRequester(statusRequester)
                                    .focusProperties { next = releasingEveryRequester }
                            )
                            SimpleDropdown(
                                options = Schedule.entries.toList(),
                                selectedOption = releasingEvery,
                                onOptionSelected = { releasingEvery = it },
                                label = "Airing every",
                                modifier = Modifier.weight(1f).focusRequester(releasingEveryRequester)
                                    .focusProperties { next = descriptionRequester }
                            )

                            YearPicker(
                                value = releaseYear,
                                onValueChange = { releaseYear = it },
                                onIncrement = {
                                    val year = releaseYear.toIntOrNull() ?: 0
                                    if (year < 9999) releaseYear = (year + 1).toString()
                                },
                                onDecrement = {
                                    val year = releaseYear.toIntOrNull() ?: 0
                                    if (year > 0) releaseYear = (year - 1).toString()
                                },
                                modifier = Modifier.weight(1f),
                                label = "Year"
                            )

                        }
                        TagChipInput(
                            tags = genreIds,
                            onTagsChange = { genreIds = it },
                            tagType = TagType.GENRE,
                            label = "Genre",
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).focusRequester(genreRequester),
                            onCreateTagClick = {
                                newTagInitialType = TagType.GENRE
                                showNewTagPopup = true
                            },
                            surfaceColor = null
                        )
                        TagChipInput(
                            tags = studioIds,
                            onTagsChange = { studioIds = it },
                            tagType = TagType.STUDIO,
                            label = "Studio",
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                .focusRequester(studioNameRequester),
                            onCreateTagClick = {
                                newTagInitialType = TagType.STUDIO
                                showNewTagPopup = true
                            },
                            surfaceColor = null
                        )
                        // Custom Tag Input
                        TagChipInput(
                            tags = tagsIds,
                            onTagsChange = { tagsIds = it },
                            tagType = TagType.CUSTOM,
                            label = "Tags",
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).focusRequester(tagsRequester),
                            onCreateTagClick = {
                                newTagInitialType = TagType.CUSTOM
                                showNewTagPopup = true
                            },
                            surfaceColor = null
                        )
                        NewTagPopup(
                            show = showNewTagPopup,
                            onDismiss = { showNewTagPopup = false },
                            onConfirm = { name, color, type ->
                                val newId = TagsController.addTag(name, color, type)
                                applyTagToThisEntry(newId, type)
                                showNewTagPopup = false
                            },
                            initialType = newTagInitialType
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp).height(120.dp)
                                .focusRequester(descriptionRequester),
                            maxLines = 5
                        )
                    }
                }
            }
        )
    }
}