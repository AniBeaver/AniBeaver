package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.clickable
import org.anibeaver.anibeaver.DataWrapper

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    dataWrapper: DataWrapper
) {
    var showEditPopup by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 32.dp).fillMaxSize() ){
        SearchBar(
            modifier = Modifier.
                fillMaxSize()
                .padding(horizontal = 32.dp)
                .height(48.dp)
                .semantics { traversalIndex = 0F },

            expanded = false,
            onExpandedChange = {},

            inputField = {SearchBarDefaults.InputField(
                query = "",
                onQueryChange = {},
                placeholder = {
                    Text("Search for an anime or manga...", fontSize = 0.9.em, lineHeight = 1.2.em)
                },
                onSearch = {},
                onExpandedChange = {},
                expanded = true,
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search Icon") },
            )}
        ) {
            Text("Search for an anime or manga", style = Typography.headlineLarge)
        }


        Row(
            modifier = Modifier.height(IntrinsicSize.Max).padding(top = 48.dp)
        ) {
            Column(modifier = Modifier.weight(3f)) {
                // Row for "Currently Watching" (Anime)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Currently Watching", style = Typography.headlineMedium)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to Anime Page",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            // FIXME: this should also change the sidebar nav status
                            .clickable { navController.navigate(Screens.Anime.name) }
                    )
                }

                Row(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Replace with Card composables for anime
                    repeat(3) {
                        androidx.compose.material3.Card(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(120.dp)
                                .weight(1f)
                        ) {
                            Text("Anime $it", modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                // Row for "Currently Reading" (Manga)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                ) {
                    Text("Currently Reading", style = Typography.headlineMedium)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to Manga Page",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            // FIXME: this should also change the sidebar nav status
                            .clickable { navController.navigate(Screens.Manga.name) }
                    )
                }

                Row(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    // Replace with your Card composables for manga
                    repeat(3) {
                        androidx.compose.material3.Card(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(120.dp)
                                .weight(1f)
                        ) {
                            Text("Manga $it", modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }

            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 16.dp, end = 8.dp),
                thickness = Dp.Hairline
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text("Airing soon", style = Typography.headlineSmall)
                Text("None of your Anime are airing soon.", modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}