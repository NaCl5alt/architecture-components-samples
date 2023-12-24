package com.android.example.github.repository

import com.android.example.model.AccessToken
import com.chibatching.kotpref.KotprefModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessTokenRepository @Inject constructor() {

    private object Pref : KotprefModel() {
        var accessTokenValue by nullableStringPref()
    }

    fun save(token: com.android.example.model.AccessToken) {
        Pref.accessTokenValue = token.value
    }

    fun load(): com.android.example.model.AccessToken? {
        return Pref.accessTokenValue?.let {
            com.android.example.model.AccessToken(it)
        }
    }

    fun clear() {
        Pref.accessTokenValue = null
    }
}
