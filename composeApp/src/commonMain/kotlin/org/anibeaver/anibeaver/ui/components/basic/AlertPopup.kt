package org.anibeaver.anibeaver.ui.components.basic

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.anibeaver.anibeaver.ui.components.abstract.DialoguePopup

@Composable
fun AlertPopup(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    DialoguePopup(
        show = show,
        message = message,
        onDismiss = onDismiss,
        dismissButton = {},
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Okay")
            }
        },
        modifier = modifier
    )
}

