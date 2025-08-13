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

    fun makeRandomRequests() {
        scope.launch{
            var id: Int = 0
            dataWrapper.apiHandler.makeRequest(
                variables = mapOf("search" to "ame to kimi to", "type" to "MANGA"),
                valueSetter = ValueSetter { mediaQuery: MediaQuery -> id = (mediaQuery.data.media.id) }
            )
            var notes: String = ""
            dataWrapper.apiHandler.makeAuthorizedRequest(
                variables = mapOf("mediaId" to id.toString()),
                valueSetter = ValueSetter { m: SaveMediaListQuery ->
                    val savedNotes = m.data.saveMediaListEntry.notes
                    if (savedNotes != null) {
                        notes = savedNotes
                    }
                }
            )
            //text2 = notes
            dataWrapper.apiHandler.makeAuthorizedRequest(
                variables = mapOf("mediaId" to id.toString(), "status" to "PLANNING", "notes" to notes+"hi from anibeaver"),
                valueSetter = ValueSetter { m: SaveMediaListQuery -> println("Mutation completed") }
            )
        }
    }

    Column{
        Text("Test", style = Typography.headlineLarge)

        Button(onClick = {
            navController.navigate(Screens.Home.name)
        }) {
            Text("Go to Home")
        }

        Button(onClick = {
                scope.launch{dataWrapper.apiHandler.apiAuthorizationHandler.getValidAccessToken()}
            }) {
                Text("Get Auth Code")
            }

        Button(onClick = {
            makeRandomRequests()
        }) {
            Text("Make Random Requests")
        }
        
        var text2 by remember {mutableStateOf("None!")}
        Text(text2)
    }
}