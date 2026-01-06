package org.anibeaver.anibeaver.ui.components.basic

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkTooltipBox(
    tooltip: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip(
                containerColor = Color(0xFF2B2B2B),
                contentColor = Color.White
            ) {
                Text(tooltip)
            }
        },
        state = rememberTooltipState(),
        modifier = modifier
    ) {
        content()
    }
}

