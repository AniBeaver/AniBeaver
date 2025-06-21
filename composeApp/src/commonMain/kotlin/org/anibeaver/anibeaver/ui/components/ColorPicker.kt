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

/**
 * A simple color picker: left is a hex input, right is a color preview.
 * @param hex The current hex string (e.g. #FF0000)
 * @param onHexChange Called when the hex string changes
 */
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
                .background(parseHexColor(hex))
        )
    }
}

private fun parseHexColor(hex: String): Color {
    return try {
        if (hex.startsWith("#") && (hex.length == 7 || hex.length == 9)) {
            val colorLong = hex.removePrefix("#").toLong(16)
            if (hex.length == 7) {
                Color(
                    red = ((colorLong shr 16) and 0xFF) / 255f,
                    green = ((colorLong shr 8) and 0xFF) / 255f,
                    blue = (colorLong and 0xFF) / 255f,
                    alpha = 1f
                )
            } else {
                Color(
                    red = ((colorLong shr 16) and 0xFF) / 255f,
                    green = ((colorLong shr 8) and 0xFF) / 255f,
                    blue = (colorLong and 0xFF) / 255f,
                    alpha = ((colorLong shr 24) and 0xFF) / 255f
                )
            }
        } else {
            Color.Transparent
        }
    } catch (_: Exception) {
        Color.Transparent
    }
}