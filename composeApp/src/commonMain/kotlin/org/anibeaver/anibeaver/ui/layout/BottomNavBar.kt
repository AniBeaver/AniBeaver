package org.anibeaver.anibeaver.ui.layout

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
    val startDestination = Screens.Anime //TODO: rechange to home later
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination.name) }

    // Update bottom bar when destination changes
    navController.addOnDestinationChangedListener { _, destination, _ ->
        if (destination.route == selectedDestination) return@addOnDestinationChangedListener
        selectedDestination = destination.route ?: startDestination.name
    }

    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        Screens.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestination == destination.name,
                onClick = {
                    if (selectedDestination != destination.name) {
                        navController.navigate(route = destination.name)
                        selectedDestination = destination.name
                    }
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