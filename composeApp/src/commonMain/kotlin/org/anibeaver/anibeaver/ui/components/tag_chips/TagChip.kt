package org.anibeaver.anibeaver.ui.components.tag_chips

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagChip(
    label: String,
    color: Color = MaterialTheme.colorScheme.primary,
    onDelete: (() -> Unit)? = null,
    fontSize: TextUnit = 14.sp
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color,
        tonalElevation = 2.dp,
        modifier = Modifier.padding(end = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                label,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = fontSize
            )
            if (onDelete != null) {
                Spacer(Modifier.size(3.dp))
                Text(
                    "✕",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = fontSize,
                    modifier = Modifier
                        .clickable { onDelete() }
                        .padding(start = 1.dp)
                )
            }
        }
    }
}

