package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

import org.anibeaver.anibeaver.DataWrapper
import org.anibeaver.anibeaver.api.ValueSetter
import org.anibeaver.anibeaver.api.jsonStructures.*
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun AccountScreen(
    navController: NavHostController = rememberNavController(),
    dataWrapper: DataWrapper,
) {
    val scope = rememberCoroutineScope()
    var userInfo by remember { mutableStateOf<Profile?>(null) }

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

    fun getUserProfile() {
        scope.launch {
            dataWrapper.apiHandler.makeAuthorizedRequest(
                variables = mapOf("id" to "1", "name" to "name"),
                valueSetter = ValueSetter { userProfileQuery: UserProfileQuery ->
                    userInfo = userProfileQuery.data.profile
                }
            )
        }
    }

    // Header
    Column (modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp).fillMaxSize()) {
        Text(
            "Account",
            style = Typography.headlineLarge,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            "Here you can manage the connection to your AniList account and view your profile information.",
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (userInfo != null) {
                // Profile Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box {
                        // Banner with fade effect
                        Box(modifier = Modifier.height(130.dp)) {
                            AsyncImage(
                                model = userInfo?.bannerImage ?: "https://picsum.photos/400/120",
                                contentDescription = "User Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Gradient overlay for fade effect
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.6f)
                                            )
                                        )
                                    )
                            )
                        }

                        // Profile info overlay
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .offset(y = 70.dp)
                                .background(color = Color.Transparent),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Profile picture
                            Card(
                                shape = CircleShape,
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                AsyncImage(
                                    model = userInfo?.avatar?.large ?: "https://picsum.photos/80",
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Username
                            Text(
                                text = userInfo?.name ?: "Unknown User",
                                style = Typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(60.dp))
                }

                // Statistics Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Anime Statistics Card
                    Card(
                        modifier = Modifier.weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Anime",
                                style = Typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = userInfo?.statistics?.anime?.count?.toString() ?: "0",
                                style = Typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // Manga Statistics Card
                    Card(
                        modifier = Modifier.weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Manga",
                                style = Typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = userInfo?.statistics?.manga?.count?.toString() ?: "0",
                                style = Typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            } else {
                // Empty State - Login Invitation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            "Welcome to AniBeaver!",
                            style = Typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            "Connect your AniList account to view your profile and manage your anime & manga lists",
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                        )

                        Button(
                            onClick = { getUserProfile() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Login,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connect AniList Account")
                        }
                    }
                }
            }
        }
    }
}