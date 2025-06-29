package org.anibeaver.anibeaver.layout

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens

@Composable
fun BottomNavBar(
    navController: NavHostController = rememberNavController()
) {
    val startDestination = Screens.Home
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination.name) }

    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        Screens.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestination == destination.name,
                onClick = {
                    navController.navigate(route = destination.name)
                    selectedDestination = destination.name
                },
                icon = {
                    Icon(
                        destination.icon,
                        contentDescription = destination.name
                    )
                },
                label = { Text(destination.title) }
            )
        }
    }
}