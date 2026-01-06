package org.anibeaver.anibeaver.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import anibeaver.composeapp.generated.resources.Res
import anibeaver.composeapp.generated.resources.abvr_icon

import org.anibeaver.anibeaver.NavItemPosition
import org.anibeaver.anibeaver.Screens
import org.anibeaver.anibeaver.ui.components.basic.DarkTooltipBox
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sidebar(
    navController: NavHostController = rememberNavController(),
    colors: ColorScheme,
    onCreateEntry: (forManga: Boolean) -> Unit = {},
    onQuickSearch: (forManga: Boolean, onSelected: (String) -> Unit) -> Unit = { _, _ -> }
) {
    val startDestination = Screens.Anime
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination.name) }

    // update bottom bar when destination changes
    navController.addOnDestinationChangedListener { _, destination, _ ->
        if (destination.route == selectedDestination) return@addOnDestinationChangedListener
        selectedDestination = destination.route ?: startDestination.name
    }

    NavigationRail(
        containerColor = colors.surfaceContainer,
        modifier = Modifier.zIndex(1f).fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.abvr_icon),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                CenterSidebarEntries(
                    navController,
                    selectedDestination
                ) { newDest -> selectedDestination = newDest }

                Spacer(modifier = Modifier.height(20.dp))

                val buttonsEnabled = selectedDestination == Screens.Anime.name || selectedDestination == Screens.Manga.name

                DarkTooltipBox(tooltip = "Quick add entry") {
                    FilledIconButton(
                        onClick = {
                            when (selectedDestination) {
                                Screens.Anime.name -> onQuickSearch(false) { }
                                Screens.Manga.name -> onQuickSearch(true) { }
                                else -> { }
                            }
                        },
                        enabled = buttonsEnabled,
                        colors = IconButtonDefaults.filledIconButtonColors(),
                        shape = ShapeDefaults.Medium,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Search, null, Modifier.size(14.dp).offset(y = (-6).dp))
                            Icon(Icons.Filled.Add, null, Modifier.size(14.dp).offset(x = (-6).dp, y = 4.dp))
                            Icon(Icons.Filled.AutoAwesome, null, Modifier.size(14.dp).offset(x = 6.dp, y = 4.dp))
                        }
                    }
                }

                DarkTooltipBox(tooltip = "Manual add entry") {
                    FilledIconButton(
                        onClick = {
                            when (selectedDestination) {
                                Screens.Anime.name -> onCreateEntry(false)
                                Screens.Manga.name -> onCreateEntry(true)
                                else -> { }
                            }
                        },
                        enabled = buttonsEnabled,
                        colors = IconButtonDefaults.filledIconButtonColors(),
                        shape = ShapeDefaults.Medium,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.Add, "Create new entry", Modifier.size(28.dp))
                    }
                }
            }
            Column {
                BottomSidebarEntries(
                    navController,
                    selectedDestination
                ) { newDest -> selectedDestination = newDest }
            }
        }
    }
}


@Composable
fun CenterSidebarEntries(
    navController: NavHostController = rememberNavController(),
    selectedDestination: String,
    setDestination: (String) -> Unit
) {
    Screens.entries.filter { s -> s.position == NavItemPosition.Center }
        .forEach { destination ->
            SidebarNavItem(
                navController,
                destination,
                selectedDestination,
                setDestination
            )
        }
}

@Composable
fun BottomSidebarEntries(
    navController: NavHostController = rememberNavController(),
    selectedDestination: String,
    setDestination: (String) -> Unit
) {
    Screens.entries.filter { s -> s.position == NavItemPosition.Bottom }
        .forEach { destination ->
            SidebarNavItem(
                navController,
                destination,
                selectedDestination,
                setDestination
            )
        }
}

@Composable
fun SidebarNavItem(
    navController: NavHostController,
    destination: Screens,
    currentlySelectedDestination: String,
    setDestination: (String) -> Unit
) {
    DarkTooltipBox(tooltip = destination.name) {
        NavigationRailItem(
            selected = destination.name == currentlySelectedDestination,
            onClick = {
                if (destination.name != currentlySelectedDestination) {
                    navController.navigate(route = destination.name)
                    setDestination(destination.name)
                }
            },
            icon = { Icon(destination.icon, destination.title) }
        )
    }
}