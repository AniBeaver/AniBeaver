package org.anibeaver.anibeaver.api

class AuthCodeStorage(context: Any?) {
    private val tokenStore = tokenStore("org.anibeaver.anibeaver", "anilist", context)

    var accessToken: String? = null

    init {
        val keyringToken = tokenStore.load()
        if (keyringToken != null) {
            accessToken = keyringToken
            println("Access token loaded from secure storage.")
        } else {
            println("No access token found in secure storage. User might not be signed in or something else went wrong.")
        }
    }
}