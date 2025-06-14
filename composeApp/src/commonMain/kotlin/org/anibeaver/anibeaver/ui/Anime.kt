package org.anibeaver.anibeaver.ui

import org.anibeaver.anibeaver.DataWrapper

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue

@Composable
@Preview
fun AnimeScreen(
    navController: NavHostController = rememberNavController(),
    dataWrapper: DataWrapper
) {

    Column{
        Text("Anime", style = Typography.headlineLarge)

        Button(onClick = {
            navController.navigate(Screens.Home.name)
        }) {
            Text("Go to Home")
        }
    }
}