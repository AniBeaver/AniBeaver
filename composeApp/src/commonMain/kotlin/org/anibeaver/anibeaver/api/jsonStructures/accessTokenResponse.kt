package org.anibeaver.anibeaver.api.jsonStructures

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenRepsonse(
    val access_token: String
)