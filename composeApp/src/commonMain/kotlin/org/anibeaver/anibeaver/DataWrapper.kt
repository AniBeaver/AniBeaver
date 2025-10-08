package org.anibeaver.anibeaver

import org.anibeaver.anibeaver.api.ApiHandler
import org.anibeaver.anibeaver.api.TokenStore

class DataWrapper(
    val activityKiller: () -> Unit = {},
    val apiHandler : ApiHandler,
    val tokenStore: TokenStore
)