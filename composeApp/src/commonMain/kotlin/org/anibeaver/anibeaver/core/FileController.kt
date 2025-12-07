package org.anibeaver.anibeaver.core

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker

object FileController {
    suspend fun chooseFile(extensions: List<String>): PlatformFile? {
        return FileKit.openFilePicker(
            mode = FileKitMode.Single,
            type = FileKitType.File(extensions)
        )
    }

    suspend fun chooseDirectory(): PlatformFile? {
        return FileKit.openDirectoryPicker()
    }
}

