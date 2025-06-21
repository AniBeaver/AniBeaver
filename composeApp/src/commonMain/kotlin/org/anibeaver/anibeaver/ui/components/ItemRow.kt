package org.anibeaver.anibeaver.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeleteButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        androidx.compose.material3.Text("Del")
    }
}

@Composable
fun ItemRow(
    firstValue: String,
    onFirstValueChange: (String) -> Unit,
    secondValue: String,
    onSecondValueChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextField(
            value = firstValue,
            onValueChange = onFirstValueChange,
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        TextField(
            value = secondValue,
            onValueChange = onSecondValueChange,
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        DeleteButton(onClick = onDelete)
    }
}
