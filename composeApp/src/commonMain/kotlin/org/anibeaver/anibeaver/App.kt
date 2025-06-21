package org.anibeaver.anibeaver


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// import androidx.compose.material3.Button // Not used directly here anymore
import androidx.compose.material3.Scaffold
// import androidx.compose.material3.Text // Not used directly here anymore
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.navigation.Screens
import org.anibeaver.anibeaver.navigation.determineNavigationMode // Updated import
import org.anibeaver.anibeaver.navigation.NavigationMode // Updated import
import org.anibeaver.anibeaver.navigation.mainNavigationItems
import org.anibeaver.anibeaver.navigation.AppNavRail // Import AppNavRail
import org.anibeaver.anibeaver.navigation.AppBottomNavigationBar // Import AppBottomNavigationBar
import org.anibeaver.anibeaver.ui.AnimeScreen
import org.anibeaver.anibeaver.ui.HomeScreen
import org.anibeaver.anibeaver.ui.MangaScreen
import org.anibeaver.anibeaver.ui.SettingsScreen
import org.anibeaver.anibeaver.ui.TestScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

import org.anibeaver.anibeaver.ui.theme.AniBeaverTheme
import org.anibeaver.anibeaver.DataWrapper

@Composable
@Preview
fun App(navController: NavHostController = rememberNavController(), dataWrapper: DataWrapper) {
    AniBeaverTheme(darkTheme = true) {
        val navigationMode = determineNavigationMode() // Determine the mode

        Scaffold(
            bottomBar = {
                if (navigationMode == NavigationMode.BottomNav) {
                    AppBottomNavigationBar(
                        navController = navController,
                        navigationItems = mainNavigationItems
                    )
                }
            }
        ) { innerPadding ->
            Row(Modifier.fillMaxSize().padding(innerPadding)) {
                if (navigationMode == NavigationMode.NavRail) {
                    AppNavRail(
                        navController = navController,
                        navigationItems = mainNavigationItems
                        // Modifier for the NavRail can be added here if needed
                        // e.g., Modifier.fillMaxHeight()
                    )
                }

                val navHostModifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())

                NavHost(
                    navController = navController,
                    startDestination = Screens.Home.name,
                    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                    exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                    popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                    modifier = navHostModifier
                ) {
                    composable(route = Screens.Home.name) {
                        HomeScreen(navController, dataWrapper)
                    }
                    composable(route = Screens.Anime.name) {
                        AnimeScreen(navController, dataWrapper)
                    }
                    composable(route = Screens.Manga.name) {
                        MangaScreen(navController)
                    }
                    composable(route = Screens.Settings.name) {
                        SettingsScreen(navController)
                    }
                    composable(route = Screens.Test.name) {
                        TestScreen(navController, dataWrapper)
                    }
                }
            }
        }
    }
}