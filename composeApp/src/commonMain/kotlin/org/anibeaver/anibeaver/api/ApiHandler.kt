package org.anibeaver.anibeaver.api

import org.anibeaver.anibeaver.api.jsonStructures.*

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import io.ktor.client.call.body

import kotlin.reflect.KClass

abstract class ApiHandler{
    abstract fun openUrl (url : String)

    val client = HttpClient(CIO){
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
    var standartVariables:Map<String, String> = emptyMap<String, String>()

    suspend fun makeManualRequest(
        query : String = "",
        variables: Map<String, String>? = null,
        url: String = "https://graphql.anilist.co",
        accesToken: String = ""
    ):HttpResponse {
        require(query!=null){"Null was passed as query"}
        require(!query.isEmpty()){"An empty String has been passed as query"}
        require(isValidQuery(query)){"An invalid query has been passed"}

        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(GraphQLRequest(
                query = query.trimIndent(), 
                variables = variables))
        }

        println(response.status.toString())
        println(isValidResponse(response))
        check(isValidResponse(response)){"Response was invalid. "  + response.status.toString()}
        return response
    }
    private fun isValidQuery(query : String) : Boolean{return true}
    private fun isValidResponse(response : HttpResponse) : Boolean{return (response.status.value==200)}

    inline suspend fun <reified T> makeRequest(variables: Map<String, String>, valueSetter: ValueSetter<T>){
        val requestType : RequestType? = getRequestTypeByClass(T::class)
        check(requestType!=null){"No valid requestType was found. Try giving request type manually"}
        makeRequest(variables = variables, valueSetter = valueSetter, requestType = requestType)
    }
    inline suspend fun <reified T> makeRequest(variables: Map<String, String>, valueSetter: ValueSetter<T>, requestType: RequestType){
        try {
            val response : HttpResponse = makeManualRequest(
                query = requestType.query,
                variables = standartVariables + variables
            )
            val jsonBody : T = response.body()
            valueSetter.callValueSet(jsonBody)
        }
        catch(e: IllegalStateException) {
            println("Api request failed with following error:")
            println(e.toString())
        }
    }
    suspend fun getRequestTypeByClass(expectedClass : KClass<*>) : RequestType?{
        for(requestType in RequestType.entries){
            if(requestType.associateClass == expectedClass){return requestType}
        }
        return null
    }
}

@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, String>? = null
)

class ValueSetter<T>(val callValueSet : (T) -> Unit)

enum class RequestType(val query : String, val associateClass : KClass<*>){
    MEDIA_LIST_COLLECTION(
        query = """
            query (${'$'}userName: String, ${'$'}type: MediaType) {
                MediaListCollection(userName: ${'$'}userName, type: ${'$'}type) {
                    lists {
                        entries {
                            media {
                                title {
                                    english
                                }
                            }
                        }
                    }
                }
            }""",
        associateClass = MediaListResponse::class
    )
}