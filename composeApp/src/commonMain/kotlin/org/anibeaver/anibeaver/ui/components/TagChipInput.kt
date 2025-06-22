package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
    Column(modifier) {
        var textFieldWidth by remember { mutableStateOf(0) }
        val density = LocalDensity.current
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = 4.dp, bottom = 4.dp)
                .fillMaxWidth()
        ) {
            tags.mapNotNull { id -> TagsController.tags.find { it.getId() == id && it.type == tagType } }.forEach { tag ->
                TagChip(
                    label = tag.name,
                    onDelete = { onTagsChange(tags - tag.getId()) },
                    color = parseHexColor(tag.color)
                )
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                },
                label = { Text(label) },
                singleLine = true,
                minLines = 1,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    }
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions.Default,
            )
            if (input.isNotBlank() && TagsController.tags.filter { it.type == tagType && it.getId() !in tags }.isNotEmpty()) {
                val suggestions = TagsController.tags
                    .filter { it.type == tagType && it.getId() !in tags }
                    .filter { it.name.contains(input, ignoreCase = true) }
                if (suggestions.isNotEmpty()) {
                    androidx.compose.ui.window.Popup(
                        alignment = androidx.compose.ui.Alignment.TopStart,
                        offset = androidx.compose.ui.unit.IntOffset(0, 100),
                        onDismissRequest = {}
                    ) {
                        Surface(
                            tonalElevation = 4.dp,
                            shape = MaterialTheme.shapes.medium,
                            color = Color(0xFF181818),
                            modifier = Modifier
                                .width(with(density) { textFieldWidth.toDp() })
                        ) {
                            Column {
                                suggestions.forEach { suggestion ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onTagsChange(tags + suggestion.getId())
                                                input = ""
                                            }
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            suggestion.name,
                                            color = parseHexColor(suggestion.color)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(input) {
        val trimmed = input.trimEnd()
        if ((trimmed.endsWith(",") || trimmed.endsWith(" ")) && trimmed.dropLast(1).isNotBlank()) {
            val newTag = trimmed.dropLast(1).trim()
            val matchedTag = TagsController.tags.firstOrNull { it.name.equals(newTag, ignoreCase = true) }
            if (matchedTag != null && matchedTag.getId() !in tags) {
                onTagsChange(tags + matchedTag.getId())
            }
            input = ""
        }
    }
}
