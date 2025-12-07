package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.anibeaver.anibeaver.core.ImageController
import org.anibeaver.anibeaver.ui.components.showConfirmation
import org.anibeaver.anibeaver.ui.theme.Typography

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: AnimeViewModel
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

        Button(
            onClick = {
                showConfirmation(
                    message = "Are you sure you want to delete ALL entries? This action cannot be undone!",
                    onAccept = {
                        showConfirmation(
                            message = "This is your final warning! All entries will be permanently deleted. Continue?",
                            onAccept = {
                                viewModel.deleteAllEntries()
                            }
                        )
                    }
                )
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Delete All Entries")
        }

        Button(
            onClick = {
                showConfirmation(
                    message = "Are you sure you want to delete ALL entries? This action cannot be undone!",
                    onAccept = {
                        showConfirmation(
                            message = "This is your final warning! All entries will be permanently deleted. Continue?",
                            onAccept = {
                                viewModel.deleteAllEntries()
                            }
                        )
                    }
                )
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Import Entries")
        }

        Button(
            onClick = {
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Export Entries")
        }
    }
}