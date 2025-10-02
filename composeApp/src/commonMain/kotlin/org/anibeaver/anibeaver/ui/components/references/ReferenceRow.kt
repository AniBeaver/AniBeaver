package org.anibeaver.anibeaver.ui.components.references

import ReorderButtons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.AutofillController.idIsValid
import org.anibeaver.anibeaver.ui.components.abstract.DeleteButton

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
    modifier: Modifier = Modifier
) {
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
        AniListSearchBar(
            alId,
            onAlIdChange
        )
        // Simple status indicator (fixed width, always centered, minimal logic)
        val uriHandler = LocalUriHandler.current
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
