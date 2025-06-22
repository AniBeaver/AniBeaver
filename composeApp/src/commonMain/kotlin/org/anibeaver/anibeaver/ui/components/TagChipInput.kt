package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.datastructures.TagType

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
    // Keyboard navigation state
    var focusedSuggestionIndex by remember { mutableStateOf(0) }
    val tagObjects = tags.mapNotNull { id -> TagsController.tags.find { it.getId() == id && it.type == tagType } }
    val allSuggestions = TagsController.tags
        .filter { it.type == tagType && it.getId() !in tags }
    val suggestions = if (input.isNotBlank()) {
        val exact = allSuggestions.firstOrNull { it.name.equals(input, ignoreCase = true) }
        if (exact != null) listOf(exact) else allSuggestions.filter { it.name.contains(input, ignoreCase = true) }
    } else emptyList()

    Column(modifier) {
        // OutlinedTextField with chips inside
        var textFieldWidth by remember { mutableStateOf(0) }
        val density = LocalDensity.current
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    expanded = it.isNotBlank() && suggestions.isNotEmpty()
                    focusedSuggestionIndex = 0
                },
                label = { Text(label) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    },
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
            // Use DropdownMenu as an overlay for suggestions
            androidx.compose.material3.DropdownMenu(
                expanded = expanded && suggestions.isNotEmpty(),
                onDismissRequest = { expanded = false },
                modifier = if (textFieldWidth > 0) with(density) { Modifier.width(textFieldWidth.toDp()) } else Modifier
            ) {
                suggestions.forEachIndexed { i, suggestion ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = {
                            Text(suggestion.name)
                        },
                        onClick = {
                            onTagsChange(tags + suggestion.getId())
                            input = ""
                            expanded = false
                            focusedSuggestionIndex = 0
                        },
                        modifier = Modifier
                            .background(
                                if (i == focusedSuggestionIndex)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                    )
                }
            }
        }
    }
    // Reset focus if suggestions change
    LaunchedEffect(suggestions) { focusedSuggestionIndex = 0 }
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
