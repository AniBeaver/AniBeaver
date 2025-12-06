package org.anibeaver.anibeaver.ui.components.tag_chips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.ui.components.ColorPicker
import org.anibeaver.anibeaver.ui.components.abstract.DeleteButton
import org.anibeaver.anibeaver.ui.components.parseHexColor

@Composable
fun TagRow(
    tagId: Int,
    tagName: String,
    onTagNameChange: (String) -> Unit,
    tagHex: String,
    onTagHexChange: (String) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier,
    usageCount: Int? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = tagName,
            onValueChange = onTagNameChange,
            singleLine = true,
            label = { Text("Tag Name") },
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(color = parseHexColor(tagHex))
        )
        ColorPicker(
            hex = tagHex,
            onHexChange = onTagHexChange,
            modifier = Modifier.weight(1f)
        )
        if (usageCount != null) {
            Text(
                text = "Used in\n$usageCount entries",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        DeleteButton(onClick = { onDelete(tagId) })
    }
}
