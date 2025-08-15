package org.anibeaver.anibeaver

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.DesktopApiAuthorizationHandler
import org.anibeaver.anibeaver.api.tokenStore

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main() = application {
    val activityKiller: () -> Unit = {
        this.exitApplication()
    }
    val dataWrapper = DataWrapper(
        activityKiller,
        apiHandler = ApiHandler(DesktopApiAuthorizationHandler()),
        tokenStore = tokenStore("org.anibeaver.anibeaver", "anilist", platformContext = null)
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "AniBeaver",
    ) {
        App(dataWrapper = dataWrapper, windowSizeClass = calculateWindowSizeClass())
    }
}