package org.anibeaver.anibeaver


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.api.ApiHandler

@Composable
@Preview
fun App(navController: NavHostController = rememberNavController(), dataWrapper: DataWrapper) {
    AniBeaverTheme (darkTheme = true) {
        Scaffold {
            Column{
                NavHost(
                    navController = navController,
                    startDestination = Screens.Home.name,
                    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                    exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                    popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .safeContentPadding()
                ) {
                    composable (route = Screens.Home.name) {
                        HomeScreen(navController, dataWrapper)
                    }
                    composable (route = Screens.Anime.name) {
                        AnimeScreen(navController)
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