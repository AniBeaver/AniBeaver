package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.controller.EditEntryController
import org.anibeaver.anibeaver.controller.CardsController
import org.anibeaver.anibeaver.controller.AnimeCard
import org.anibeaver.anibeaver.ui.components.EntryCard
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.ui.theme.Typography

@Composable
@Preview
fun AnimeScreen(
    navController: NavHostController = rememberNavController()
) {
    var showPopup by remember { mutableStateOf(false) }
    // Use CardsController.cards directly, do not create a local copy
    Column(Modifier.fillMaxSize()) {
        Text("Anime", style = Typography.headlineLarge)
        Button(onClick = { navController.navigate(Screens.Home.name) }) { Text("Go to Home") }
        Button(onClick = { showPopup = true }) { Text("Open Popup") }
        Button(onClick = {
            val nextId = (CardsController.cards.maxOfOrNull { it.id } ?: 0) + 1
            CardsController.cards.add(AnimeCard(nextId, "Test Card", "Test Tag"))
        }) { Text("Add Test Card") }

        EditEntryPopup(
            show = showPopup,
            onDismiss = { showPopup = false },
            onConfirm = {
                EditEntryController.handleEditEntry(it)
                showPopup = false
            }
        )
        CardsController.cards.chunked(3).forEach { rowCards ->
            Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowCards.forEach { card ->
                    EntryCard(
                        id = card.id,
                        name = card.name,
                        labels = card.labels,
                        onDelete = { CardsController.cards.removeIf { it.id == card.id } }
                    )
                    Spacer(Modifier.width(12.dp))
                }
                repeat(3 - rowCards.size) { Spacer(Modifier.width(432.dp)) }
            }
        }
    }
}