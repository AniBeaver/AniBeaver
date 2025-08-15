package org.anibeaver.anibeaver.api

import org.netbeans.api.keyring.Keyring

actual fun tokenStore(service: String, account: String, platformContext: Any?): TokenStore {
    val id = "$service:$account"
    return object : TokenStore {
        override fun save(token: String) {
            println("Saving token for $id")
            Keyring.save(id, token.toCharArray(), "AniList access token")
        }
        override fun load(): String? = Keyring.read(id)?.let { String(it) }
        override fun clear() { Keyring.delete(id) }
    }
}