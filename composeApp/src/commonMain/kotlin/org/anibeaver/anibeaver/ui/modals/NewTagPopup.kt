package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.ui.components.ColorPicker
import org.anibeaver.anibeaver.ui.components.ColorPreview
import org.anibeaver.anibeaver.ui.components.parseHexColor
import androidx.compose.ui.text.TextStyle

@Composable
fun NewTagPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    initialTagName: String = "",
    initialHex: String = "#ffffff"
) {
    var tagName by remember { mutableStateOf(initialTagName) }
    var tagHex by remember { mutableStateOf(initialHex.ifBlank { "#FFFFFF" }) }

    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Tag") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = tagName,
                        onValueChange = { tagName = it },
                        singleLine = true,
                        label = { Text("Tag Name") },
                        modifier = Modifier.weight(1f),
                        textStyle = androidx.compose.ui.text.TextStyle(color = org.anibeaver.anibeaver.ui.components.parseHexColor(tagHex))
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.size(32.dp).background(
                            try {
                                val cleanHex = tagHex.removePrefix("#")
                                if (cleanHex.length == 6) org.anibeaver.anibeaver.ui.components.parseHexColor(tagHex)
                                else Color.White
                            } catch (_: Exception) {
                                Color.White
                            }
                        )
                    )
                }
                ColorPicker(
                    hex = tagHex,
                    onHexChange = { tagHex = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(tagName, tagHex) }) {
                Text("Confirm/Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Dismiss/Close")
            }
        }
    )
}