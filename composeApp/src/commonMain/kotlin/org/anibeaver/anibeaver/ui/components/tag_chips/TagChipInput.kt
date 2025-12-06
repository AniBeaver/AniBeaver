package org.anibeaver.anibeaver.ui.components.tag_chips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.ui.components.parseHexColor

@Composable
fun TagChipInput(
    tags: List<Int>,
    onTagsChange: (List<Int>) -> Unit,
    tagType: TagType,
    modifier: Modifier = Modifier,
    label: String = "Tags",
    buttonContent: (@Composable () -> Unit)? = null,
    onCreateTagClick: ((String) -> Unit)? = null
) {
    var input by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var textFieldWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    Column(modifier) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = 4.dp, bottom = 4.dp)
                .fillMaxWidth()
        ) {
            tags.mapNotNull { id -> TagsController.tags.find { it.id == id && it.type == tagType } }.forEach { tag ->
                TagChip(
                    label = tag.name,
                    onDelete = { onTagsChange(tags - tag.id) },
                    color = parseHexColor(tag.color)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                val focusRequester = remember { FocusRequester() }
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
                if (input.isNotBlank() && TagsController.tags.any { it.type == tagType && it.id !in tags }) {
                    val suggestions = TagsController.tags
                        .filter { it.type == tagType && it.id !in tags }
                        .filter { it.name.contains(input, ignoreCase = true) }
                    if (suggestions.isNotEmpty()) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(0, 100),
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
                                                    onTagsChange(tags + suggestion.id)
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
            if (onCreateTagClick != null) {
                Button(
                    onClick = {
                        onCreateTagClick(input)
                        input = ""
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Create ${label.lowercase()}")
                }
            }
        }
    }
    LaunchedEffect(input) {
        val trimmed = input.trimEnd()
        if ((trimmed.endsWith(",") || trimmed.endsWith(" ")) && trimmed.dropLast(1).isNotBlank()) {
            val newTag = trimmed.dropLast(1).trim()
            val matchedTag = TagsController.tags.firstOrNull { it.name.equals(newTag, ignoreCase = true) }
            if (matchedTag != null && matchedTag.id !in tags) {
                onTagsChange(tags + matchedTag.id)
            }
            input = ""
        }
    }
}

@Composable
fun TagChipInput(
    tags: List<Int>,
    onTagsChange: (List<Int>) -> Unit,
    tagType: TagType,
    modifier: Modifier = Modifier,
    label: String = "Tags",
    buttonContent: (@Composable () -> Unit)? = null,
    onCreateTagClick: ((String) -> Unit)? = null,
    surfaceColor: Color? = null
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = surfaceColor ?: Color.Unspecified,
        modifier = modifier
    ) {
        TagChipInput(
            tags = tags,
            onTagsChange = onTagsChange,
            tagType = tagType,
            modifier = Modifier.padding(8.dp),
            label = label,
            buttonContent = buttonContent,
            onCreateTagClick = onCreateTagClick
        )
    }
}
