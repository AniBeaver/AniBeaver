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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.RequestType
import org.anibeaver.anibeaver.api.ValueSetter
import org.anibeaver.anibeaver.api.jsonStructures.PageQuery
import org.anibeaver.anibeaver.ui.components.basic.DarkTooltipBox
import org.koin.core.context.GlobalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSearchAddButton(
    alId: String,
    selectedName: String,
    onSelectionChange: (String, String) -> Unit,
    type: String = "ANIME"
) {
    var showSearchOverlay by remember { mutableStateOf(false) }

    val displayText = when {
        selectedName.isNotEmpty() -> selectedName
        alId.isNotEmpty() -> "✓ $alId"
        else -> "Quick search"
    }

    val isSelected = selectedName.isNotEmpty() || alId.isNotEmpty()
    val hasCheckmark = selectedName.isNotEmpty()

    val buttonText = formatButtonText(displayText, hasCheckmark, maxChars = 30)
    val fullText = (if (hasCheckmark) "✓ " else "") + displayText

    DarkTooltipBox(tooltip = fullText) {
        Button(
            onClick = { showSearchOverlay = true },
            colors = if (isSelected) {
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            } else {
                ButtonDefaults.buttonColors()
            },
            modifier = Modifier.width(120.dp).heightIn(min = 40.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = buttonText,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
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

private fun formatButtonText(text: String, hasCheckmark: Boolean, maxChars: Int): String {
    val prefix = if (hasCheckmark) "✓ " else ""
    val prefixLength = prefix.length

    return when {
        text.length + prefixLength <= maxChars -> prefix + text
        text.length <= maxChars - 3 - prefixLength -> prefix + text
        else -> {
            val availableChars = maxChars - prefixLength - 3
            prefix + "..." + text.takeLast(availableChars)
        }
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
    var hasNetworkError by remember { mutableStateOf(false) }

    LaunchedEffect(searchText) {
        if (searchText.isEmpty()) {
            suggestions = emptyList()
            isSearching = false
            hasNetworkError = false
            return@LaunchedEffect
        }

        isSearching = true
        hasNetworkError = false
        kotlinx.coroutines.delay(300)

        try {
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
        } catch (e: Exception) {
            isSearching = false
            hasNetworkError = true
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
                    .clickable(enabled = false, onClick = {}),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                        onValueChange = { searchText = it },
                        placeholder = { Text("Type to search...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    when {
                        isSearching -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    if (hasNetworkError) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            "No internet connection?",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                        suggestions.isEmpty() && searchText.isNotEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("No results found", color = Color.Gray)
                                    if (hasNetworkError) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "No internet connection?",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(suggestions) { suggestion ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelect(suggestion.id, suggestion.title) }
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
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
}

data class SearchSuggestion(
    val id: String,
    val title: String
)
