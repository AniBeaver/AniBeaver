package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.ui.components.NumberPicker
import org.anibeaver.anibeaver.ui.components.SimpleDropdown
import org.anibeaver.anibeaver.ui.components.TagInput
import org.anibeaver.anibeaver.ui.components.ImageInput

@Composable
fun EditEntryPopup(
    show: Boolean,
    onDismiss: () -> Unit
) {
    var animeName by remember { mutableStateOf("") }
    var releaseYear by remember { mutableStateOf("") }
    var studioName by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    var status by remember { mutableStateOf("") }
    var releasingEvery by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onDismiss) {
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
                            )
                        }
                        TagInput(
                            value = genre,
                            onValueChange = { genre = it },
                            label = "Genre",
                            modifier = Modifier.fillMaxWidth()
                        )
                        TagInput(
                            value = studioName,
                            onValueChange = { studioName = it },
                            label = "Studio",
                            modifier = Modifier.fillMaxWidth()
                        )
                        TagInput(
                            value = tags,
                            onValueChange = { tags = it },
                            label = "Custom Tags",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SimpleDropdown(
                                options = listOf("Towatch", "Watching", "On Hold", "Finished", "Dropped"),
                                selectedOption = status,
                                onOptionSelected = { status = it },
                                label = "Status",
                                modifier = Modifier.weight(1f)
                            )
                            SimpleDropdown(
                                options = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Irregular"),
                                selectedOption = releasingEvery,
                                onOptionSelected = { releasingEvery = it },
                                label = "Releasing Every",
                                modifier = Modifier.weight(1f)
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
                            .height(120.dp),
                        maxLines = 5
                    )
                }
            }
        )
    }
}
