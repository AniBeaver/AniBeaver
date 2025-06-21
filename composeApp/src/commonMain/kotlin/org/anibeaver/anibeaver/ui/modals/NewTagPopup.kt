package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

@Composable
fun NewTagPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    initialTagName: String = "",
    initialHex: String = "#000000"
) {
    var tagName by remember { mutableStateOf(initialTagName) }
    var tagHex by remember { mutableStateOf(initialHex) }

    // Reset fields when initial values change
    LaunchedEffect(initialTagName, initialHex) {
        tagName = initialTagName
        tagHex = initialHex
    }

    val focusManager = LocalFocusManager.current
    val tagNameRequester = remember { FocusRequester() }
    val tagHexRequester = remember { FocusRequester() }

    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = {
                    onConfirm(tagName, tagHex)
                }) {
                    Text("Confirm/Create")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Dismiss/Close")
                }
            },
            title = { Text("New Tag") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tagName,
                        onValueChange = { tagName = it },
                        label = { Text("Tag Name") },
                        modifier = Modifier.fillMaxWidth()
                            .focusRequester(tagNameRequester)
                            .focusProperties { next = tagHexRequester },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tagHex,
                        onValueChange = { tagHex = it },
                        label = { Text("Tag Hex Code") },
                        modifier = Modifier.fillMaxWidth()
                            .focusRequester(tagHexRequester),
                        singleLine = true
                    )
                }
            }
        )
    }
}