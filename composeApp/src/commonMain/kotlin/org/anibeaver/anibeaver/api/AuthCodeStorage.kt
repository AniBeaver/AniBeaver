package org.anibeaver.anibeaver.api

class AuthCodeStorage{
    var authCode: String? = null
        set(value){
            authCodeListener(value)
            field = value
        }

    var accessToken: String? = null
        set(value){
            accessTokenListener(value)
            field = value
        }

    var authCodeListener: (String?) -> Unit = {}
    var accessTokenListener: (String?) -> Unit = {}
}