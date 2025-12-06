package org.anibeaver.anibeaver.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.anibeaver.anibeaver.ui.components.basic.AlertPopup
import org.anibeaver.anibeaver.ui.components.basic.ConfirmationPopup

object DialogState {
    var currentDialog by mutableStateOf<DialogConfig?>(null)
        private set

    fun showAlert(message: String, onDismiss: () -> Unit = {}) {
        currentDialog = DialogConfig.Alert(message, onDismiss)
    }

    fun showConfirmation(
        message: String,
        onAccept: () -> Unit,
        onDiscard: () -> Unit = {}
    ) {
        currentDialog = DialogConfig.Confirmation(message, onAccept, onDiscard)
    }

    fun dismiss() {
        currentDialog = null
    }
}

sealed class DialogConfig {
    data class Alert(
        val message: String,
        val onDismiss: () -> Unit
    ) : DialogConfig()

    data class Confirmation(
        val message: String,
        val onAccept: () -> Unit,
        val onDiscard: () -> Unit
    ) : DialogConfig()
}

fun showAlert(message: String, onDismiss: () -> Unit = {}) {
    DialogState.showAlert(message, onDismiss)
}

fun showConfirmation(
    message: String,
    onAccept: () -> Unit,
    onDiscard: () -> Unit = {}
) {
    DialogState.showConfirmation(message, onAccept, onDiscard)
}

@Composable
fun DialogPopupHost() {
    val currentDialog = DialogState.currentDialog

    when (currentDialog) {
        is DialogConfig.Alert -> {
            AlertPopup(
                show = true,
                message = currentDialog.message,
                onDismiss = {
                    currentDialog.onDismiss()
                    DialogState.dismiss()
                }
            )
        }
        is DialogConfig.Confirmation -> {
            ConfirmationPopup(
                show = true,
                message = currentDialog.message,
                onAccept = {
                    currentDialog.onAccept()
                    DialogState.dismiss()
                },
                onDiscard = {
                    currentDialog.onDiscard()
                    DialogState.dismiss()
                }
            )
        }
        null -> {}
    }
}
