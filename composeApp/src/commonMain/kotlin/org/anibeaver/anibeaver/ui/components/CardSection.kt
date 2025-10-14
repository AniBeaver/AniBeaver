package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CardSection(
    status: String,
    isExpanded: Boolean = true,
    onToggleExpand: () -> Unit,
    cardSpacing: Dp
) {
    if (status.isBlank()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = cardSpacing),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = status,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Button(onClick = onToggleExpand, modifier = Modifier.height(32.dp)) {
            Text(if (isExpanded) "Collapse" else "Expand", fontSize = 12.sp)
        }
    }
}