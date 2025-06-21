package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.padding

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
        // Color input (hex) field
        OutlinedTextField(
            value = hex,
            onValueChange = { onHexChange(it) },
            label = { Text("Hex") },
            singleLine = true,
            modifier = Modifier
                .weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        // Color preview box
        val color = try {
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
        } catch (e: Exception) {
            Color.Transparent
        }
        Box(
            Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .background(color)
        )
    }
}