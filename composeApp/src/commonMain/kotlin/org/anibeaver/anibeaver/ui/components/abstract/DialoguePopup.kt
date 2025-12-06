package org.anibeaver.anibeaver.ui.components.abstract

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DialoguePopup(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = { Text(message) },
        modifier = modifier
    )
}

