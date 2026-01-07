package org.anibeaver.anibeaver.api

import kotlinx.coroutines.delay

abstract class ApiAuthorizationHandler(private val context: Any?) {
    abstract fun openUrl(url: String)

    val authCodeStorage: AuthCodeStorage = AuthCodeStorage(context)

    suspend fun getValidAccessToken() {
        try {
            doAuthorizationRoutine()
        } catch (e: Exception) {
            println("Authorization failed")
            println(e.toString())
        }
    }

    private suspend fun doAuthorizationRoutine() {
        var oAuthLocalServer: OAuthLocalServer? = null

        if (authCodeStorage.accessToken == null) {
            try {
                println("Starting local server to get authentication code...")
                oAuthLocalServer = OAuthLocalServer(authCodeStorage)
                oAuthLocalServer.start()
                println("Server started successfully. Waiting for authorization...")
                // Keep the server running until we get the access token
                // The server will be stopped after the access token is received
            } catch (e: Exception) {
                println("Failed running Server")
                throw (e)
            }

            try {
                println("Getting access token...")
                getAccessToken()
                val timeOutTries = 1000
                var counter = timeOutTries
                while (authCodeStorage.accessToken == null && (counter > 0)) {
                    delay(200)
                    counter -= 1
                }
                check(counter > 0) { "Timeout while doing Authorization" }
            } catch (e: Exception) {
                println("Acquiring authorisation code failed.")
                println("Stopping local OAuth callback server...")
                oAuthLocalServer.stop()
                throw (e)
            }
        }

        // After getting the access token, we can stop the local server
        if (oAuthLocalServer != null) {
            try {
                println("Stopping local OAuth callback server...")
                oAuthLocalServer.stop()

                println("Saving token to keystore...")
                val tokenStore = tokenStore("org.anibeaver.anibeaver", "anilist", context)
                tokenStore.save(authCodeStorage.accessToken ?: "")
                println("Token saved successfully.")
            } catch (e: Exception) {
                println("Failed stopping local server")
                throw (e)
            }
        }
    }

    private fun getAccessToken() {
        openUrl("http://127.0.0.1:8080/start")
    }
}

