package org.anibeaver.anibeaver.core

import io.github.vinceglb.filekit.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.anibeaver.anibeaver.core.datastructures.Art
import java.net.URL
import java.util.*

object ImageController {
    val filesDir = FileKit.filesDir
    val imagesDir = filesDir / "images"

    fun createImagesDir() {
        FileController.createDirectory(imagesDir)
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
        //TODO: seems like the downloaded images are very low res? !!!
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

        val chosen = FileController.chooseFile(listOf("jpg", "jpeg", "png", "webp")) ?: return null

        val resaved = resaveImage(chosen)
        return artFromImage(resaved, "custom")
    }

    suspend fun downloadNewArt(link: String): Art {
        if (link.isBlank()) {
            return Art("empty", "")
        }

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

        if (art.source.isNotBlank() && art.source != "custom" && art.source != "empty") { //redownload if needed
            return try {
                downloadImageToPath(art.source, art.localPath)
            } catch (e: Exception) {
                null //TODO: potentially alert user that the link should be re-set by re-pulling or manually downloading? Likely won't happen but still
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

        val filesInDir = FileController.listFiles(imagesDir)

        filesInDir.forEach { file ->
            if (file.path !in allUsedPaths) {
                FileController.deleteFile(file)
            }
        }
    }


}
