package org.anibeaver.anibeaver.ui.components.basic

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.anibeaver.anibeaver.ui.components.abstract.DialoguePopup

@Composable
fun ConfirmationPopup(
    show: Boolean,
    message: String,
    onAccept: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    DialoguePopup(
        show = show,
        message = message,
        onDismiss = onDiscard,
        dismissButton = {
            Button(onClick = onDiscard) {
                Text("No")
            }
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("Yes")
            }
        },
        modifier = modifier
    )
}

