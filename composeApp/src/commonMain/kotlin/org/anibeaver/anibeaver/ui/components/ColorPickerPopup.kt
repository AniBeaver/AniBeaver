package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPickerPopup(
    show: Boolean,
    initialColor: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (!show) return

    val controller = rememberColorPickerController()

    // Normalize initial color to 6-digit uppercase hex
    fun normalizeHex(hex: String): String {
        var clean = hex.removePrefix("#")
        // Expand 3-digit to 6-digit
        if (clean.length == 3) {
            clean = clean.map { "$it$it" }.joinToString("")
        }
        // Ensure 6 digits
        return clean.take(6).padEnd(6, '0').uppercase()
    }

    var hexInput by remember(show) {
        mutableStateOf(normalizeHex(initialColor))
    }
    var currentColor by remember(show) {
        mutableStateOf(parseHexColor(initialColor))
    }

    // Set initial color on the picker when dialog opens
    LaunchedEffect(show) {
        if (show) {
            val normalized = normalizeHex(initialColor)
            hexInput = normalized
            currentColor = parseHexColor("#$normalized")
            // Set the picker to show the initial color
            controller.selectByColor(parseHexColor("#$normalized"), fromUser = false)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick Color") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        // Update color immediately
                        currentColor = colorEnvelope.color
                        // Normalize hex code from picker
                        var hex = colorEnvelope.hexCode.removePrefix("#").uppercase()
                        // Ensure it's 6 digits
                        if (hex.length == 3) {
                            hex = hex.map { "$it$it" }.joinToString("")
                        }
                        hexInput = hex.take(6)
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = hexInput,
                        onValueChange = { input ->
                            val filtered = input.uppercase().filter { it.isDigit() || it in 'A'..'F' }
                            if (filtered.length <= 6) {
                                hexInput = filtered
                                if (filtered.length == 6) {
                                    val newColor = parseHexColor("#$filtered")
                                    currentColor = newColor
                                    controller.selectByColor(newColor, fromUser = false)
                                } else if (filtered.length == 3) {
                                    // Expand 3-digit to 6-digit
                                    val expanded = filtered.map { "$it$it" }.joinToString("")
                                    hexInput = expanded
                                    val newColor = parseHexColor("#$expanded")
                                    currentColor = newColor
                                    controller.selectByColor(newColor, fromUser = false)
                                }
                            }
                        },
                        label = { Text("Hex") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        prefix = { Text("#") }
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(currentColor, RoundedCornerShape(8.dp))
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Convert current color to hex to ensure we return what's actually shown
                val r = (currentColor.red * 255).toInt().coerceIn(0, 255)
                val g = (currentColor.green * 255).toInt().coerceIn(0, 255)
                val b = (currentColor.blue * 255).toInt().coerceIn(0, 255)
                val confirmedHex = "%02X%02X%02X".format(r, g, b)
                onConfirm("#$confirmedHex")
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

