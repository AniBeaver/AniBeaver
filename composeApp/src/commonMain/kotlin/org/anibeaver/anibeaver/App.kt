package org.anibeaver.anibeaver


import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.ui.*
import org.anibeaver.anibeaver.ui.layout.BottomNavBar
import org.anibeaver.anibeaver.ui.layout.Sidebar
import org.anibeaver.anibeaver.ui.theme.AniBeaverTheme
import org.anibeaver.anibeaver.ui.theme.getColorScheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    navController: NavHostController? = rememberNavController(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    activityKiller: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    if (navController == null) {
        return
    }

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

    val paddingValues by remember(showSidebar) {
        derivedStateOf {
            if (showSidebar) {
                PaddingValues(horizontal = 32.dp, vertical = 24.dp)
            } else {
                PaddingValues(horizontal = 16.dp, vertical = 24.dp)
            }
        }
    }

    // On app start, populate the EntryController with the entries from the database
    // While our app is open, the EntryController is the source of truth for entries
    val appViewModel: AppViewModel = remember { AppViewModel() }
    val sharedAnimeViewModel: AnimeViewModel = remember { AnimeViewModel() }

    AniBeaverTheme(darkTheme = true) {
        Scaffold { padding ->
            Row {
                if (showSidebar) {
                    Sidebar(navController, colors)
                }

                Column(
                    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f).padding(padding)) {
                        NavHost(
                            navController = navController, startDestination = Screens.Home.name, enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start, tween(700)
                            )
                        }, exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start, tween(700)
                            )
                        }, popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                            )
                        }, popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.End, tween(700)
                            )
                        }, modifier = Modifier.fillMaxHeight(1f).verticalScroll(rememberScrollState())
                        ) {
                            composable(route = Screens.Home.name) {
                                HomeScreen(navController, sharedAnimeViewModel)
                            }
                            composable(route = Screens.Anime.name) {
                                EntriesScreen(navController, forManga=false, viewModel = sharedAnimeViewModel)
                            }
                            composable(route = Screens.Manga.name) {
                                EntriesScreen(navController, forManga=true, viewModel = sharedAnimeViewModel)
                            }
                            composable(route = Screens.Settings.name) {
                                SettingsScreen(navController)
                            }
                            composable(route = Screens.Account.name) {
                                AccountScreen(paddingValues, windowSizeClass)
                            }
                        }
                    }

                    if (!showSidebar) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BottomNavBar(navController)
                        }
                    }
                }
            }
        }
    }
}