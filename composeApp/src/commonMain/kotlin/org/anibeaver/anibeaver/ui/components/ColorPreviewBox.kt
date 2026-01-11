package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPreview(
    hex: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(parseHexColor(hex))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .height(32.dp)
    )
}

fun parseHexColor(hex: String): Color {
    return try {
        var cleanHex = hex.removePrefix("#")

        // Normalize 3-digit to 6-digit hex codes
        if (cleanHex.length == 3) {
            cleanHex = cleanHex.map { "$it$it" }.joinToString("")
        }

        when (cleanHex.length) {
            6 -> {
                val intColor = cleanHex.toLong(16).toInt()
                Color(
                    red = ((intColor shr 16) and 0xFF) / 255f,
                    green = ((intColor shr 8) and 0xFF) / 255f,
                    blue = (intColor and 0xFF) / 255f
                )
            }

            8 -> {
                val intColor = cleanHex.toLong(16)
                Color(
                    red = ((intColor shr 24) and 0xFF) / 255f,
                    green = ((intColor shr 16) and 0xFF) / 255f,
                    blue = ((intColor shr 8) and 0xFF) / 255f,
                    alpha = (intColor and 0xFF) / 255f
                )
            }

            else -> Color.Gray
        }
    } catch (_: Exception) {
        Color.Gray
    }
}

