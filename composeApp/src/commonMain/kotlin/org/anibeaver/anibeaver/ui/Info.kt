package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import anibeaver.composeapp.generated.resources.Res
import org.anibeaver.anibeaver.ui.theme.Typography
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun InfoScreen(
    pagePadding: PaddingValues
) {
    var infoText by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        infoText = Res.readBytes("files/info.md").decodeToString()
    }

    Column(
        modifier = Modifier
            .padding(pagePadding)
            .fillMaxSize()
    ) {
        Text(
            "Information",
            style = Typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (infoText != "Loading...") {
            RenderMarkdown(infoText)
        } else {
            Text(infoText)
        }
    }
}

@Composable
fun RenderMarkdown(markdown: String) {
    val lines = markdown.lines()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        lines.forEach { line ->
            when {
                line.startsWith("# ") -> {
                    Text(
                        text = line.removePrefix("# "),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                line.startsWith("## ") -> {
                    Text(
                        text = line.removePrefix("## "),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }

                line.startsWith("### ") -> {
                    Text(
                        text = line.removePrefix("### "),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                line.startsWith("- ") || line.startsWith("* ") -> {
                    Row(modifier = Modifier.padding(start = 16.dp)) {
                        Text("• ", fontSize = 16.sp)
                        Text(
                            text = line.removePrefix("- ").removePrefix("* "),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                line.startsWith("[//]: #") -> {
                    // Skip markdown comments
                }

                line.isBlank() -> {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                else -> {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

