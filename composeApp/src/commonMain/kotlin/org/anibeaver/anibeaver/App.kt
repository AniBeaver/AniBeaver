package org.anibeaver.anibeaver


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.ui.AnimeScreen
import org.anibeaver.anibeaver.ui.HomeScreen
import org.anibeaver.anibeaver.ui.MangaScreen
import org.anibeaver.anibeaver.ui.SettingsScreen
import org.anibeaver.anibeaver.ui.TestScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

import org.anibeaver.anibeaver.ui.theme.AniBeaverTheme
import org.anibeaver.anibeaver.ui.layout.Sidebar
import org.anibeaver.anibeaver.ui.theme.getColorScheme

@Composable
@Preview
fun App(
    navController: NavHostController = rememberNavController(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    dataWrapper: DataWrapper,
    windowSizeClass: WindowSizeClass
) {
    val colors by remember(darkTheme) {
        derivedStateOf {
            getColorScheme(darkTheme)
        }
    }

    val showSidebar by remember(windowSizeClass) {
        derivedStateOf {
            windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact
        }
    }

    AniBeaverTheme (darkTheme = true) {
        Scaffold {
            Row {
                if (showSidebar) {
                    Sidebar(navController, colors)
                }

                Column{
                    NavHost(
                        navController = navController,
                        startDestination = Screens.Home.name,
                        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                        modifier = Modifier
                            .safeContentPadding()
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        composable (route = Screens.Home.name) {
                            HomeScreen(navController, dataWrapper)
                        }
                        composable (route = Screens.Anime.name) {
                            AnimeScreen(navController, dataWrapper)
                        }
                        composable (route = Screens.Manga.name) {
                            MangaScreen(navController)
                        }
                        composable (route = Screens.Settings.name) {
                            SettingsScreen(navController)
                        }
                        composable (route = Screens.Test.name) {
                            TestScreen(navController, dataWrapper)
                        }
                    }
                }
            }
        }
    }
}