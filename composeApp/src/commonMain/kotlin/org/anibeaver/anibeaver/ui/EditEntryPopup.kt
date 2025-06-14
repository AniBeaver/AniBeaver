package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
                    Text("OK")
                }
            },
            title = { Text("Popup") },
            text = {
                Column {
                    // Add image input rectangle at the top
                    ImageInput(
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        onClick = { /* TODO: Handle image selection */ }
                    )
                    OutlinedTextField(
                        value = animeName,
                        onValueChange = { animeName = it },
                        label = { Text("Anime Name") },
                        modifier = Modifier.padding(bottom = 8.dp)
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
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = genre,
                        onValueChange = { genre = it },
                        label = { Text("Genre") },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .height(120.dp),
                        maxLines = 5
                    )
                    Text("Rating", modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                    NumberPicker(
                        value = rating,
                        onValueChange = { rating = it }
                    )
                    TagInput(
                        value = tags,
                        onValueChange = { tags = it },
                        label = "Custom Tags",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    TagInput(
                        value = genre,
                        onValueChange = { genre = it },
                        label = "Genre",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    TagInput(
                        value = studioName,
                        onValueChange = { studioName = it },
                        label = "Studio",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SimpleDropdown(
                        options = listOf("Towatch", "Watching", "On Hold", "Finished", "Dropped"),
                        selectedOption = status,
                        onOptionSelected = { status = it },
                        label = "Status",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SimpleDropdown(
                        options = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Irregular"),
                        selectedOption = releasingEvery,
                        onOptionSelected = { releasingEvery = it },
                        label = "Releasing Every",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        )
    }
}
