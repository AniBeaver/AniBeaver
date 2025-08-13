package org.anibeaver.anibeaver.api

import io.ktor.http.ContentType
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*

class OAuthLocalServer(authCodeStorage: AuthCodeStorage) {
    private val server = embeddedServer(CIO, port = 8080) {
        routing {
            get("/") {
                call.respondText("Authorization successful! You can close this window now.", ContentType.Text.Html)
                val code = call.request.queryParameters["code"]
                if (code != null) {
                    authCodeStorage.authCode = code
                } else {
                    call.respondText("No authorization code received.", ContentType.Text.Html)
                }
            }
        }
    }

    fun start() {
        server.start(wait = false)
    }

    fun stop() {
        server.stop(1000, 10000)
    }
}

