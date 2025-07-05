package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
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
import org.anibeaver.anibeaver.core.datastructures.Entry
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.core.datastructures.Schedule
import org.anibeaver.anibeaver.ui.components.basic.FloatPicker
import org.anibeaver.anibeaver.ui.components.basic.SimpleDropdown
import org.anibeaver.anibeaver.ui.components.tag_chips.TagChipInput
import org.anibeaver.anibeaver.ui.components.basic.ImageInput
import org.anibeaver.anibeaver.ui.components.basic.YearPicker

//TODO: tiny windows not supported still
@Composable
fun EditEntryPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Entry) -> Unit,
    initialEntry: Entry? = null
) {
    var animeName by remember { mutableStateOf(initialEntry?.animeName ?: "") }
    var releaseYear by remember { mutableStateOf(initialEntry?.releaseYear ?: "2010") }
    var studioIds by remember { mutableStateOf(initialEntry?.studioIds ?: emptyList()) }
    var genreIds by remember { mutableStateOf(initialEntry?.genreIds ?: emptyList()) }
    var description by remember { mutableStateOf(initialEntry?.description ?: "") }
    var rating by remember { mutableStateOf(initialEntry?.rating ?: 8.5f) }
    var status by remember { mutableStateOf(initialEntry?.status ?: Status.Planning) }
    var releasingEvery by remember { mutableStateOf(initialEntry?.releasingEvery ?: Schedule.Monday) }
    var tagsIds by remember { mutableStateOf(initialEntry?.tagIds ?: emptyList()) }
    var showNewTagPopup by remember { mutableStateOf(false) }
    var newTagInitialType by remember { mutableStateOf(TagType.CUSTOM) }

    // Reset fields when initialEntry changes (for editing)
    LaunchedEffect(initialEntry) {
        animeName = initialEntry?.animeName ?: ""
        releaseYear = initialEntry?.releaseYear ?: "2010"
        studioIds = initialEntry?.studioIds ?: emptyList()
        genreIds = initialEntry?.genreIds ?: emptyList()
        description = initialEntry?.description ?: ""
        rating = initialEntry?.rating ?: 8.5f
        status = initialEntry?.status ?: Status.Planning
        releasingEvery = initialEntry?.releasingEvery ?: Schedule.Monday
        tagsIds = initialEntry?.tagIds ?: emptyList()
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

    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = {
                    onConfirm(
                        Entry(
                            animeName = animeName,
                            releaseYear = releaseYear,
                            studioIds = studioIds,
                            genreIds = genreIds,
                            description = description,
                            rating = rating,
                            status = status,
                            releasingEvery = releasingEvery,
                            tagIds = tagsIds,
                            id = initialEntry?.id ?: 0
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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImageInput(
                            modifier = Modifier.size(96.dp).padding(end = 24.dp),
                            onClick = { /* TODO: Handle image selection */ }
                        )
                        FloatPicker(
                            value = rating,
                            onValueChange = { rating = it },
                            label = "Rating"
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = animeName,
                            onValueChange = { animeName = it },
                            label = { Text("Anime Name") },
                            modifier = Modifier.weight(1f).focusRequester(animeNameRequester).focusProperties { next = releaseYearRequester },
                            singleLine = true
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
                            modifier = Modifier.weight(1f).focusRequester(releaseYearRequester).focusProperties { next = genreRequester },
                            label = "Year"
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        SimpleDropdown(
                            options = Status.entries.toList(),
                            selectedOption = status,
                            onOptionSelected = { status = it },
                            label = "Status",
                            modifier = Modifier.weight(1f).focusRequester(statusRequester).focusProperties { next = releasingEveryRequester }
                        )
                        SimpleDropdown(
                            options = Schedule.entries.toList(),
                            selectedOption = releasingEvery,
                            onOptionSelected = { releasingEvery = it },
                            label = "Releasing Every",
                            modifier = Modifier.weight(1f).focusRequester(releasingEveryRequester).focusProperties { next = descriptionRequester }
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
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).focusRequester(studioNameRequester),
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
                            val newId = org.anibeaver.anibeaver.core.TagsController.addTag(name, color, type)
                            when (type) {
                                TagType.GENRE -> genreIds = genreIds + newId
                                TagType.STUDIO -> studioIds = studioIds + newId
                                TagType.CUSTOM -> tagsIds = tagsIds + newId
                            }
                            showNewTagPopup = false
                        },
                        initialType = newTagInitialType
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp).height(120.dp).focusRequester(descriptionRequester),
                        maxLines = 5
                    )
                }
            }
        )
    }
}