package org.anibeaver.anibeaver.ui.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import org.jetbrains.compose.resources.painterResource

@Composable
fun Sidebar(
    navController: NavHostController = rememberNavController(),
    colors: ColorScheme
) {
    val startDestination = Screens.Home
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination.name) }

    NavigationRail(
        containerColor = colors.surfaceContainer,
        modifier = Modifier.zIndex(1f).fillMaxHeight()
    ) {
        Column (
            modifier = Modifier.fillMaxHeight().padding(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.abvr_icon),
                contentDescription = null, // decorative element
                modifier = Modifier.size(60.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                CenterSidebarEntries(
                    navController,
                    selectedDestination,
                    {newDest -> selectedDestination = newDest }
                )

                FilledIconButton(
                    onClick = {},
                    colors = IconButtonDefaults.filledIconButtonColors(),
                    shape = ShapeDefaults.Medium,
                    modifier = Modifier.padding(top = 12.dp).size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "yeet",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Column() {
                BottomSidebarEntries(
                    navController,
                    selectedDestination,
                    {newDest -> selectedDestination = newDest }
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SidebarNavItem(
    navController: NavHostController,
    destination: Screens,
    currentlySelectedDestination: String,
    setDestination: (String) -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip { Text(destination.name) }
        },
        state = rememberTooltipState()
    ) {
        NavigationRailItem(
            selected = destination.name == currentlySelectedDestination,
            onClick = {
                if (destination.name != currentlySelectedDestination) {
                    navController.navigate(route = destination.name)
                    setDestination(destination.name)
                }
            },
            icon = {
                Icon(
                    destination.icon,
                    contentDescription = destination.title,
                )
            }
        )
    }
}