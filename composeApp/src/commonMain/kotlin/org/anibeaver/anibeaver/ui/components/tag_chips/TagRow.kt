package org.anibeaver.anibeaver.ui.components.tag_chips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.ui.components.ColorPicker
import org.anibeaver.anibeaver.ui.components.basic.SimpleDropdown
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
    usageCount: Int? = null,
    tagType: TagType? = null,
    onTagTypeChange: ((TagType) -> Unit)? = null
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
            modifier = Modifier.weight(1.5f),
            textStyle = TextStyle(color = parseHexColor(tagHex))
        )
        ColorPicker(
            hex = tagHex,
            onHexChange = onTagHexChange,
            modifier = Modifier.weight(1f)
        )
        if (tagType != null && onTagTypeChange != null) {
            SimpleDropdown(
                options = TagType.entries.toList(),
                selectedOption = tagType,
                onOptionSelected = onTagTypeChange,
                modifier = Modifier.width(130.dp),
                label = "Type"
            )
        }
        if (usageCount != null) {
            Text(
                text = "Used\nin $usageCount\nentries",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 4.dp).width(48.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                lineHeight = 12.sp
            )
        }
        Button(
            onClick = { onDelete(tagId) },
            modifier = Modifier.size(28.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
