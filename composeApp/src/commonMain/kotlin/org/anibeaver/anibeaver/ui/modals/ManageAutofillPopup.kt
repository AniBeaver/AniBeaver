package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ManageAutofillPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Close")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Dismiss")
                }
            },
            title = { Text("Manage AL Autofill") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Autofill lets you automatically fill fields from AniList.")
                    // Add more controls or info here as needed
                }
            }
        )
    }
}