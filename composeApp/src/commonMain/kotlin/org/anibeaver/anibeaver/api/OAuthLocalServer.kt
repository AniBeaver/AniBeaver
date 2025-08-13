package org.anibeaver.anibeaver.api

import io.ktor.http.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class OAuthLocalServer(
    private val authCodeStorage: AuthCodeStorage,
    private val port: Int = 8080,
) {
    @kotlin.concurrent.Volatile
    private var expectedNonce: String? = null
    private val origin = "http://127.0.0.1:$port"

    @OptIn(ExperimentalEncodingApi::class)
    private fun newNonce(): String {
        val bytes = ByteArray(32) { (0..255).random().toByte() }
        return Base64.UrlSafe.encode(bytes).replace("=", "")
    }

    @Serializable private data class FinishPayload(val token: String, val nonce: String)

    private val server = embeddedServer(CIO, port = port, host = "127.0.0.1") {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }

        routing {
            // Step 1: set nonce cookie and bounce to AniList authorize
            get("/start") {
                val nonce = newNonce().also { expectedNonce = it }
                call.response.cookies.append(
                    Cookie(
                        name = "oauth_nonce",
                        value = nonce,
                        path = "/",
                        secure = false, // loopback HTTP; ok
                        httpOnly = false, // must be readable by the callback page JS
                        extensions = (
                                mapOf("SameSite" to "Lax")
                        )
                    )
                )
                // You open this in the browser; AniList will redirect back to /callback#access_token=...
                call.respondText(
                    """
                    <!doctype html><meta charset="utf-8">
                    <script>
                      // Replace with your real client_id & redirect_uri (must be this callback)
                      const auth = new URL("https://anilist.co/api/v2/oauth/authorize");
                      auth.searchParams.set("client_id", "29342");
                      auth.searchParams.set("response_type", "token");
                      location.replace(auth.toString());
                    </script>
                    """.trimIndent(), ContentType.Text.Html
                )
            }

            // Step 2: tiny page that reads the fragment and double-submits the nonce
            get("/callback") {
                call.respondText(
                    """
                    <!doctype html><meta charset="utf-8">
                    <script>
                      const p = new URLSearchParams(location.hash.slice(1));
                      const token = p.get("access_token");
                      // read cookie (same-origin)
                      const m = document.cookie.match(/(?:^|;\s*)oauth_nonce=([^;]+)/);
                      const nonce = m && m[1];
                      if (token && nonce) {
                        fetch("$origin/oauth/finish", {
                          method:"POST",
                          credentials: "same-origin",
                          headers: {"Content-Type":"application/json"},
                          body: JSON.stringify({ token, nonce })
                        }).then(()=> document.body.textContent="Signed in. You can close this tab.");
                      } else {
                        document.body.textContent = "Missing token or nonce.";
                      }
                      // scrub the fragment from the bar
                      history.replaceState(null, "", location.pathname);
                    </script>
                    """.trimIndent(), ContentType.Text.Html
                )
            }

            // Step 3: finish – verify origin, host, cookie, and body nonce all match
            post("/oauth/finish") {
                println("OAuth finish request received")

                // Content-Type must be JSON
                if (call.request.contentType().withoutParameters() != ContentType.Application.Json) {
                    return@post call.respond(HttpStatusCode.UnsupportedMediaType)
                }
                // Origin check (when browsers send it; some may omit)
                call.request.headers["Origin"]?.let { o ->
                    if (o != origin) return@post call.respond(HttpStatusCode.Forbidden)
                }
                // Host header pinning
                if (call.request.headers["Host"] != "127.0.0.1:$port") {
                    return@post call.respond(HttpStatusCode.Forbidden)
                }
                val cookieNonce = call.request.cookies["oauth_nonce"]
                val body = runCatching { call.receive<FinishPayload>() }.getOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val expected = expectedNonce
                if (cookieNonce == null || expected == null || body.nonce != cookieNonce || body.nonce != expected) {
                    println("Nonce mismatch: cookie=$cookieNonce, body=${body.nonce}, expected=$expected")
                    return@post call.respond(HttpStatusCode.Forbidden)
                }

                // Success: persist token, then nuke the nonce & cookie
                authCodeStorage.accessToken = body.token
                expectedNonce = null
                call.response.cookies.append(
                    Cookie(
                        name = "oauth_nonce",
                        value = "",
                        path = "/",
                        maxAge = 0
                    )
                )
                call.respond(HttpStatusCode.NoContent)

                println("OAuth success: access token set.")

                // Optional: stop immediately after success
                // stop()
            }

            // (Optional) root for debugging
            get("/") { call.respondText("OK", ContentType.Text.Plain) }
        }
    }

    fun start() = server.start(wait = false)
    fun stop() = server.stop(1000, 10_000)
}
