package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.core.ImageController
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun SettingsScreen(
    navController: NavHostController = rememberNavController()
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = Typography.headlineLarge)

//        Button(onClick = { //FIXME: readd later
//            navController.navigate(Screens.Home.name)
//        }) {
//            Text("Go to Home")
//        }

        Button(
            onClick = {
                ImageController.cleanUpImagesDir()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Clean Up Unused Images")
        }
    }
}