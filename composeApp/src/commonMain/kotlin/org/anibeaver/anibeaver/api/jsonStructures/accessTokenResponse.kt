package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
    val access_token: String
)