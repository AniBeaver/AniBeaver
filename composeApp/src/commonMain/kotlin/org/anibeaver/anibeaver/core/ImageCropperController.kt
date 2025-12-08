package org.anibeaver.anibeaver.core

import androidx.compose.runtime.mutableStateOf
import io.github.vinceglb.filekit.PlatformFile

object ImageCropperController {
    private val cropDialogState = mutableStateOf<PlatformFile?>(null)

    fun getCropDialogFile() = cropDialogState.value

    fun requestCrop(file: PlatformFile) {
        cropDialogState.value = file
    }

    fun dismissCrop() {
        cropDialogState.value = null
    }
}

