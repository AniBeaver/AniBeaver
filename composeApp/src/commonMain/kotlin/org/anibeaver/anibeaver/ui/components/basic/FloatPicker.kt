package org.anibeaver.anibeaver.ui.components.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.anibeaver.anibeaver.ui.components.abstract.SpinBox

@Composable
fun FloatPicker(
    value: Float?,
    onValueChange: (Float?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    highlightIfEmpty: Boolean = false
) {
    SpinBox(
        value = value?.let { "%.1f".format(it) } ?: "",
        onValueChange = { filtered ->
            onValueChange(filtered.toFloatOrNull())
        },
        onIncrement = { onValueChange(if (value == null) 8.0f else value + 0.5f) },
        onDecrement = { onValueChange(if (value == null) 8.0f else (value - 0.5f).coerceAtLeast(0f)) },
        modifier = modifier,
        filter = { input ->
            input.filterIndexed { idx, c ->
                c.isDigit() || (c == '.' && !input.take(idx).contains('.'))
            }
        },
        label = label,
        highlightIfEmpty = highlightIfEmpty
    )
}
