package org.anibeaver.anibeaver.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.PlatformFile
import org.anibeaver.anibeaver.core.ImageController
import org.anibeaver.anibeaver.core.datastructures.Art

@Composable
fun ImageInput(
    modifier: Modifier = Modifier,
    imagePath: String?,
    onImagePathChange: (String) -> Unit,
    onClick: () -> Unit = {}
) {
    var platformFileToShow: PlatformFile? by remember {
        mutableStateOf(imagePath?.let { PlatformFile(it) })
    }

    LaunchedEffect(imagePath) {
        platformFileToShow = imagePath?.let { PlatformFile(it) }
    }

    ImagePreview(
        modifier = modifier,
        file = platformFileToShow,
        onClick = onClick
    )
}

@Composable
fun ImageInput(
    modifier: Modifier = Modifier,
    art: Art?,
    onClick: () -> Unit = {}
) {
    var platformFileToShow: PlatformFile? by remember { mutableStateOf(null) }

    LaunchedEffect(art) {
        if (art == null) {
            platformFileToShow = null
            return@LaunchedEffect
        }

        platformFileToShow = ImageController.ensureImageExists(art)
    }

    ImagePreview(
        modifier = modifier,
        file = platformFileToShow,
        onClick = onClick
    )
}
