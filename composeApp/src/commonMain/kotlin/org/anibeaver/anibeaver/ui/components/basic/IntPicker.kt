package org.anibeaver.anibeaver.ui.components.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.anibeaver.anibeaver.ui.components.abstract.SpinBox

@Composable
fun IntPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Value",
    min: Int = 0,
    max: Int = Int.MAX_VALUE
) {
    SpinBox(
        value = value.toString(),
        onValueChange = { filtered ->
            val intValue = filtered.filter { it.isDigit() }.toIntOrNull() ?: min
            val clamped = intValue.coerceIn(min, max)
            onValueChange(clamped)
        },
        onIncrement = onIncrement,
        onDecrement = onDecrement,
        modifier = modifier,
        filter = { input -> input.filter { it.isDigit() } },
        maxLength = max.toString().length,
        label = label
    )
}

