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
                    Text("Confirm") //TODO: is this really the right word? It's more like "close" or "ok"
                }
            },
            title = { Text("Manage Tags") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onCreateTag, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Create new tag")
                    }
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp, min=400.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (tag in TagsController.tags) {
                            TagRow(
                                tagId = tag.getId(),
                                tagName = tag.name,
                                onTagNameChange = { name ->
                                    TagsController.updateTag(tag.getId(), name, tag.color, tag.type)
                                },
                                tagHex = tag.color,
                                onTagHexChange = { hex ->
                                    TagsController.updateTag(tag.getId(), tag.name, hex, tag.type)
                                },
                                onDelete = { id -> TagsController.removeTagById(id) }
                            )
                        }
                    }
                }
            }
        )
    }
}
