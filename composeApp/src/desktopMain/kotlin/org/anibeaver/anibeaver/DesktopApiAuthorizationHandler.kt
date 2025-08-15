package org.anibeaver.anibeaver.api

import org.anibeaver.anibeaver.api.ApiAuthorizationHandler

import java.net.URI
import java.awt.Desktop
import java.io.IOException

class DesktopApiAuthorizationHandler : ApiAuthorizationHandler(null) {
    override fun openUrl(url : String){
        try {
            Desktop.getDesktop().browse(URI(url))
        } catch (e: Exception) {
            println("Failed to open URL: $url with error: ${e.message}")
        }
    }
}