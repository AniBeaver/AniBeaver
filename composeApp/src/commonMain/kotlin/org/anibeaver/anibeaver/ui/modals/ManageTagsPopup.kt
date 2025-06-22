package org.anibeaver.anibeaver.ui.modals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.ui.components.TagRow

@Composable
fun ManageTagsModal(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCreateTag: () -> Unit
) {
    if (show) {
        var selectedTab by remember { mutableStateOf(0) }
        val tabTitles = listOf("Custom", "Genre", "Studio")
        val tagTypes = listOf(
            org.anibeaver.anibeaver.datastructures.TagType.CUSTOM,
            org.anibeaver.anibeaver.datastructures.TagType.GENRE,
            org.anibeaver.anibeaver.datastructures.TagType.STUDIO
        )
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Close")
                }
            },
            title = { Text("Manage Tags") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = onCreateTag, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Create new tag")
                    }
                    // Style-consistent tab row
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
                                    onClick = { selectedTab = index },
                                    text = { Text(title, style = MaterialTheme.typography.labelLarge) },
                                    selectedContentColor = MaterialTheme.colorScheme.primary,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp, min = 400.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (tag in TagsController.tags.filter { it.type == tagTypes[selectedTab] }) {
                            TagRow(
                                tagId = tag.getId(),
                                tagName = tag.name,
                                onTagNameChange = { name ->
                                    TagsController.updateTag(tag.getId(), name, tag.color, tag.type)
                                },
                                tagHex = tag.color,
                                onTagHexChange = { hex ->
                                    TagsController.updateTag(tag.getId(), tag.name, hex, tag.type)
                                },
                                onDelete = { id -> TagsController.removeTagById(id) }
                            )
                        }
                    }
                }
            }
        )
    }
}
