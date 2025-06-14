package org.anibeaver.anibeaver.ui.components

import androidx.compose.runtime.Composable

@Composable
fun YearPicker(
    value: String,
    onValueChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    SpinBox(
        value = value,
        onValueChange = { filtered ->
            onValueChange(filtered)
        },
        onIncrement = onIncrement,
        onDecrement = onDecrement,
        modifier = modifier,
        filter = { input -> input.filter { it.isDigit() } },
        maxLength = 4,
        label = "Year"
    )
}

