package org.anibeaver.anibeaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.tokenStore

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(
                darkTheme = isSystemInDarkTheme(),
                dataWrapper = DataWrapper(
                    activityKiller = { finish() },
                    apiHandler = ApiHandler(AndroidApiAuthorizationHandler(this)),
                    tokenStore = tokenStore(
                        "org.anibeaver.anibeaver",
                        "anilist",
                        platformContext = this
                    )
                ),
                windowSizeClass = androidx.compose.material3.windowsizeclass.calculateWindowSizeClass(this)
            )
        }
    }
}

/*
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun AppAndroidPreview() {
    App(
        dataWrapper = DataWrapper(
            activityKiller = { },
            apiHandler = null
        ),
        windowSizeClass = androidx.compose.material3.windowsizeclass.calculateWindowSizeClass(this)
    )
}
 */