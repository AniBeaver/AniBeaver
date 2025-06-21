package org.anibeaver.anibeaver


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.navigation.Screens
import org.anibeaver.anibeaver.navigation.mainNavigationItems
import org.anibeaver.anibeaver.ui.AnimeScreen
import org.anibeaver.anibeaver.ui.HomeScreen
import org.anibeaver.anibeaver.ui.MangaScreen
import org.anibeaver.anibeaver.ui.SettingsScreen
import org.anibeaver.anibeaver.ui.TestScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold

import org.anibeaver.anibeaver.ui.theme.AniBeaverTheme
import org.anibeaver.anibeaver.DataWrapper

@Composable
@Preview
fun App(navController: NavHostController = rememberNavController(), dataWrapper: DataWrapper) {
    AniBeaverTheme(darkTheme = true) {
        var currentScreen by rememberSaveable { mutableStateOf(Screens.Home) }

        // Sync currentScreen with NavController's current route
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        LaunchedEffect(navBackStackEntry) {
            navBackStackEntry?.destination?.route?.let { route ->
                Screens.entries.find { it.name == route }?.let { screen ->
                    if (currentScreen != screen) {
                        currentScreen = screen
                    }
                }
            }
        }

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                mainNavigationItems.forEach { screenInfo ->
                    item(
                        icon = { Icon(screenInfo.icon, contentDescription = screenInfo.title) },
                        label = { Text(screenInfo.title) },
                        selected = screenInfo.screen == currentScreen,
                        onClick = {
                            if (currentScreen != screenInfo.screen) {
                                currentScreen = screenInfo.screen
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
        ) { // This is the content lambda
            NavHost(
                navController = navController,
                startDestination = Screens.Home.name,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                modifier = Modifier
                    .fillMaxSize() // NavHost should fill the content area
                    .verticalScroll(rememberScrollState())
                    // The NavigationSuiteScaffold handles padding for its navigation elements.
                    // If NavHost content needs specific padding, it should be applied here or within individual screens.
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
