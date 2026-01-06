package org.anibeaver.anibeaver.ui.components.references

import ReorderButtons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.RequestType
import org.anibeaver.anibeaver.api.ValueSetter
import org.anibeaver.anibeaver.api.jsonStructures.MediaQuery
import org.anibeaver.anibeaver.core.AutofillController.idIsValid
import org.anibeaver.anibeaver.ui.components.abstract.DeleteButton
import org.anibeaver.anibeaver.ui.components.anilist_searchbar.QuickSearchAddButton
import org.koin.core.context.GlobalContext

@Composable
fun ReferenceRow(
    alId: String,
    refNote: String,
    onAlIdChange: (String) -> Unit,
    onRefNoteChange: (String) -> Unit,
    onDelete: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    isPriority: Boolean = false,
    onPrioritySelected: (() -> Unit)? = null,
    forManga: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedName by remember(alId) { mutableStateOf("") }
    val apiHandler: ApiHandler = GlobalContext.get().get()

    LaunchedEffect(alId) {
        if (alId.isNotBlank() && idIsValid(alId)) {
            try {
                apiHandler.makeRequest(
                    variables = mapOf("mediaId" to alId),
                    valueSetter = ValueSetter { mediaQuery: MediaQuery ->
                        selectedName = mediaQuery.data.media.title.english ?: ""
                    },
                    requestType = RequestType.MEDIA
                )
            } catch (e: Exception) {
                selectedName = ""
            }
        } else {
            selectedName = ""
        }
    }

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RadioButton(
            selected = isPriority,
            onClick = { onPrioritySelected?.invoke() },
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        ReorderButtons(
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            modifier = Modifier.size(48.dp)
        )
        OutlinedTextField(
            value = refNote,
            onValueChange = onRefNoteChange,
            singleLine = true,
            label = { Text("Note") },
            placeholder = { Text("(optional)") },
            modifier = Modifier.weight(1f)
        )
        val uriHandler = LocalUriHandler.current

        QuickSearchAddButton(
            alId = alId,
            selectedName = selectedName,
            onSelectionChange = { id, name ->
                onAlIdChange(id)
                selectedName = name
            },
            type = if (forManga) "MANGA" else "ANIME"
        )
        Box(
            modifier = Modifier.width(56.dp).align(Alignment.CenterVertically),
            contentAlignment = Alignment.Center
        ) {
            when {
                alId.isBlank() -> {}
                idIsValid(alId) -> Text(
                    text = "Link",
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { uriHandler.openUri("https://anilist.co/anime/$alId") }
                )

                else -> Text("Illegal", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
        DeleteButton(onClick = onDelete)
    }
}
