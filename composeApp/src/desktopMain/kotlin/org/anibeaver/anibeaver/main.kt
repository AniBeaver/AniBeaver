package org.anibeaver.anibeaver

import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.api.DesktopApiHandler

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    val activityKiller: () -> Unit = {
        this.exitApplication()
    }
    val dataWrapper : DataWrapper = DataWrapper(activityKiller, apiHandler = DesktopApiHandler())
    Window(
        onCloseRequest = ::exitApplication,
        title = "AniBeaver",
    ) {
        App(dataWrapper = dataWrapper)
    }
}