package org.anibeaver.anibeaver.ui.components.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.anibeaver.anibeaver.ui.components.abstract.SpinBox

@Composable
fun YearPicker(
    value: String,
    onValueChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
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

