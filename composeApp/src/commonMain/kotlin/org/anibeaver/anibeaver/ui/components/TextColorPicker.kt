package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    hex: String,
    onHexChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = hex,
            onValueChange = onHexChange,
            label = { Text("Hex") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        Box(
            Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .background(parseHexColor(hex))
        )
    }
}