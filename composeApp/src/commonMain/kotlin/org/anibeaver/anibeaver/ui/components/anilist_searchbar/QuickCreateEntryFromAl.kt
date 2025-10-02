package org.anibeaver.anibeaver.ui.components.anilist_searchbar

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.anibeaver.anibeaver.core.AutofillController


@Composable
fun QuickCreateEntryFromAl(
    quickAlId: String,
    setQuickAlId: (String) -> Unit,
    openQuickEntryCreation: () -> Unit
) {

    Row {
        AniListSearchBar(quickAlId, setQuickAlId)
        Column {
            Button(onClick = {
                if (AutofillController.idIsValid(quickAlId)) {
                    println("$quickAlId getting fetched.")
                    openQuickEntryCreation()
                }
            }) { Text("Quick add from AL") }
            if (!AutofillController.idIsValid(quickAlId)) Text("Illegal", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }


}