package org.anibeaver.anibeaver.api

import android.content.Context
import com.liftric.kvault.KVault

actual fun tokenStore(service: String, account: String, platformContext: Any?): TokenStore {
    println("Got token in SECURE TOKEN STORE AAA $platformContext")

    println("Creating token store for service: $service, account: $account")
    println("Using KVault for secure storage")
    println("Platform context: $platformContext")

    val kv = KVault(platformContext as Context, service)
    val key = "$account.token"
    return object : TokenStore {
        override fun save(token: String) { kv.set(key, token) }
        override fun load(): String? = kv.string(key)
        override fun clear() { kv.deleteObject(key) }
    }
}