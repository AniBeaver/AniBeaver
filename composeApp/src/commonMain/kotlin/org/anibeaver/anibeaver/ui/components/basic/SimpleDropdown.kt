package org.anibeaver.anibeaver.ui.components.basic

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SimpleDropdown(
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Select",
    highlightIfEmpty: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val shouldHighlight = highlightIfEmpty

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            colors = if (shouldHighlight) {
                OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                )
            } else {
                OutlinedTextFieldDefaults.colors()
            }
        )
        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option.toString()) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}
