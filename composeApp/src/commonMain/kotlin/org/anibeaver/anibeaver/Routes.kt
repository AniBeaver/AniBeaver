package org.anibeaver.anibeaver

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavItemPosition {
    Center,
    Bottom
}

enum class Screens(val title: String, val icon: ImageVector, val position: NavItemPosition) {
    Home(title = "Home", icon = Icons.Filled.Home, position = NavItemPosition.Center),
    Anime(title = "Anime", icon = Icons.Filled.Videocam, position = NavItemPosition.Center),
    Manga(title = "Manga", icon = Icons.Filled.Book, position = NavItemPosition.Center),
    Account(title = "Account", icon = Icons.Filled.AccountCircle, position = NavItemPosition.Bottom),
    Settings(title = "Settings", icon = Icons.Filled.Settings, position = NavItemPosition.Bottom)
}
