package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.anibeaver.anibeaver.core.datastructures.Status

@Composable
fun CardGroup(
    statusId: Int,
    invisible: Boolean,
    onCollapseClicked: () -> Unit,
    cardSpacing: Dp,
    isExpanded: Boolean = true
) {
    if (invisible) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = cardSpacing),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onCollapseClicked),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isExpanded) "▼" else "▶",
                fontSize = 14.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )
            Text(
                text = Status.fromId(statusId).toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        TextButton( //TODO: button might not be needed anymore

            onClick = onCollapseClicked,
            modifier = Modifier.height(32.dp).width(100.dp)
        ) {
            Text(if (isExpanded) "Collapse" else "Expand", fontSize = 12.sp)
        }
    }
}