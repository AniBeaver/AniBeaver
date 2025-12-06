package org.anibeaver.anibeaver.ui.components.abstract

import UpDownButtons
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpinBox(
    value: String,
    onValueChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    filter: (String) -> String = { it },
    maxLength: Int = Int.MAX_VALUE,
    label: String = ""
) {
    var textValue by remember { mutableStateOf(value) }
    LaunchedEffect(value) {
        if (textValue != value) textValue = value
    }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        OutlinedTextField(
            value = textValue,
            onValueChange = {
                var filtered = filter(it)
                if (filtered.length > maxLength) filtered = filtered.take(maxLength)
                textValue = filtered
                onValueChange(filtered)
            },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .widthIn(min = 48.dp, max = 72.dp),
            label = if (label.isNotEmpty()) {
                { Text(label) }
            } else null
        )
        UpDownButtons(
            onIncrement = onIncrement,
            onDecrement = onDecrement
        )
    }
}
