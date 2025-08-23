package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.datastructures.Art
import org.anibeaver.anibeaver.core.datastructures.Entry
import org.anibeaver.anibeaver.core.datastructures.EntryData
import org.anibeaver.anibeaver.core.datastructures.FilterData
import org.anibeaver.anibeaver.core.datastructures.Schedule
import org.anibeaver.anibeaver.core.datastructures.Status
import org.anibeaver.anibeaver.ui.components.EntryCard
import org.anibeaver.anibeaver.ui.modals.*
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max

@Composable
@Preview
fun AnimeScreen(
    navController: NavHostController = rememberNavController(),
    dataWrapper: DataWrapper
) {
    var showPopup by remember { mutableStateOf(false) }
    var currentEditedEntryId by remember { mutableStateOf<Int?>(null) }
    var showManageTags by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    var showNewTagPopupFromManage by remember { mutableStateOf(false) }
    val filterState = rememberAnimeFilterState()

    fun refreshTags() {
        // TODO: Implement tag refresh logic here
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val cardWidth = 350.dp
        val cardSpacing = 6.dp
        val totalWidth = maxWidth
        val columns = max(1, ((totalWidth + cardSpacing) / (cardWidth + cardSpacing)).toInt())

        // Buttons
        Column(Modifier.fillMaxSize()) {
            Text("Anime", style = Typography.headlineLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { navController.navigate(Screens.Home.name) }) { Text("Go to Home") }
                Button(onClick = {
                    currentEditedEntryId = null
                    showPopup = true
                }) { Text("New Entry") }
                Button(onClick = { showManageTags = true }) { Text("Manage tags") }
                Button(onClick = { showFilter = true }) { Text("Filter entries") }
                Button(onClick = {
                    val entryData = EntryData(
                        animeName = "Placeholder Anime",
                        releaseYear = "2025",
                        studioIds = listOf(18), // Bones studio id
                        genreIds = listOf(7, 8, 9), // Action, Adventure, Fantasy genre ids
                        description = "This is a placeholder entry.",
                        rating = 8.5f,
                        status = Status.Completed, // Use enum value
                        releasingEvery = Schedule.Irregular, // Use enum value
                        tagIds = listOf(10, 11), // Shounen, Classic custom tag ids
                        coverArt = Art("", ""),
                        bannerArt = Art("", ""),
                        episodesTotal = 13,
                        episodesProgress = 13,
                        rewatches = 1
                        //references: e.g 179966
                    )
                    EntriesController.addEntry(entryData = entryData)
                }) { Text("Add Placeholder Entry") }
            }
            Spacer(Modifier.height(16.dp))
            if (showPopup) {
                EditEntryPopup(
                    show = showPopup,
                    onDismiss = { showPopup = false },
                    onConfirm = { entryData ->
                        EntriesController.updateEntry(currentEditedEntryId, entryData)
                        showPopup = false
                    },
                    initialValues = EntriesController.getEntryDataById(currentEditedEntryId),
                    dataWrapper
                )
            }
            ManageTagsModal(
                show = showManageTags,
                onDismiss = { showManageTags = false },
                onConfirm = { showManageTags = false },
                onCreateTag = { showNewTagPopupFromManage = true }
            )
            FilterPopup(
                show = showFilter,
                onDismiss = { showFilter = false },
                onConfirm = { data ->
                    filterState.onFilterChange(data)
                    showFilter = false
                },
                initialFilter = filterState.filterData
            )
            NewTagPopup(
                show = showNewTagPopupFromManage,
                onDismiss = { showNewTagPopupFromManage = false },
                onConfirm = { name, color, type ->
                    org.anibeaver.anibeaver.core.TagsController.addTag(name, color, type)
                    showNewTagPopupFromManage = false
                }
            )

            val allEntries = EntriesController.entries
            val entriesToShow = allEntries.filter { it.matchesFilter(filterState.filterData) }
            FilterInfoRow(entriesToShow, allEntries) { filterState.clear() }
            EntryGrid(
                entriesToShow = entriesToShow,
                columns = columns,
                cardWidth = cardWidth,
                cardSpacing = cardSpacing,
                onEdit = { entryId ->
                    currentEditedEntryId = entryId
                    showPopup = true
                },
                onDelete = { entryId ->
                    EntriesController.deleteEntry(entryId)
                }
            )
        }
    }
}


data class AnimeFilterState(
    var filterData: FilterData? = null, // change this to defaultFilterData
    val onFilterChange: (FilterData?) -> Unit
) {
    fun clear() = onFilterChange(defaultFilterData)
}

@Composable
fun rememberAnimeFilterState(): AnimeFilterState {
    var filterData by remember { mutableStateOf<FilterData?>(null) }
    return AnimeFilterState(filterData) { filterData = it }
}

@Composable
private fun FilterInfoRow(entriesToShow: List<Entry>, allEntries: List<Entry>, onClear: () -> Unit) {
    val hiddenCount = allEntries.size - entriesToShow.size
    if (hiddenCount > 0) {
        val entryWord = if (entriesToShow.size == 1) "entry" else "entries"
        val hiddenWord = if (hiddenCount == 1) "entry" else "entries"
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Showing ${entriesToShow.size} $entryWord. $hiddenCount $hiddenWord hidden.", color = androidx.compose.ui.graphics.Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            Spacer(Modifier.width(12.dp))
            Button(onClick = onClear, modifier = Modifier.height(32.dp)) {
                Text("Clear filters", fontSize = androidx.compose.ui.unit.TextUnit.Unspecified)
            }
        }
    }
}

@Composable
private fun EntryGrid(
    entriesToShow: List<Entry>,
    columns: Int,
    cardWidth: androidx.compose.ui.unit.Dp,
    cardSpacing: androidx.compose.ui.unit.Dp,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    entriesToShow.chunked(columns).forEach { rowEntries ->
        Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(cardSpacing)) {
            Spacer(Modifier.width(cardSpacing))
            rowEntries.forEach { entry ->
                val studioTags = entry.entryData.studioIds.mapNotNull { id -> org.anibeaver.anibeaver.core.TagsController.tags.find { it.id == id }?.name }
                val genreTags = entry.entryData.genreIds.mapNotNull { id -> org.anibeaver.anibeaver.core.TagsController.tags.find { it.id == id }?.name }
                val customTags = entry.entryData.tagIds.mapNotNull { id -> org.anibeaver.anibeaver.core.TagsController.tags.find { it.id == id }?.name }
                EntryCard(
                    id = entry.id,
                    name = entry.entryData.animeName,
                    tags = (genreTags + entry.entryData.releaseYear + studioTags + customTags).joinToString(", "),
                    description = entry.entryData.description,
                    onEdit = { onEdit(entry.id) },
                    onDelete = { onDelete(entry.id) }
                )
                Spacer(Modifier.width(cardSpacing))
            }
            repeat(columns - rowEntries.size) { Spacer(Modifier.width(cardWidth + cardSpacing)) }
        }
    }
}