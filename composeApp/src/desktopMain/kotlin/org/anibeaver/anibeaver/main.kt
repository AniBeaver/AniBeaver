package org.anibeaver.anibeaver

import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.DesktopApiAuthorizationHandler
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main() = application {
    val activityKiller: () -> Unit = {
        this.exitApplication()
    }
    val dataWrapper : DataWrapper = DataWrapper(activityKiller, apiHandler = ApiHandler(DesktopApiAuthorizationHandler()))
    Window(
        onCloseRequest = ::exitApplication,
        title = "AniBeaver",
    ) {
        App(dataWrapper = dataWrapper,windowSizeClass = calculateWindowSizeClass() )
    }
}