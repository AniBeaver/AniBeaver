package org.anibeaver.anibeaver.ui.components.anilist_searchbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.RequestType
import org.anibeaver.anibeaver.api.ValueSetter
import org.anibeaver.anibeaver.api.jsonStructures.PageQuery
import org.koin.core.context.GlobalContext

@Composable
fun AniListSearchBar(
    alId: String,
    selectedName: String,
    onSelectionChange: (String, String) -> Unit,
    type: String = "ANIME"
) {
    var showSearchOverlay by remember { mutableStateOf(false) }

    Button(
        onClick = { showSearchOverlay = true },
        colors = if (alId.isNotEmpty()) {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        } else {
            ButtonDefaults.buttonColors()
        },
        modifier = Modifier.width(120.dp).heightIn(min = 40.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = when {
                selectedName.isNotEmpty() -> "✓ $selectedName"
                alId.isNotEmpty() -> "ID: $alId"
                else -> "Quick search"
            },
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 12.sp
        )
    }

    if (showSearchOverlay) {
        SearchOverlay(
            type = type,
            onDismiss = { showSearchOverlay = false },
            onSelect = { id, name ->
                onSelectionChange(id, name)
                showSearchOverlay = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchOverlay(
    type: String,
    onDismiss: () -> Unit,
    onSelect: (String, String) -> Unit
) {
    val apiHandler: ApiHandler = GlobalContext.get().get()

    var searchText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<SearchSuggestion>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(searchText) {
        if (searchText.isEmpty()) {
            suggestions = emptyList()
            isSearching = false
            return@LaunchedEffect
        }

        try {
            kotlinx.coroutines.delay(300)
            isSearching = true

            apiHandler.makeRequest(
                variables = mapOf(
                    "page" to "1",
                    "perPage" to "10",
                    "search" to searchText,
                    "type" to type
                ),
                valueSetter = ValueSetter { pageQuery: PageQuery ->
                    suggestions = pageQuery.data.page.media.map { media ->
                        SearchSuggestion(
                            id = media.id.toString(),
                            title = media.title.english ?: media.title.native ?: "Unknown"
                        )
                    }
                    isSearching = false
                },
                requestType = RequestType.PAGE
            )
        } catch (e: kotlinx.coroutines.CancellationException) {
            // Silently ignore cancellation - this is expected when typing
        } catch (e: Exception) {
            isSearching = false
            suggestions = emptyList()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 100.dp)
                    .width(600.dp)
                    .clickable(enabled = true, onClick = { }),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Search ${if (type == "MANGA") "Manga" else "Anime"}",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { newValue ->
                            searchText = newValue
                        },
                        placeholder = { Text("Type to search...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isSearching) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (suggestions.isEmpty() && searchText.isNotEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No results found", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(suggestions) { suggestion ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelect(suggestion.id, suggestion.title)
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Text(
                                            suggestion.title,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            "ID: ${suggestion.id}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
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
}

data class SearchSuggestion(
    val id: String,
    val title: String
)
