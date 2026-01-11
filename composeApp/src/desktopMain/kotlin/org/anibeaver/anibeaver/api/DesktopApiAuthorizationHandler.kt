package org.anibeaver.anibeaver.api

import java.awt.Desktop
import java.net.URI

class DesktopApiAuthorizationHandler : ApiAuthorizationHandler(null) {
    override fun openUrl(url: String) {
        try {
            Desktop.getDesktop().browse(URI(url))
        } catch (e: Exception) {
            println("Failed to open URL: $url with error: ${e.message}")
        }
    }
}