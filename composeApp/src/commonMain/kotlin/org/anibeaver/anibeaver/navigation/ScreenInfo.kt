package org.anibeaver.anibeaver.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.vector.ImageVector

// Defines the structure for each screen/navigation item
data class ScreenInfo(
    val screen: Screens, // Changed from route: String to screen: Screens
    val title: String, // Using String for title directly for now, can be resource ID
    val icon: ImageVector
)

// List of screens to be used in navigation components
val mainNavigationItems = listOf(
    ScreenInfo(
        screen = Screens.Home,
        title = "Home",
        icon = Icons.Filled.Home
    ),
    ScreenInfo(
        screen = Screens.Anime,
        title = "Anime",
        icon = Icons.Filled.Videocam // Placeholder icon
    ),
    ScreenInfo(
        screen = Screens.Manga,
        title = "Manga",
        icon = Icons.Filled.Book // Placeholder icon
    ),
    ScreenInfo(
        screen = Screens.Settings,
        title = "Settings",
        icon = Icons.Filled.Settings
    ),
    ScreenInfo(
        screen = Screens.Test,
        title = "Test",
        icon = Icons.Filled.AccountCircle // Placeholder, change as needed
    )
)
