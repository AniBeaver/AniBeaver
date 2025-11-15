package org.anibeaver.anibeaver.core

import io.github.vinceglb.filekit.*
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import org.anibeaver.anibeaver.core.datastructures.Art
import java.util.*

object ImageController {
    fun downloadImage(link: String): PlatformFile { //called by autofill
        return PlatformFile("")
    }

    fun artFromImage(image: PlatformFile): Art {
        return Art(
            source = "custom",
            localPath = image.path
        )
    }

    suspend fun chooseAndResaveNewArt(): Art {
        val chosen: PlatformFile? = FileKit.openFilePicker(
            mode = FileKitMode.Single,
            type = FileKitType.File(listOf("jpg", "jpeg", "png", "webp"))
        )

        val image = chosen ?: throw IllegalStateException("No image chosen")
        val resaved = resaveImage(image)
        return artFromImage(resaved)
    }

    private suspend fun resaveImage(image: PlatformFile): PlatformFile {
        val filesDir = FileKit.filesDir
        val imagesDir = filesDir / "images"

        if (!imagesDir.exists()) {
            imagesDir.createDirectories()
        }

        val extension = image.extension.takeIf { it.isNotBlank() }?.let { ".$it" } ?: ""

        var destination: PlatformFile
        do {
            destination = generateFilename(imagesDir, extension)
        } while (destination.exists())

        image.copyTo(destination)
        return destination
    }

    private fun generateFilename(imagesDir: PlatformFile, extension: String): PlatformFile {
        val uuid = UUID.randomUUID().toString()
        return imagesDir / "$uuid$extension"
    }
}
