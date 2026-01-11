package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.ui.components.ColorPicker
import org.anibeaver.anibeaver.ui.components.basic.SimpleDropdown
import org.anibeaver.anibeaver.ui.components.parseHexColor

@Composable
fun NewTagPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, TagType) -> Unit,
    initialTagName: String = "",
    initialHex: String = "#ffffff",
    initialType: TagType = TagType.CUSTOM
) {
    var tagName by remember { mutableStateOf(initialTagName) }
    var tagHex by remember { mutableStateOf(initialHex.ifBlank { "#FFFFFF" }) }
    var tagType by remember { mutableStateOf(initialType) }

    LaunchedEffect(show) {
        if (show) {
            tagName = initialTagName
            tagHex = initialHex.ifBlank { "#FFFFFF" }
            tagType = initialType
        }
    }

    val cleanHex = tagHex.removePrefix("#")
    val isHexValid = cleanHex.matches(Regex("^[A-Fa-f0-9]{3}$")) ||
                     cleanHex.matches(Regex("^[A-Fa-f0-9]{6}$")) ||
                     cleanHex.matches(Regex("^[A-Fa-f0-9]{8}$"))

    // Normalize to 6-digit hex code
    val normalizedHex = if (tagHex.startsWith("#")) {
        val hex = tagHex.removePrefix("#")
        if (hex.length == 3) {
            "#${hex.map { "$it$it" }.joinToString("")}"
        } else {
            tagHex
        }
    } else {
        val hex = tagHex
        if (hex.length == 3) {
            "#${hex.map { "$it$it" }.joinToString("")}"
        } else {
            "#$tagHex"
        }
    }

    val isNameValid = tagName.isNotBlank()
    val isConfirmEnabled = isHexValid && isNameValid

    if (!show) return

    AlertDialog(
        onDismissRequest = {},
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
                        textStyle = TextStyle(color = parseHexColor(tagHex))
                    )
                    Spacer(Modifier.width(8.dp))

                    SimpleDropdown(
                        options = TagType.entries.toList(),
                        selectedOption = tagType,
                        onOptionSelected = { tagType = it },
                        modifier = Modifier.weight(1f),
                        label = "Tag Type"
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
            Button(onClick = { onConfirm(tagName, normalizedHex, tagType) }, enabled = isConfirmEnabled) {
                Text("Confirm/Create/Apply")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Dismiss/Close")
            }
        }
    )
}