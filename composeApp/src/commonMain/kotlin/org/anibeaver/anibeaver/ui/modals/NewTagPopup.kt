package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.ui.components.ColorPicker

fun parseHexColor(hex: String): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        if (cleanHex.length == 6) {
            val intColor = cleanHex.toLong(16).toInt()
            Color(
                red = ((intColor shr 16) and 0xFF) / 255f,
                green = ((intColor shr 8) and 0xFF) / 255f,
                blue = (intColor and 0xFF) / 255f
            )
        } else Color.Black
    } catch (_: Exception) {
        Color.Black
    }
}

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
    var showColorPalette by remember { mutableStateOf(false) }

    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Tag") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    label = { Text("Tag Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                ColorPicker(
                    hex = tagHex,
                    onHexChange = { tagHex = it },
                    modifier = Modifier.fillMaxWidth()
                )
                if (showColorPalette) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF").forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .background(parseHexColor(hex))
                                    .clickable {
                                        tagHex = hex
                                        showColorPalette = false
                                    }
                            )
                        }
                    }
                }
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