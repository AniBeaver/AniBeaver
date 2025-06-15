package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue

import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.RequestType
import org.anibeaver.anibeaver.api.ValueSetter
import org.anibeaver.anibeaver.api.jsonStructures.*

@Composable
@Preview
fun TestScreen(
    navController: NavHostController = rememberNavController(),
    dataWrapper: DataWrapper
) {
    val scope = rememberCoroutineScope()

    Column{
        Text("Test", style = Typography.headlineLarge)

        Button(onClick = {
            navController.navigate(Screens.Home.name)
        }) {
            Text("Go to Home")
        }

        Button(onClick = {
                dataWrapper.apiHandler.openUrl("https://anilist.co/api/v2/oauth/authorize?client_id=27567&response_type=token")
            }) {
                Text("Get Auth Code")
            }
        
        var text2 by remember {mutableStateOf("None!")}
        Text(text2)

        scope.launch{
            dataWrapper.apiHandler.makeRequest(
                variables = mapOf("userName" to "AAA", "type" to "ANIME"),
                valueSetter = ValueSetter({m:MediaListResponse -> text2 = (m.data.MediaListCollection.lists.get(0).entries.get(1).media.title.english.toString())})
            )
        }
    }
}