package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button

@Composable
fun NumberPicker(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var textValue by remember { mutableStateOf(value.toString()) }
    LaunchedEffect(value) {
        val valueStr = if (value % 1 == 0f) value.toInt().toString() else value.toString()
        if (textValue != valueStr) textValue = valueStr
    }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Button(onClick = { onValueChange(value - 1f) }) { Text("-") }
        OutlinedTextField(
            value = textValue,
            onValueChange = {
                textValue = it
                it.toFloatOrNull()?.let { num -> onValueChange(num) }
            },
            singleLine = true,
            modifier = Modifier.width(64.dp)
        )
        Button(onClick = { onValueChange(value + 1f) }) { Text("+") }
    }
}
