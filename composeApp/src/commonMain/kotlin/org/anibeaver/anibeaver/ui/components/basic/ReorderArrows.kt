package org.anibeaver.anibeaver.ui.components.basic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ReorderArrows {
}

@Composable
fun ReorderArrows(
    modifier: Modifier = Modifier,
    arrowColor: Color = MaterialTheme.colorScheme.onSurface,
    size: Dp = 120.dp, // Increased default size
    onUp: (() -> Unit)? = null,
    onDown: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.size(size),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = onUp != null) { onUp?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Move up",
                tint = arrowColor,
                modifier = Modifier.size(size * 0.75f) // Make icon larger relative to box
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = onDown != null) { onDown?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Move down",
                tint = arrowColor,
                modifier = Modifier.size(size * 0.75f)
            )
        }
    }
}
