package org.anibeaver.anibeaver

class AndroidApiAuthorizationHandler : ApiAuthorizationHandler {
    override fun openUrl(url: String) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            android.content.ContextCompat.getMainExecutor(android.app.ApplicationProvider.getApplicationContext()).execute {
                android.app.ApplicationProvider.getApplicationContext<android.content.Context>().startActivity(intent)
            }
        } catch (e: Exception) {
            println("Failed to open URL: $url with error: ${e.message}")
        }
    }
}