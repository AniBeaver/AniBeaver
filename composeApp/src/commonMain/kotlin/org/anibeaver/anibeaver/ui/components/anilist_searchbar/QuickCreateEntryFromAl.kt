package org.anibeaver.anibeaver.ui.components.anilist_searchbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight


@Composable
fun QuickCreateEntryFromAl(
    quickAlId: String,
    setQuickAlId: (String) -> Unit,
    openQuickEntryCreation: () -> Unit
) {

    fun isValid(alId: String): Boolean {
        return true
    }

    Row {
        AniListSearchBar(quickAlId, setQuickAlId)
        Column {
            Button(onClick = {
                if (isValid(quickAlId)) {
                    println("$quickAlId getting fetched.")
                    openQuickEntryCreation()
                }
            }) { Text("Quick add from AL") }
            if (!isValid(quickAlId)) Text("Illegal", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }


}