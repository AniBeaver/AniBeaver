package org.anibeaver.anibeaver.api

import org.anibeaver.anibeaver.api.AuthCodeStorage
import org.anibeaver.anibeaver.api.jsonStructures.AccessTokenRepsonse

import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.net.BindException

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.coroutines.*

import io.ktor.client.call.body

abstract class ApiAuthorizationHandler{
    abstract fun openUrl (url : String)

    val authCodeStorage: AuthCodeStorage = AuthCodeStorage()

    val clientId : Int = 27567
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
        if(authCodeStorage.authCode==null){
            try {
                runBasicServer()
            }
            catch(e: Exception) {
                println("Failed running Server")
                throw(e)
            }
            try {
                println("Getting authentication code")
                getAuthCode()
                val timeOutTries = 1000
                var counter = timeOutTries
                while(authCodeStorage.authCode == null && (counter>0)){
                    delay(200)
                    counter -= 1
                }
                check(counter>0){"Timeout while doing Authorization"}
            }
            catch(e: Exception) {
                println("Aquiring authorisation code failed.")
                throw(e)
            }
        }

        try {
            println("Retrieving access token")
            getAccessToken()
            authCodeStorage.authCode = null
        }
        catch(e: Exception) {
            println("Failed retrieving acces token")
            throw(e)
        }
    }
    private suspend fun runBasicServer(){
        try {
            println("Running local server.")
            embeddedServer(Netty, port = 8080) {
                routing {
                    get("/") {
                        //Mandatory ; cause Kotlin jank
                        {code : String? -> if(code!=null){authCodeStorage.authCode = code}}(call.request.queryParameters["code"]);
                        {token : String? -> if(token!=null){authCodeStorage.accessToken = token}}(call.request.queryParameters["access_token"])
                        call.respondText("Authentication complete. Please close this tab")
                    }
                }
            }.start(wait = false)
            println("Server started")
        }
        catch(e : BindException) {
            println("Server already running ...")
            println(e.toString())
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
        check(false){"Client secret is not present"}
        return ""
    }
}

