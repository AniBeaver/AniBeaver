package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.model.Entry
import org.anibeaver.anibeaver.ui.components.NumberPicker
import org.anibeaver.anibeaver.ui.components.SimpleDropdown
import org.anibeaver.anibeaver.ui.components.TagInput
import org.anibeaver.anibeaver.ui.components.ImageInput

@Composable
fun EditEntryPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Entry) -> Unit,
    initialEntry: Entry? = null
) {
    var animeName by remember { mutableStateOf(initialEntry?.animeName ?: "") }
    var releaseYear by remember { mutableStateOf(initialEntry?.releaseYear ?: "") }
    var studioName by remember { mutableStateOf(initialEntry?.studioName ?: "") }
    var genre by remember { mutableStateOf(initialEntry?.genre ?: "") }
    var description by remember { mutableStateOf(initialEntry?.description ?: "") }
    var rating by remember { mutableStateOf(initialEntry?.rating ?: 0f) }
    var status by remember { mutableStateOf(initialEntry?.status ?: "") }
    var releasingEvery by remember { mutableStateOf(initialEntry?.releasingEvery ?: "") }
    var tags by remember { mutableStateOf(initialEntry?.tags ?: "") }

    // Reset fields when initialEntry changes (for editing)
    LaunchedEffect(initialEntry) {
        animeName = initialEntry?.animeName ?: ""
        releaseYear = initialEntry?.releaseYear ?: ""
        studioName = initialEntry?.studioName ?: ""
        genre = initialEntry?.genre ?: ""
        description = initialEntry?.description ?: ""
        rating = initialEntry?.rating ?: 0f
        status = initialEntry?.status ?: ""
        releasingEvery = initialEntry?.releasingEvery ?: ""
        tags = initialEntry?.tags ?: ""
    }

    // FocusRequesters for tab navigation
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
                            id = 0, // id will be set by EntriesController
                            animeName = animeName,
                            releaseYear = releaseYear,
                            studioName = studioName,
                            genre = genre,
                            description = description,
                            rating = rating,
                            status = status,
                            releasingEvery = releasingEvery,
                            tags = tags
                        )
                    )
                }) {
                    Text("Confirm")
                }
            },
            title = { Text("Edit Entry") },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImageInput(
                            modifier = Modifier.size(96.dp).padding(end = 24.dp),
                            onClick = { /* TODO: Handle image selection */ }
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Rating", modifier = Modifier.padding(bottom = 4.dp))
                            NumberPicker(
                                value = rating,
                                onValueChange = { rating = it }
                            )
                        }
                    }
                    // Grid layout for fields
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = animeName,
                                onValueChange = { animeName = it },
                                label = { Text("Anime Name") },
                                modifier = Modifier.weight(1f)
                                    .focusOrder(animeNameRequester) { next = releaseYearRequester },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = releaseYear,
                                onValueChange = { newValue ->
                                    // Only allow digits
                                    if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                        releaseYear = newValue
                                    }
                                },
                                label = { Text("Release Year") },
                                modifier = Modifier.weight(1f)
                                    .focusOrder(releaseYearRequester) { next = genreRequester },
                                singleLine = true
                            )
                        }
                        TagInput(
                            value = genre,
                            onValueChange = { genre = it },
                            label = "Genre",
                            modifier = Modifier.fillMaxWidth()
                                .focusOrder(genreRequester) { next = studioNameRequester }
                        )
                        TagInput(
                            value = studioName,
                            onValueChange = { studioName = it },
                            label = "Studio",
                            modifier = Modifier.fillMaxWidth()
                                .focusOrder(studioNameRequester) { next = tagsRequester }
                        )
                        TagInput(
                            value = tags,
                            onValueChange = { tags = it },
                            label = "Custom Tags",
                            modifier = Modifier.fillMaxWidth()
                                .focusOrder(tagsRequester) { next = statusRequester }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SimpleDropdown(
                                options = listOf("Towatch", "Watching", "On Hold", "Finished", "Dropped"),
                                selectedOption = status,
                                onOptionSelected = { status = it },
                                label = "Status",
                                modifier = Modifier.weight(1f)
                                    .focusOrder(statusRequester) { next = releasingEveryRequester }
                            )
                            SimpleDropdown(
                                options = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Irregular"),
                                selectedOption = releasingEvery,
                                onOptionSelected = { releasingEvery = it },
                                label = "Releasing Every",
                                modifier = Modifier.weight(1f)
                                    .focusOrder(releasingEveryRequester) { next = descriptionRequester }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                            .height(120.dp)
                            .focusOrder(descriptionRequester),
                        maxLines = 5
                    )
                }
            }
        )
    }
}