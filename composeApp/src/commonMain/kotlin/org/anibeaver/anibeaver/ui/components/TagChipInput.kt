package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.datastructures.TagType
import org.anibeaver.anibeaver.ui.components.parseHexColor

@Composable
fun TagChipInput(
    tags: List<Int>,
    onTagsChange: (List<Int>) -> Unit,
    tagType: TagType,
    modifier: Modifier = Modifier,
    label: String = "Tags"
) {
    var input by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val tagObjects = tags.mapNotNull { id -> TagsController.tags.find { it.getId() == id && it.type == tagType } }
    val suggestions = TagsController.tags
        .filter { it.type == tagType && it.name.contains(input, ignoreCase = true) && it.getId() !in tags }
        .map { it }

    Column(modifier) {
        // OutlinedTextField with chips inside
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    expanded = it.isNotBlank() && suggestions.isNotEmpty()
                },
                label = { Text(label) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        tagObjects.forEach { tag ->
                            TagChip(
                                label = tag.name,
                                onDelete = { onTagsChange(tags - tag.getId()) },
                                color = parseHexColor(tag.color)
                            )
                        }
                    }
                }
            )
            if (expanded && suggestions.isNotEmpty()) {
                DropdownSurface(visible = true) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(top = 2.dp)) {
                        suggestions.forEach { suggestion ->
                            Text(
                                suggestion.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onTagsChange(tags + suggestion.getId())
                                        input = ""
                                        expanded = false
                                    }
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(input) {
        if (input.endsWith(",") && input.dropLast(1).isNotBlank()) {
            val newTag = input.dropLast(1).trim()
            val matchedTag = suggestions.firstOrNull { it.name.equals(newTag, ignoreCase = true) }
            if (matchedTag != null && matchedTag.getId() !in tags) {
                onTagsChange(tags + matchedTag.getId())
            }
            input = ""
        }
    }
}

@Composable
fun DropdownSurface(visible: Boolean, content: @Composable () -> Unit) {
    if (!visible) return
    Surface(
        tonalElevation = 8.dp,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
    ) {
        content()
    }
}
