package org.anibeaver.anibeaver.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun FloatPicker(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    SpinBox(
        value = if (value % 1 == 0f) value.toInt().toString() else value.toString(),
        onValueChange = { filtered ->
            filtered.toFloatOrNull()?.let { onValueChange(it) }
        },
        onIncrement = { onValueChange(value + 1f) },
        onDecrement = { onValueChange(value - 1f) },
        modifier = modifier,
        filter = { input ->
            input.filterIndexed { idx, c ->
                c.isDigit() || (c == '.' && !input.take(idx).contains('.'))
            }
        }
    )
}
