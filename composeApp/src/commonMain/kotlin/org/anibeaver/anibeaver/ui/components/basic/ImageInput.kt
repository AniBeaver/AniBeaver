package org.anibeaver.anibeaver.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.PlatformFile

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
