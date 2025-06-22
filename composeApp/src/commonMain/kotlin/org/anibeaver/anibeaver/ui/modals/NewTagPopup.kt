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
import org.anibeaver.anibeaver.datastructures.TagType
import org.anibeaver.anibeaver.ui.components.ColorPicker
import org.anibeaver.anibeaver.ui.components.SimpleDropdown
import org.anibeaver.anibeaver.ui.components.parseHexColor
import androidx.compose.ui.text.TextStyle

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

    val isHexValid = tagHex.matches(Regex("^#([A-Fa-f0-9]{6})$"))
    val isNameValid = tagName.isNotBlank()
    val isConfirmEnabled = isHexValid && isNameValid

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
            Button(onClick = { onConfirm(tagName, tagHex, tagType) }, enabled = isConfirmEnabled) {
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