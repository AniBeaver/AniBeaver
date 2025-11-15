import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.blur
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.coil.AsyncImage

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

    Box(
        modifier = modifier
            .size(120.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (platformFileToShow != null) {
            AsyncImage(
                file = platformFileToShow,
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
                    .background(Color.LightGray)
            )
        }

        AsyncImage(
            file = platformFileToShow,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(120.dp)
        )
    }
}
