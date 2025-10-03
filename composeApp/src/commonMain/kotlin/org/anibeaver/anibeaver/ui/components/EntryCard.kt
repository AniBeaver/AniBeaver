package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.Entry

@Composable
fun EntryCard(
    entry: Entry,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    // Build display strings from the entry data so callers don't need to compute them
    val name = entry.entryData.animeName
    val description = entry.entryData.description

    val studioTags = entry.entryData.studioIds.mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val genreTags = entry.entryData.genreIds.mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val customTags = entry.entryData.tagIds.mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val tags = (genreTags + listOf(entry.entryData.releaseYear) + studioTags + customTags).joinToString(", ")

    Card(shape = RoundedCornerShape(6.dp)) {
        Row(
            Modifier
                .height(200.dp)
                .width(380.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("IMG", fontSize = 14.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(tags, maxLines = 8, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                Text(description, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.End) {
                Button(onClick = onEdit, modifier = Modifier.height(32.dp)) {
                    Text("Edit", fontSize = 12.sp)
                }
                Button(onClick = onDelete, modifier = Modifier.height(32.dp)) {
                    Text("Delete", fontSize = 12.sp)
                }
            }

        }
    }
}
