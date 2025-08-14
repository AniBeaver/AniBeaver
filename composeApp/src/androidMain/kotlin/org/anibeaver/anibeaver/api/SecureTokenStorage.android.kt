package org.anibeaver.anibeaver.api

actual fun tokenStore(service: String, account: String): TokenStore {
    // return empty object
    return object : TokenStore {
        override fun save(token: String) {
            // No-op for Android, as the token is not stored securely in this implementation.
            println("Saving token for $service:$account (no-op on Android)")
        }

        override fun load(): String? {
            // No-op for Android, as the token is not stored securely in this implementation.
            println("Loading token for $service:$account (no-op on Android)")
            return null
        }

        override fun clear() {
            // No-op for Android, as the token is not stored securely in this implementation.
            println("Clearing token for $service:$account (no-op on Android)")
        }
    }
}

actual fun initSecureStorage(platformContext: Any?) {
    // No-op for Android, as the initialization is handled by the Android application context.
    // This function can be used to set up any necessary configurations if needed in the future.
}