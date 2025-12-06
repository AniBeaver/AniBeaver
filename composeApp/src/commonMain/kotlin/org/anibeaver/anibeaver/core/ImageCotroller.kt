package org.anibeaver.anibeaver.core

import io.github.vinceglb.filekit.*
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.anibeaver.anibeaver.core.datastructures.Art
import java.net.URL
import java.util.*

object ImageController {
    val filesDir = FileKit.filesDir
    val imagesDir = filesDir / "images"

    fun createImagesDir() {
        if (!imagesDir.exists()) {
            imagesDir.createDirectories()
        }
    }

    fun artFromImage(image: PlatformFile, source: String): Art {
        return Art(
            source = source, localPath = image.path
        )
    }

    private fun getExtensionFromFile(image: PlatformFile): String {
        return image.extension.takeIf { it.isNotBlank() }?.let { ".$it" } ?: ""
    }

    private fun getExtensionFromLink(link: String): String {
        val path = URL(link).path
        val idx = path.lastIndexOf('.')
        return if (idx >= 0 && idx < path.length - 1) {
            "." + path.substring(idx + 1)
        } else {
            ""
        }
    }

    private fun getDestinationFile(extension: String): PlatformFile {
        fun generateFilename(extension: String): PlatformFile {
            val uuid = UUID.randomUUID().toString()
            return imagesDir / "$uuid$extension"
        }

        var destination: PlatformFile
        do {
            destination = generateFilename(extension)
        } while (destination.exists())
        return destination
    }

    private suspend fun downloadImageBytes(link: String): ByteArray {
        return withContext(Dispatchers.IO) {
            URL(link).openStream().use { it.readBytes() }
        }
    }

    private suspend fun downloadImageToPath(link: String, destinationPath: String): PlatformFile {
        createImagesDir()
        val imageBytes = downloadImageBytes(link)
        val destination = PlatformFile(destinationPath)
        destination.write(imageBytes)
        return destination
    }

    suspend fun chooseAndResaveNewArt(): Art? {
        suspend fun resaveImage(image: PlatformFile): PlatformFile {
            createImagesDir()

            //if already in the directory
            val parent = image.parent()
            if (parent != null) {
                if (parent.absolutePath() == imagesDir.absolutePath()) {
                    return image
                }
            }

            val extension = getExtensionFromFile(image)
            val destination = getDestinationFile(extension)
            image.copyTo(destination)
            return destination
        }

        val chosen: PlatformFile? = FileKit.openFilePicker(
            mode = FileKitMode.Single, type = FileKitType.File(listOf("jpg", "jpeg", "png", "webp"))
        )

        if (chosen == null) {
            return null
        }

        val resaved = resaveImage(chosen)
        return artFromImage(resaved, "custom")
    }

    suspend fun downloadNewArt(link: String): Art {
        createImagesDir()
        val imageBytes = downloadImageBytes(link)
        val extension = getExtensionFromLink(link)
        val destination = getDestinationFile(extension)
        destination.write(imageBytes)
        return artFromImage(destination, link)
    }

    suspend fun ensureImageExists(art: Art): PlatformFile? {
        if (art.localPath.isBlank()) {
            return null
        }

        val localFile = PlatformFile(art.localPath)

        if (localFile.exists()) {
            return localFile
        }

        if (art.source.isNotBlank() && art.source != "custom" && art.source != "empty") {
            return try {
                downloadImageToPath(art.source, art.localPath)
            } catch (e: Exception) {
                null
            }
        }

        return null
    }

    fun cleanUpImagesDir() {
        if (!imagesDir.exists()) {
            return
        }

        val allUsedPaths = EntriesController.entries.flatMap { entry ->
            listOfNotNull(
                entry.entryData.coverArt.localPath.takeIf { it.isNotBlank() },
                entry.entryData.bannerArt.localPath.takeIf { it.isNotBlank() }
            )
        }.toSet()

        val imagesDirFile = java.io.File(imagesDir.path)
        val filesInDir = imagesDirFile.listFiles() ?: return

        filesInDir.forEach { file ->
            val filePath = file.absolutePath
            if (filePath !in allUsedPaths) {
                try {
                    file.delete()
                } catch (_: Exception) {
                }
            }
        }
    }


}
