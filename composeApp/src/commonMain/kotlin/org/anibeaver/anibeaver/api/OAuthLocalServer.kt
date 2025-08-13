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
                    <!doctype html>
                    <html lang="en">
                    <head>
                      <meta charset="utf-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1">
                      <meta name="color-scheme" content="dark light">
                      <title>Signing you in…</title>
                      <style>
                        :root{
                          --bg:#0b0f14;--fg:#e6e9ef;--muted:#91a0b2;--accent:#7c9cff;
                          --success:#2ecc71;--error:#ff5c5c;--card:rgba(255,255,255,.06);--border:rgba(255,255,255,.12)
                        }
                        @media (prefers-color-scheme: light){
                          :root{
                            --bg:#f6f7fb;--fg:#0c1220;--muted:#5b667a;--accent:#3b6cff;
                            --success:#1aa160;--error:#d23c3c;--card:rgba(0,0,0,.04);--border:rgba(0,0,0,.08)
                          }
                        }
                        *{box-sizing:border-box}
                        html,body{height:100%}
                        body{
                          margin:0;background:
                            radial-gradient(1200px 800px at 20% 10%,var(--card),transparent),
                            radial-gradient(1000px 700px at 80% 90%,var(--card),transparent),
                            var(--bg);
                          color:var(--fg);
                          font-family:ui-sans-serif,system-ui,-apple-system,Segoe UI,Roboto,Ubuntu,Cantarell,Noto Sans,Arial,"Apple Color Emoji","Segoe UI Emoji";
                          display:grid;place-items:center
                        }
                        .card{
                          width:min(560px,92vw);padding:24px;padding-top:18px;border-radius:18px;
                          background:var(--card);border:1px solid var(--border);
                          backdrop-filter:blur(6px);box-shadow:0 10px 30px rgba(0,0,0,.15)
                        }
                        h1{margin:0 0 8px;font-size:20px;letter-spacing:.2px}
                        p{margin:6px 0 0;color:var(--muted)}
                        .row{display:flex;align-items:center;gap:12px;margin-top:8px}
                        .spinner{
                          width:20px;height:20px;border-radius:50%;
                          border:2px solid var(--border);border-top-color:var(--accent);
                          animation:spin 900ms linear infinite
                        }
                        @keyframes spin{to{transform:rotate(360deg)}}
                        .check,.xmark{
                          width:24px;height:24px;border-radius:50%;display:grid;place-items:center;color:#fff;font-weight:700
                        }
                        .check{background:var(--success)} .xmark{background:var(--error)}
                        .btn{
                          margin-top:14px;display:inline-flex;align-items:center;gap:8px;
                          padding:10px 14px;border-radius:12px;border:1px solid var(--border);
                          background:transparent;color:var(--fg);cursor:pointer
                        }
                        .btn:hover{border-color:var(--accent)}
                        .small{font-size:12px;color:var(--muted);margin-top:10px}
                        .hidden{display:none}
                        .debug{margin-top:12px;word-break:break-all;font-size:12px}
                      </style>
                    </head>
                    <body>
                      <main class="card" role="status" aria-live="polite">
                        <div id="loading" class="row">
                          <div class="spinner" aria-hidden="true"></div>
                          <div>
                            <h1>Finishing sign-in…</h1>
                            <p>Please keep this tab open for a moment.</p>
                          </div>
                        </div>
            
                        <div id="success" class="row hidden">
                          <div class="check" aria-hidden="true">✓</div>
                          <div>
                            <h1>Signed in to AniBeaver with AniList.</h1>
                            <p>You can now close this tab.</p>
                          </div>
                        </div>
            
                        <div id="error" class="row hidden">
                          <div class="xmark" aria-hidden="true">!</div>
                          <div>
                            <h1>Couldn’t complete sign-in</h1>
                            <p id="errmsg">Missing token or nonce.</p>
                            <div class="debug" id="debug"></div>
                            <button class="btn" id="retry">Try again</button>
                          </div>
                        </div>
                      </main>
            
                      <script>
                        (function(){
                          const show = id => {
                            for (const el of document.querySelectorAll('#loading,#success,#error')) {
                              el.classList.toggle('hidden', el.id !== id);
                            }
                          };
            
                          const params = new URLSearchParams(location.hash.slice(1));
                          const token = params.get('access_token') || '';
                          const m = document.cookie.match(/(?:^|;\s*)oauth_nonce=([^;]+)/);
                          const nonce = m && m[1];
            
                          if (!token || !nonce) {
                            document.getElementById('errmsg').textContent = !token ? 'Missing access token.' : 'Missing nonce cookie.';
                            document.getElementById('debug').textContent = location.hash ? 'Fragment present.' : 'No fragment in URL.';
                            show('error');
                            history.replaceState(null, '', location.pathname);
                            document.getElementById('retry').onclick = () => location.reload();
                            return;
                          }
            
                          show('loading');
                          fetch("$origin/oauth/finish", {
                            method: "POST",
                            credentials: "same-origin",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({ token, nonce })
                          })
                          .then(resp => {
                            if (resp.ok) { show('success'); }
                            else { return resp.text().then(t => { throw new Error(t || ('HTTP ' + resp.status)); }); }
                          })
                          .catch(err => {
                            document.getElementById('errmsg').textContent = 'Local app rejected the sign-in.';
                            document.getElementById('debug').textContent = String(err);
                            show('error');
                          })
                          .finally(() => {
                            history.replaceState(null, '', location.pathname); // scrub fragment
                          });
                        })();
                      </script>
                    </body>
                    </html>
                    """.trimIndent(),
                    ContentType.Text.Html
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
