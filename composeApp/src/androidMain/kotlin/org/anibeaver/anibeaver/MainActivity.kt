package org.anibeaver.anibeaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(onCloseRequest = ::exitApplication,
                title = "AniBeaver",
                dataWrapper = DataWrapper(
                    activityKiller = { this.finish() },
                    apiHandler = ApiHandler(AndroidApiAuthorizationHandler())
                ),
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}