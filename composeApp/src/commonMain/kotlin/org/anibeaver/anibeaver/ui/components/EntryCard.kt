package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        name,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp, top = 4.dp, end = 4.dp)
                    )

                    val rating = entry.entryData.rating
                    val ratingText = if (rating > 0f) {
                        val rounded = (round(rating * 10f) / 10f)
                        if (rounded % 1f == 0f) "${rounded.toInt()}.0" else rounded.toString()
                    } else "-"

                    Text(
                        "$ratingText★",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(end = 4.dp, top = 3.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top
                ) {
                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .size(width = 62.dp, height = 94.dp)
                            .padding(top = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ImageInput(
                            modifier = Modifier.size(width = 62.dp, height = 94.dp),
                            art = entry.entryData.coverArt,
                            onClick = { /* TODO */ }
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Top
                    ) {
                        val year = entry.entryData.releaseYear
                        val status = entry.entryData.status.toString()
                        val schedule = entry.entryData.releasingEvery.toString()
                        val epsProgress = entry.entryData.episodesProgress
                        val epsTotal = entry.entryData.episodesTotal
                        val rewatches = entry.entryData.rewatches

                        Text(
                            "$epsProgress/$epsTotal eps (${rewatches}x) • $year • $schedule • $status",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )

                        if (genreTags.isNotEmpty()) {
                            Text(
                                "Genres: ${genreTags.joinToString(", ")}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        if (studioTags.isNotEmpty()) {
                            Text(
                                "Studios: ${studioTags.joinToString(", ")}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        if (authorTags.isNotEmpty()) {
                            Text(
                                "Authors: ${authorTags.joinToString(", ")}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        if (customTags.isNotEmpty()) {
                            Text(
                                "Tags: ${customTags.joinToString(", ")}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        if (description.isNotBlank()) {
                            Text(
                                description,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Button(
                            onClick = onEdit,
                            modifier = Modifier.size(28.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Button(
                            onClick = onDelete,
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
            }
        }
    }
}
