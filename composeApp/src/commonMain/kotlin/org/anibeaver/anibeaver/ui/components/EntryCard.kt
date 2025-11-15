package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.PlatformFile
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.Entry
import org.anibeaver.anibeaver.core.datastructures.EntryType
import org.anibeaver.anibeaver.ui.ImagePreview
import kotlin.math.round

@Composable
fun EntryCard(
    entry: Entry,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val name: String = entry.entryData.title.toString()
    val description = entry.entryData.description

    val studioTags = entry.entryData.studioIds
        .mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val authorTags = entry.entryData.authorIds
        .mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val genreTags = entry.entryData.genreIds
        .mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val customTags = entry.entryData.tagIds
        .mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val tags = (genreTags +
            listOf(entry.entryData.releaseYear) +
            (if (entry.entryData.type == EntryType.Anime) studioTags else authorTags) +
            customTags
            ).joinToString(", ")

    Card(shape = RoundedCornerShape(6.dp)) {
        Row(
            modifier = Modifier
                .height(160.dp)
                .width(380.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                ImagePreview(
                    modifier = Modifier.size(width = 64.dp, height = 64.dp),
                    file = entry.entryData.coverArt.localPath.let { PlatformFile(it) },
                    onClick = { /* TODO: handle image click */ }
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    fontSize = 16.sp
                )
                val year = entry.entryData.releaseYear
                val rating = entry.entryData.rating
                val status = entry.entryData.status.toString()
                val schedule = entry.entryData.releasingEvery.toString()
                val epsProgress = entry.entryData.episodesProgress
                val epsTotal = entry.entryData.episodesTotal
                val rewatches = entry.entryData.rewatches
                val ratingText = if (rating > 0f) {
                    val rounded = (round(rating * 10f) / 10f)
                    if (rounded % 1f == 0f) "${rounded.toInt()}.0" else rounded.toString()
                } else "-"
                Text(
                    "$year, Rating: $ratingText, Status: $status, Eps: $epsProgress/$epsTotal (rewatches: $rewatches), Schedule: $schedule",
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                val tagsLine = listOf(
                    genreTags.joinToString(", "),
                    studioTags.joinToString(", "),
                    authorTags.joinToString(", "),
                    customTags.joinToString(", ")
                ).filter { it.isNotBlank() }.joinToString(", ")
                if (tagsLine.isNotBlank()) {
                    Text(tagsLine, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(description, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
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
