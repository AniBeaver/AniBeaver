package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
    modifier: Modifier = Modifier
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
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        Box(
            Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .background(org.anibeaver.anibeaver.ui.components.parseHexColor(hex))
        )
    }
}