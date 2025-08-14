package org.anibeaver.anibeaver

import android.content.Context
import org.anibeaver.anibeaver.api.ApiAuthorizationHandler
import androidx.core.net.toUri


class AndroidApiAuthorizationHandler(private val context: Context) : ApiAuthorizationHandler() {
    override fun openUrl(url: String) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, url.toUri())
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)
        } catch (e: Exception) {
            println("Failed to open URL: $url with error: ${e.message}")
        }
    }
}