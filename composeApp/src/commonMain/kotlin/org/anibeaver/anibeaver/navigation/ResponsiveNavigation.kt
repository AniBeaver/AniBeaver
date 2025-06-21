package org.anibeaver.anibeaver.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass // This was correct
import androidx.compose.runtime.Composable
// Modifier is not directly used by this decision-making composable anymore
// import androidx.compose.ui.Modifier
// NavController and navigationItems are not needed here anymore, App.kt will pass them
// import androidx.navigation.NavController
// import org.anibeaver.anibeaver.navigation.ScreenInfo
import org.anibeaver.anibeaver.utils.rememberWindowSizeClass

/**
 * A sealed interface to represent the type of navigation that should be displayed.
 * This helps App.kt decide which navigation composable to use and how to structure the layout.
 */
sealed interface NavigationMode {
    object BottomNav : NavigationMode
    object NavRail : NavigationMode
    // object None : NavigationMode // Could be added if there are cases with no nav
}

/**
 * Determines whether to use a NavRail or BottomNavigation based on screen width.
 * This function does NOT render the navigation. It only decides the mode.
 *
 * @return A [NavigationMode] indicating whether to use NavRail or BottomNavigation.
 */
@Composable
fun determineNavigationMode(): NavigationMode {
    val windowSizeClass = rememberWindowSizeClass()
    val useNavRail = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    return if (useNavRail) {
        NavigationMode.NavRail
    } else {
        NavigationMode.BottomNav
    }
}
