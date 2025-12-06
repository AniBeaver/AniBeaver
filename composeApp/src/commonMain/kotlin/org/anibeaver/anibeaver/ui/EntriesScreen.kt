package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.core.EntriesController
import org.anibeaver.anibeaver.core.TagsController
import org.anibeaver.anibeaver.core.datastructures.*
import org.anibeaver.anibeaver.ui.components.CardGroup
import org.anibeaver.anibeaver.ui.components.EntryCard
import org.anibeaver.anibeaver.ui.components.anilist_searchbar.QuickCreateEntryFromAl
import org.anibeaver.anibeaver.ui.components.basic.SimpleDropdown
import org.anibeaver.anibeaver.ui.modals.*
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
@Preview
fun EntriesScreen(
    navController: NavHostController = rememberNavController(), forManga: Boolean
) {
    var showEditEntryPopup by remember { mutableStateOf(false) }
    var showAutofillPopup by remember { mutableStateOf(false) }
    var currentEditedEntryId by remember { mutableStateOf<Int?>(null) }
    var showManageTags by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    var showNewTagPopupFromManage by remember { mutableStateOf(false) }
    val filterState = rememberAnimeFilterState()
    var quickAlId by remember { mutableStateOf((if (!forManga) "97832" else "80145")) } //default value set here for debug (Citrus)
    var sortBy by remember { mutableStateOf(SortingBy.Rating) }
    var sortOrder by remember { mutableStateOf(SortingType.Ascending) }
    var groupByStatus by remember { mutableStateOf(true) }


    val viewModel: AnimeViewModel = remember { AnimeViewModel() }

    fun refreshTags() {
        // TODO: Implement tag refresh logic here
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val cardWidth = 350.dp
        val cardSpacing = 6.dp
        val totalWidth = maxWidth
        val columns = max(1, ((totalWidth + cardSpacing) / (cardWidth + cardSpacing)).toInt())

        fun showEntryPopup(id: Int?, alsoShowAutofillPopup: Boolean = false) { //id = null for empty
            currentEditedEntryId = id
            showAutofillPopup = alsoShowAutofillPopup
            showEditEntryPopup = true
        }

        Column(
            Modifier.fillMaxSize()
        ) {
            Text(if (forManga) "Anime" else "Manga", style = Typography.headlineLarge)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Button(onClick = { navController.navigate(Screens.Home.name) }) { Text("Go to Home") }

                Button(onClick = { showEntryPopup(null) }) { Text("New Entry") }
                Button(onClick = { showManageTags = true }) { Text("Manage tags") }
                Button(onClick = { showFilter = true }) { Text("Filter entries") }

                Card(
                    modifier = Modifier.padding(end = 4.dp).height(72.dp)
                ) {
                    Row(
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp).fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SimpleDropdown(
                            options = SortingBy.entries,
                            selectedOption = sortBy,
                            onOptionSelected = { sortBy = it },
                            label = "Sort by",
                            modifier = Modifier.width(160.dp)
                        )
                        SimpleDropdown(
                            options = SortingType.entries,
                            selectedOption = sortOrder,
                            onOptionSelected = { sortOrder = it },
                            label = "Sort order",
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
                Card(
                    modifier = Modifier.padding(end = 4.dp).height(72.dp)
                ) {
                    Row(
                        Modifier.fillMaxHeight().padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Group by status:")
                        Checkbox(
                            checked = groupByStatus, onCheckedChange = { checked -> groupByStatus = checked })

                    }
                }
                QuickCreateEntryFromAl(quickAlId, { newQuickAlId -> quickAlId = newQuickAlId }, {
                    showEntryPopup(null, true)
                })
                Button(onClick = {
//                    val entryData = EntryData(
//                        animeName = "Placeholder Anime",
//                        releaseYear = "2025",
//                        studioIds = listOf(18), // Bones studio id
//                        genreIds = listOf(7, 8, 9), // Action, Adventure, Fantasy genre ids
//                        description = "This is a placeholder entry.",
//                        rating = 8.5f,
//                        status = Status.Completed,
//                        releasingEvery = Schedule.Irregular,
//                        tagIds = listOf(10, 11), // Shounen, Classic custom tag ids
//                        coverArt = Art("", ""),
//                        bannerArt = Art("", ""),
//                        episodesTotal = 13,
//                        episodesProgress = 13,
//                        rewatches = 1
//                        //references: e.g 179966
//                    )
//                    EntriesController.addEntry(entryData = entryData)
                    viewModel.saveAnimeEntry()
                }) { Text("Add Placeholder Entry") }

                Button(onClick = { filterState.collapseAll() }) {
                    Text("Collapse All")
                }

                Button(onClick = { filterState.expandAll() }) {
                    Text("Expand All")
                }
            }

            Spacer(Modifier.height(16.dp))
            if (showEditEntryPopup) {
                EditEntryPopup(
                    show = showEditEntryPopup,
                    onDismiss = { showEditEntryPopup = false },
                    onConfirm = { entryData ->
                        viewModel.upsertAnimeEntry(currentEditedEntryId, entryData)

                        showEditEntryPopup = false

                        if (showAutofillPopup) quickAlId = ""
                    },
                    forceShowAutofillPopup = showAutofillPopup,
                    alIdToBePassed = quickAlId,
                    initialValues = EntriesController.getEntryDataById(currentEditedEntryId),
                    forManga=forManga
                )
            }
            ManageTagsModal(
                show = showManageTags,
                onDismiss = { showManageTags = false },
                onConfirm = { showManageTags = false },
                onCreateTag = { showNewTagPopupFromManage = true })

            FilterPopup(
                show = showFilter, onDismiss = { showFilter = false }, onConfirm = { data ->
                filterState.onFilterChange(data)
                showFilter = false
                print(data)
            }, initialFilter = filterState.filterData
            )
            NewTagPopup(
                show = showNewTagPopupFromManage,
                onDismiss = { showNewTagPopupFromManage = false },
                onConfirm = { name, color, type ->
                    TagsController.addTag(name, color, type)
                    showNewTagPopupFromManage = false
                })

            val allEntries = EntriesController.entries
            val observedVersion = EntriesController.entriesVersion // for resorting


            val allEntriesByType: Map<Int, List<Entry>> = allEntries.groupBy { entry -> entry.entryData.type.id}
            val allEntriesOfType = if (forManga) allEntriesByType.getOrDefault(EntryType.Manga.id, listOf()) else allEntriesByType.getOrDefault(EntryType.Anime.id, listOf())
            print("Entries of type manga?" + forManga + allEntriesOfType.size)

            val filteredEntries = allEntriesOfType.filter { it.matchesFilter(filterState.filterData) }
            val entriesToShow = sortEntries(filteredEntries, sortBy, sortOrder)
            FilterInfoRow(entriesToShow, allEntriesOfType) { filterState.clear() }

            EntryGrid(
                entriesToShow = entriesToShow,
                allEntriesOfType = allEntriesOfType,
                columns = columns,
                cardWidth = cardWidth,
                cardSpacing = cardSpacing,
                onEdit = { entryId -> showEntryPopup(entryId) },
                onDelete = { entryId ->
                    viewModel.deleteAnimeEntry(entryId)
                },
                groupByStatus = groupByStatus,
                filterState = filterState,
                forManga=forManga
            )
        }
    }
}


data class AnimeFilterState(
    var filterData: FilterData? = null,
    val onFilterChange: (FilterData?) -> Unit
) {
    private val currentSelectedStatuses: List<Status>
        get() = (filterData ?: defaultFilterData).selectedStatus.ifEmpty { defaultFilterData.selectedStatus }

    fun clear() = onFilterChange(defaultFilterData)

    fun toggleStatusInFilter(status: Status) {
        val statusSet = currentSelectedStatuses.toSet()
        val newSelectedStatuses = if (status in statusSet) {
            (statusSet - status).toList()
        } else {
            (statusSet + status).toList()
        }
        onFilterChange((filterData ?: defaultFilterData).copy(selectedStatus = newSelectedStatuses))
    }

    fun isStatusInFilter(status: Status): Boolean = status in currentSelectedStatuses

    fun collapseAll() {
        onFilterChange((filterData ?: defaultFilterData).copy(selectedStatus = emptyList()))
    }

    fun expandAll() {
        onFilterChange((filterData ?: defaultFilterData).copy(selectedStatus = Status.entries.toList()))
    }
}

@Composable
fun rememberAnimeFilterState(): AnimeFilterState {
    var filterData by remember { mutableStateOf<FilterData?>(defaultFilterData) }
    return AnimeFilterState(
        filterData = filterData,
        onFilterChange = { filterData = it }
    )
}

private fun sortEntries(entries: List<Entry>, primarySortBy: SortingBy, sortType: SortingType): List<Entry> {

    fun statusWeight(s: Status) = when (s) {
        Status.Watching -> 10
        Status.Paused -> 1
        Status.Planning -> 3
        Status.Completed -> 5
        Status.Dropped -> 0
    }

    val comparators: Map<SortingBy, Comparator<Entry>> =
        mapOf(
            SortingBy.Rating to compareBy<Entry> { it.entryData.rating }.reversed(), //TODO: sorting by title is the only one where the default makes sense to be descending... Not sure if this is the best solution though
            SortingBy.Title to compareBy<Entry> { it.entryData.title?.lowercase() },
            SortingBy.Rewatches to compareBy<Entry> { it.entryData.rewatches },
            SortingBy.Year to compareBy({ it.entryData.releaseYear.toIntOrNull() ?: Int.MIN_VALUE }),
            SortingBy.Length to compareBy { it.entryData.episodesTotal })

    val defaultTiebreakerPriorities = listOf( // tiebreaker priority
        SortingBy.Rating, SortingBy.Title, SortingBy.Rewatches, SortingBy.Year, SortingBy.Length
    )

    val secondarySortBys = listOf(primarySortBy) + defaultTiebreakerPriorities.filterNot { it == primarySortBy }

    val statusComparator = compareBy<Entry> { statusWeight(it.entryData.status) }.reversed()

    val chainedComparator: Comparator<Entry> =
        secondarySortBys.mapNotNull { comparators[it] }.fold(comparators[primarySortBy]) { acc, next ->
            acc!!.thenComparing(next)
        }!!

    val finalComparator = if (sortType == SortingType.Ascending) chainedComparator
    else chainedComparator.reversed()

    return entries.sortedWith(finalComparator).sortedWith(statusComparator)
}

@Composable
private fun FilterInfoRow(entriesToShow: List<Entry>, allEntries: List<Entry>, onClear: () -> Unit) {
    val hiddenCount = allEntries.size - entriesToShow.size
    if (hiddenCount > 0) {
        val entryWord = if (entriesToShow.size == 1) "entry" else "entries"
        val hiddenWord = if (hiddenCount == 1) "entry" else "entries"
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Showing ${entriesToShow.size} $entryWord. $hiddenCount $hiddenWord hidden.",
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Spacer(Modifier.width(12.dp))
            Button(onClick = onClear, modifier = Modifier.height(32.dp)) {
                Text(
                    "Clear filters", fontSize = TextUnit.Unspecified
                ) //FIXME: clear filters no longer works for some reason – likely because some new attribute was added. Look in Entry.kt/FilterData
            }
        }
    }
}

