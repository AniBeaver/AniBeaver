package org.anibeaver.anibeaver

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import org.anibeaver.anibeaver.di.sharedModule
import org.anibeaver.anibeaver.di.platformModule

import org.koin.core.context.startKoin

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main(){
    startKoin {
        modules(sharedModule, platformModule)
    }

    application {
        val activityKiller: () -> Unit = {
            this.exitApplication()
        }
        val dataWrapper = DataWrapper(
            activityKiller
        )
        Window(
            onCloseRequest = ::exitApplication,
            title = "AniBeaver",
        ) {
            App(dataWrapper = dataWrapper, windowSizeClass = calculateWindowSizeClass())
        }
    }
}