package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.controller.EditEntryController
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun HomeScreen(
    navController: NavHostController = rememberNavController(), activityKiller : () -> Unit = {}
) {
    var showEditPopup by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(32.dp)) {
        Text("Welcome to AniBeaver", style = Typography.headlineLarge, modifier = Modifier.padding(bottom = 8.dp))

        Button(onClick = {
            navController.navigate(Screens.Anime.name)
        }) {
            Text("Go to Anime")
        }

        Button(onClick = {
            navController.navigate(Screens.Manga.name)
        }) {
            Text("Go to Manga")
        }

        Button(onClick = {
            navController.navigate(Screens.Settings.name)
        }) {
            Text("Go to Settings")
        }

        Button(onClick = {
            activityKiller()
        }) {
            Text("Close")
        }
    }
    // Focus order for tab navigation
    // This requires passing Modifier.focusOrder to each input in EditEntryPopup
    // and managing FocusRequester chain
    // See EditEntryPopup for implementation
}