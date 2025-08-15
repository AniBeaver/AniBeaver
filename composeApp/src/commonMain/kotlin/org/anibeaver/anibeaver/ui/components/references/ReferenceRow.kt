package org.anibeaver.anibeaver.ui.components.references

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.ui.components.ColorPicker
import org.anibeaver.anibeaver.ui.components.abstract.DeleteButton
import org.anibeaver.anibeaver.ui.components.basic.ReorderArrows
import org.anibeaver.anibeaver.ui.components.parseHexColor

@Composable
fun ReferenceRow(
    alId: String,
    refNote: String,
    onAlIdChange: (String) -> Unit,
    onRefNoteChange: (String) -> Unit,
    onDelete: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ReorderArrows(
            onUp = onMoveUp,
            onDown = onMoveDown,
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
        OutlinedTextField(
            value = alId,
            onValueChange = onAlIdChange,
            singleLine = true,
            placeholder = { Text("000000") },
            label = { Text("AniList ID") },
            modifier = Modifier.width(110.dp)
        )
        // Simple status indicator (fixed width, always centered, minimal logic)
        val uriHandler = LocalUriHandler.current
        val isSixDigits = alId.length == 6 && alId.all { it.isDigit() }
        Box(
            modifier = Modifier.width(56.dp).align(Alignment.CenterVertically),
            contentAlignment = Alignment.Center
        ) {
            when {
                alId.isBlank() -> {}
                isSixDigits -> Text(
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
