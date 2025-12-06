package org.anibeaver.anibeaver.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.blur
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.coil.AsyncImage

@Composable
fun ImagePreview(
    modifier: Modifier = Modifier,
    file: PlatformFile?,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (file != null) {
            AsyncImage(
                file = file,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .blur(16.dp, 16.dp)
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0x66000000))
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFF1A1A1A))
            )
        }

        AsyncImage(
            file = file,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(120.dp)
        )
    }
}

//TODO: if no image chosen
