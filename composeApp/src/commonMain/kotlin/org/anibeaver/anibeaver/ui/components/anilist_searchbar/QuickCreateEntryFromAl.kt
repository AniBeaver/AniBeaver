package org.anibeaver.anibeaver.ui.components.anilist_searchbar

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.anibeaver.anibeaver.core.AutofillController


@Composable
fun QuickCreateEntryFromAl(
    quickAlId: String,
    setQuickAlId: (String) -> Unit,
    openQuickEntryCreation: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(end = 4.dp)
            .height(72.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxHeight(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AniListSearchBar(quickAlId, setQuickAlId)
            Column {
                Button(onClick = {
                    if (AutofillController.idIsValid(quickAlId)) {
                        println("$quickAlId getting fetched.")
                        openQuickEntryCreation()
                    }
                }) { Text("Quick add from AL") }
                if (!AutofillController.idIsValid(quickAlId)) Text(
                    "Empty/Illegal",
                    color = Color.Magenta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 7.sp
                )
            }
        }
    }


}