@Composable
private fun EntryGrid(
    entriesToShow: List<Entry>,
    allEntriesOfType: List<Entry>,
    columns: Int,
    cardWidth: Dp,
    cardSpacing: Dp,
    groupByStatus: Boolean,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    filterState: AnimeFilterState,
    forManga: Boolean
) {
    val groupedEntries = entriesToShow.groupBy { entry ->
        if (groupByStatus) entry.entryData.status.id else -1
    }

    val allEntriesGroupedByStatus = allEntriesOfType.groupBy { entry ->
        if (groupByStatus) entry.entryData.status.id else -1
    }

    val statusesToDisplay = if (groupByStatus) {
        Status.entries
    } else {
        listOf(Status.fromId(-1)!!)
    }

    //TODO: perhaps separate filter state for manga?

    statusesToDisplay.forEach { status ->
        val statusId = status.id
        val entriesForThisStatus = groupedEntries[statusId] ?: emptyList()
        val allEntriesForThisStatus = allEntriesGroupedByStatus[statusId] ?: emptyList()
        val isStatusInFilter = filterState.isStatusInFilter(status)
        val hasEntriesForStatus = allEntriesForThisStatus.isNotEmpty()

        if (hasEntriesForStatus) {
            CardGroup(
                statusId = statusId,
                onCollapseClicked = {
                    filterState.toggleStatusInFilter(status)
                },
                cardSpacing = cardSpacing,
                invisible = !groupByStatus,
                isExpanded = isStatusInFilter
            )

            if (isStatusInFilter) {
                entriesForThisStatus.chunked(columns).forEach { rowEntries ->
                    Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(cardSpacing)) {
                        Spacer(Modifier.width(cardSpacing))
                        rowEntries.forEach { entry ->
                            EntryCard(entry = entry, onEdit = { onEdit(entry.id) }, onDelete = { onDelete(entry.id) })
                            Spacer(Modifier.width(cardSpacing))
                        }
                        repeat(columns - rowEntries.size) { Spacer(Modifier.width(cardWidth + cardSpacing)) }
                    }
                }
            }
        }
    }

}