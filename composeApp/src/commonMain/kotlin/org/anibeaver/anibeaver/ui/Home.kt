package org.anibeaver.anibeaver.ui

import org.anibeaver.anibeaver.DataWrapper

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    dataWrapper: DataWrapper
) {
    Column(modifier = Modifier.padding(32.dp)) {
        Text(
                "Welcome to AniBeaver",
                style = Typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(onClick = { navController.navigate(Screens.Anime.name) }) { Text("Go to Anime") }

        Button(onClick = { navController.navigate(Screens.Manga.name) }) { Text("Go to Manga") }

        Button(onClick = { navController.navigate(Screens.Settings.name) }) {Text("Go to Settings")}

        Button(onClick = { navController.navigate(Screens.Test.name) }) {Text("Go to Test")}

        Button(onClick = { dataWrapper.activityKiller() }) { Text("Close") }
    }
}
