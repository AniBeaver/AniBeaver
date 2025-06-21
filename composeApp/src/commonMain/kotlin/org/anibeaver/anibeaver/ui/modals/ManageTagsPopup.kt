package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.ui.components.TagRow

@Composable
fun ManageTagsModal(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCreateTag: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Confirm/Save")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Dismiss/Undo")
                }
            },
            title = { Text("Manage Tags") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onCreateTag, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Create new tag")
                    }
                    // Tag management UI: scrollable list of TagRow for all tags
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (tag in TagsController.tags) {
                            var name by remember { mutableStateOf(tag.name) }
                            var hex by remember { mutableStateOf(tag.color) }
                            TagRow(
                                tagName = name,
                                onTagNameChange = { name = it },
                                tagHex = hex,
                                onTagHexChange = { hex = it },
                                onDelete = { TagsController.removeTagById(tag.getId()) }
                            )
                        }
                    }
                }
            }
        )
    }
}
