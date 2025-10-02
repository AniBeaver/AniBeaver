package org.anibeaver.anibeaver.ui.components.anilist_searchbar

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun QuickCreateEntryFromAl (
    quickAlId: String,
    setQuickAlId: (String) -> Unit,
    openQuickEntryCreation: () -> Unit
) {

        Row {
            AniListSearchBar(quickAlId, setQuickAlId)
            Button(onClick = {
                println("$quickAlId getting fetched.")
                openQuickEntryCreation()
            }) { Text("Quick add from AL") }
        }

}