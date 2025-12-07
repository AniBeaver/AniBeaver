package org.anibeaver.anibeaver.core

import io.github.vinceglb.filekit.*
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

    suspend fun textToFile(file: PlatformFile, text: String) {
        file.write(text.toByteArray())
    }

    suspend fun textToFile(file: PlatformFile): String {
        return file.readBytes().decodeToString()
    }

    fun listFiles(directory: PlatformFile, filter: (PlatformFile) -> Boolean = { true }): List<PlatformFile> {
        if (!directory.exists()) {
            return emptyList()
        }

        val dirFile = java.io.File(directory.path)
        val files = dirFile.listFiles() ?: return emptyList()

        return files
            .filter { it.isFile }
            .map { PlatformFile(it.absolutePath) }
            .filter { filter(it) }
    }

    fun deleteFile(file: PlatformFile): Boolean {
        return try {
            java.io.File(file.path).delete()
        } catch (_: Exception) {
            false
        }
    }

    fun createDirectory(directory: PlatformFile) {
        if (!directory.exists()) {
            directory.createDirectories()
        }
    }
}

