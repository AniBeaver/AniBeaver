package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.coil.AsyncImage
import org.anibeaver.anibeaver.core.ImageController
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.Art
import org.anibeaver.anibeaver.core.datastructures.Entry
import org.anibeaver.anibeaver.ui.ImageInput
import kotlin.math.round

@Composable
fun BannerBackground(
    art: Art?,
    modifier: Modifier = Modifier,
    alpha: Float = 0.2f
) {
    var bannerFile by remember { mutableStateOf<PlatformFile?>(null) }

    LaunchedEffect(art) {
        bannerFile = art?.let { ImageController.ensureImageExists(it) }
    }

    if (bannerFile == null) return

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val parentHeight = constraints.maxHeight.toFloat()
        val parentWidth = constraints.maxWidth.toFloat()
        val imageAspectRatio = 16f / 9f
        val scaleX = ((parentHeight / parentWidth) * imageAspectRatio).coerceAtLeast(1f)

        AsyncImage(
            file = bannerFile,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    this.alpha = alpha
                    this.scaleX = scaleX
                    this.scaleY = 1f
                }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
    }
}


@Composable
fun EntryCard(
    entry: Entry,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val name = entry.entryData.title.toString()
    val description = entry.entryData.description

    val studioTags = entry.entryData.studioIds
        .mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val authorTags = entry.entryData.authorIds
        .mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val genreTags = entry.entryData.genreIds
        .mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }
    val customTags = entry.entryData.tagIds
        .mapNotNull { id -> TagsController.tags.find { it.id == id }?.name }

    Card(shape = RoundedCornerShape(6.dp)) {
        Box(
            modifier = Modifier
                .height(160.dp)
                .width(380.dp)
        ) {
            BannerBackground(
                art = entry.entryData.bannerArt,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.2f,
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ImageInput(
                        modifier = Modifier.size(64.dp),
                        art = entry.entryData.coverArt,
                        onClick = { /* TODO */ }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        name,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        fontSize = 16.sp,
                        color = Color.White
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
                        fontSize = 12.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    val tagsLine = listOf(
                        genreTags.joinToString(", "),
                        studioTags.joinToString(", "),
                        authorTags.joinToString(", "),
                        customTags.joinToString(", ")
                    ).filter { it.isNotBlank() }.joinToString(", ")
                    if (tagsLine.isNotBlank()) {
                        Text(tagsLine, fontSize = 12.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(description, fontSize = 13.sp, color = Color.White)
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
}
