package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.TagType
import org.anibeaver.anibeaver.ui.components.showConfirmation
import org.anibeaver.anibeaver.ui.components.tag_chips.TagRow

@Composable
fun ManageTagsModal(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCreateTag: (TagType) -> Unit
) {
    if (!show) return

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Custom", "Genre", "Studio", "Author")
    val tagTypes = listOf(TagType.CUSTOM, TagType.GENRE, TagType.STUDIO, TagType.AUTHOR)

    AlertDialog(
        onDismissRequest = {},
        confirmButton = { Button(onClick = onConfirm) { Text("Close") } },
        title = { Text("Manage Tags") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { onCreateTag(tagTypes[selectedTab]) }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Create new tag")
                }
                TagTabRow(tabTitles, selectedTab) { selectedTab = it }
                TagList(tagType = tagTypes[selectedTab])
            }
        }
    )
}

@Composable
private fun TagTabRow(tabTitles: List<String>, selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(title, style = MaterialTheme.typography.labelLarge) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TagList(tagType: TagType) {
    val tags = TagsController.tags.filter { it.type == tagType }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp, min = 400.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            val usageCount = EntriesController.entries.count { entry ->
                when (tagType) {
                    TagType.CUSTOM -> tag.id in entry.entryData.tagIds
                    TagType.GENRE -> tag.id in entry.entryData.genreIds
                    TagType.STUDIO -> tag.id in entry.entryData.studioIds
                    TagType.AUTHOR -> tag.id in entry.entryData.authorIds
                }
            }
            TagRow(
                tagId = tag.id,
                tagName = tag.name,
                onTagNameChange = { name ->
                    TagsController.updateTag(tag.id, name, tag.color, tag.type)
                },
                tagHex = tag.color,
                onTagHexChange = { hex ->
                    TagsController.updateTag(tag.id, tag.name, hex, tag.type)
                },
                onDelete = { id ->
                    if (usageCount > 0) {
                        showConfirmation(
                            message = "Delete tag \"${tag.name}\"? This will remove it from $usageCount ${if (usageCount == 1) "entry" else "entries"}.",
                            onAccept = {
                                TagsController.removeTagById(id)
                            }
                        )
                    } else {
                        TagsController.removeTagById(id)
                    }
                },
                usageCount = usageCount
            )
        }
    }
}
