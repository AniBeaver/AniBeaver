package org.anibeaver.anibeaver.ui.components.references

import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AniListSearchBar(
    alId: String,
    onAlIdChange: (String) -> Unit
) {
    OutlinedTextField(
    value = alId,
    onValueChange = onAlIdChange,
    singleLine = true,
    placeholder = { Text("000000") },
    label = { Text("AniList ID") },
    modifier = Modifier.width(110.dp)
    )
}