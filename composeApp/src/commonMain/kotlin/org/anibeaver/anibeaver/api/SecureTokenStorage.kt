package org.anibeaver.anibeaver.api

interface TokenStore {
    fun save(token: String)
    fun load(): String?
    fun clear()
}

/** Create a platform TokenStore. */
expect fun tokenStore(service: String, account: String): TokenStore

/** Optional platform init. Required on Android to pass Application Context. */
expect fun initSecureStorage(platformContext: Any? = null)