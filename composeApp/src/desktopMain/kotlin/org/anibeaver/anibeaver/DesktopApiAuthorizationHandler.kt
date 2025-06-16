package org.anibeaver.anibeaver.api

import org.anibeaver.anibeaver.api.ApiAuthorizationHandler

import java.net.URI
import java.awt.Desktop
import java.io.IOException

class DesktopApiAuthorizationHandler : ApiAuthorizationHandler(){
    //Temporary Test!!! While this solution works, it should be replaced soon, because it is insecure
    //Also only designed for linux
    override fun openUrl(url : String){
        val browsers = listOf(
            "xdg-open",        // Default first
            "google-chrome",   // Common alternatives
            "firefox",
            "chromium",
            "vivaldi",
            "/usr/bin/xdg-open"  // Absolute path
        )
        browsers.forEach { browser ->
            try {
                Runtime.getRuntime().exec(arrayOf(browser, url))
                return
            } catch (e: IOException) {
                println(browser + " didn't work. Trying next Browser.")
            }
        }
    }
}