package org.anibeaver.anibeaver.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester

//TODO: turn this into proper tag-input with autocomplete (save those tags elsewhere?) and discord-role like 'x'-to-remove button
@Composable
fun TagInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Tags (comma separated)"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = true
    )
}
