package org.anibeaver.anibeaver.ui.components.abstract

import UpDownButtons
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
    label: String = "",
    highlightIfEmpty: Boolean = false
) {
    val isEmpty = value.isEmpty()
    val displayValue = if (highlightIfEmpty && isEmpty) "" else value
    var textValue by remember { mutableStateOf(displayValue) }
    LaunchedEffect(displayValue) {
        if (textValue != displayValue) textValue = displayValue
    }

    val shouldHighlight = highlightIfEmpty && textValue.isEmpty()

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
            } else null,
            colors = if (shouldHighlight) {
                OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            } else {
                OutlinedTextFieldDefaults.colors()
            }
        )
        UpDownButtons(
            onIncrement = onIncrement,
            onDecrement = onDecrement
        )
    }
}
