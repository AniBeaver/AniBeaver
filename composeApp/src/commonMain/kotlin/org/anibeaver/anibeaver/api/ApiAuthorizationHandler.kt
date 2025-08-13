package org.anibeaver.anibeaver.api

import org.anibeaver.anibeaver.api.jsonStructures.AccessTokenRepsonse

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.*

import io.ktor.client.call.body

abstract class ApiAuthorizationHandler{
    abstract fun openUrl (url : String)

    val authCodeStorage: AuthCodeStorage = AuthCodeStorage()

    val clientId : Int = -1
    val redirectUri : String = "http://127.0.0.1:8080"

    suspend fun getValidAccessToken(){
        try {
            doAuthorizationRoutine()
        }
        catch(e:Exception) {
            println("Authorization failed")
            println(e.toString())
        }
    }

    private suspend fun doAuthorizationRoutine(){
        var localOauthServer: OAuthLocalServer? = null;

        if(authCodeStorage.authCode==null){
            try {
                println("Starting local server to get authentication code...")
                localOauthServer = OAuthLocalServer(authCodeStorage)
                localOauthServer.start()
                println("Server started successfully. Waiting for authorization...")
                // Keep the server running until we get the auth code
                // The server will be stopped in getAccessToken() after the code is retrieved
            }
            catch(e: Exception) {
                println("Failed running Server")
                throw(e)
            }

            println("${authCodeStorage.authCode}, ${authCodeStorage.accessToken} GURT YO")

            try {
                println("Getting authentication code...")
                getAuthCode()
                val timeOutTries = 1000 // 10 seconds with 200ms delay
                var counter = timeOutTries
                while(authCodeStorage.authCode == null && (counter>0)){
                    delay(200)
                    counter -= 1
                }
                check(counter>0){"Timeout while doing Authorization"}
            }
            catch(e: Exception) {
                println("Acquiring authorisation code failed.")
                throw(e)
            }
        }

        // After getting a callback with the auth code, we can stop the local server
        if (localOauthServer != null) {
            try {
                println("Stopping local OAuth callback server...")
                localOauthServer.stop()
            }
            catch(e: Exception) {
                println("Failed stopping local server")
                throw(e)
            }
        }

        try {
            println("Retrieving access token...")
            getAccessToken()
            authCodeStorage.authCode = null
        }
        catch(e: Exception) {
            println("Failed retrieving access token")
            throw(e)
        }
    }

    private fun getAuthCode(){
        val url : String = "https://anilist.co/api/v2/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri&response_type=code"
        openUrl(url)
    }

    private suspend fun getAccessToken(){
        require(authCodeStorage.authCode != null){"Authentication code may not be null when getting an access token."}

        val client: HttpClient? = HttpClient(CIO){
            engine {
                // this: CIOEngineConfig
                maxConnectionsCount = 1000
                endpoint {
                    // this: EndpointConfig
                    maxConnectionsPerRoute = 100
                    pipelineMaxSize = 20
                    keepAliveTime = 5000
                    connectTimeout = 5000
                    connectAttempts = 5
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
        check(client != null){"Failed creating temporary http client"}

        println("Created temporary HTTP client. Making request to get AL access token...")

        val response = client.post("https://anilist.co/api/v2/oauth/token") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(mapOf(
                "grant_type" to "authorization_code",
                "client_id" to clientId.toString(),
                "client_secret" to retrieveClientSecret(),
                "redirect_uri" to redirectUri,
                "code" to authCodeStorage.authCode
            ))
        }
        client.close()
        check(response.status.value == 200){"Failed to make Api call to get access token. " + response.status.toString()}

        val jsonBody: AccessTokenRepsonse = response.body()
        authCodeStorage.accessToken = jsonBody.access_token
    }

    private fun retrieveClientSecret() : String{
        return ""
    }
}

