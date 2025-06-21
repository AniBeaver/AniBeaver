package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.ui.components.ColorPicker

@Composable
fun TagRow(
    tagId: Int,
    tagName: String,
    onTagNameChange: (String) -> Unit,
    tagHex: String,
    onTagHexChange: (String) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = tagName,
            onValueChange = onTagNameChange,
            singleLine = true,
            label = { Text("Tag Name") },
            modifier = Modifier.weight(1f),
            textStyle = androidx.compose.ui.text.TextStyle(color = org.anibeaver.anibeaver.ui.components.parseHexColor(tagHex))
        )
        ColorPicker(
            hex = tagHex,
            onHexChange = onTagHexChange,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { onDelete(tagId) }) {
            Text("Del")
        }
    }
}
