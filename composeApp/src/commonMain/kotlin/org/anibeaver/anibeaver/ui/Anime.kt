package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.controller.EditEntryController
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun AnimeScreen(
    navController: NavHostController = rememberNavController()
) {
    var showPopup by remember { mutableStateOf(false) }
    Column{
        Text("Anime", style = Typography.headlineLarge)

        Button(onClick = {
            navController.navigate(Screens.Home.name)
        }) {
            Text("Go to Home")
        }
        Button(onClick = { showPopup = true }) {
            Text("Open Popup")
        }
        EditEntryPopup(
            show = showPopup,
            onDismiss = { showPopup = false },
            onConfirm = { entryData ->
                EditEntryController.handleEditEntry(entryData)
                showPopup = false
            }
        )
    }
}