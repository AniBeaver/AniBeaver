package org.anibeaver.anibeaver.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    navigationItems: List<ScreenInfo>,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navigationItems.forEach { screenInfo ->
            NavigationBarItem(
                icon = { Icon(screenInfo.icon, contentDescription = screenInfo.title) },
                label = { Text(screenInfo.title) },
                selected = currentRoute == screenInfo.screen.name,
                onClick = {
                    if (currentRoute != screenInfo.screen.name) {
                        navController.navigate(screenInfo.screen.name) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
