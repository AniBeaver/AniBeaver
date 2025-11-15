package org.anibeaver.anibeaver

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit

import org.anibeaver.anibeaver.di.sharedModule
import org.anibeaver.anibeaver.di.platformModule

import org.koin.core.context.startKoin

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main(){
    startKoin {
        modules(sharedModule, platformModule)
    }

    FileKit.init(appId = "AniBeaver")

    application {
        val activityKiller: () -> Unit = {
            this.exitApplication()
        }
        Window(
            onCloseRequest = ::exitApplication,
            title = "AniBeaver",
        ) {
            App(activityKiller = activityKiller, windowSizeClass = calculateWindowSizeClass())
        }
    }
}