package org.anibeaver.anibeaver.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.attafitamim.krop.core.CropError
import com.attafitamim.krop.core.CropResult
import com.attafitamim.krop.core.rememberImageCropper
import com.attafitamim.krop.ui.ImageCropperDialog
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.anibeaver.anibeaver.core.ImageCropperController
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun ImageCropperHost(onImageCropped: (ImageBitmap?) -> Unit) {
    val fileToEdit = ImageCropperController.getCropDialogFile()
    val scope = rememberCoroutineScope()
    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState

    LaunchedEffect(fileToEdit) {
        if (fileToEdit != null) {
            scope.launch {
                val bytes = fileToEdit.readBytes()
                val bitmap = SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()

                when (val result = imageCropper.crop(bitmap)) {
                    CropResult.Cancelled -> {
                        ImageCropperController.dismissCrop()
                        onImageCropped(null)
                    }

                    is CropError -> {
                        ImageCropperController.dismissCrop()
                        onImageCropped(null)
                    }

                    is CropResult.Success -> {
                        ImageCropperController.dismissCrop()
                        onImageCropped(result.bitmap)
                    }
                }
            }
        }
    }

    if (cropState != null) {
        ImageCropperDialog(state = cropState)
    }
}
