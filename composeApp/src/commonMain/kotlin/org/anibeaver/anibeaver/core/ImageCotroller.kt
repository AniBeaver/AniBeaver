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


    suspend fun chooseAndResaveNewArt(): Art {
        suspend fun resaveImage(image: PlatformFile): PlatformFile {
            fun getExtensionFromFile(image: PlatformFile): String {
                return image.extension.takeIf { it.isNotBlank() }?.let { ".$it" } ?: ""

            }

            createImagesDir()

            //if already in the directory
            val parent = image.parent()
            if (parent != null) {
                if (parent.absolutePath() == imagesDir.absolutePath()) {
                    return image
                }
            }
            //otherwise copy into dir

            val extension = getExtensionFromFile(image)
            val destination = getDestinationFile(extension)
            image.copyTo(destination)
            return destination
        }

        val chosen: PlatformFile? = FileKit.openFilePicker(
            mode = FileKitMode.Single, type = FileKitType.File(listOf("jpg", "jpeg", "png", "webp"))
        )

        val image = chosen ?: throw IllegalStateException("No image chosen")
        val resaved = resaveImage(image)
        return artFromImage(resaved, "custom")
    }

    suspend fun downloadNewArt(link: String): Art {
        suspend fun downloadImage(link: String): PlatformFile {
            createImagesDir()
            fun getExtensionFromLink(link: String): String {
                return run {
                    val path = URL(link).path
                    val idx = path.lastIndexOf('.')
                    if (idx >= 0 && idx < path.length - 1) {
                        "." + path.substring(idx + 1)
                    } else {
                        ""
                    }
                }

            }

            val imageBytes = withContext(Dispatchers.IO) {
                URL(link).openStream().use { it.readBytes() }
            }
            val extension = getExtensionFromLink(link)
            val destination = getDestinationFile(extension)
            destination.write(imageBytes)
            return destination
        }

        val downloaded = downloadImage(link)
        return artFromImage(downloaded, link)
    }

    fun cleanUpImagesDir() {
        //TODO: go through all the entries and delete any images that aren't mentioned in any
    }


}
