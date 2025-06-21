package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ManageTagsModal(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCreateTag: () -> Unit,
    onRefresh: () -> Unit
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
                    // ... (future tag management UI goes here) ...
                    Button(onClick = onCreateTag, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Create new tag")
                    }
                    Button(onClick = onRefresh, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Refresh tags")
                    }
                }
            }
        )
    }
}
