package com.android.example.github.api

import com.android.example.data.repository.AccessTokenRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationInterceptor @Inject constructor(
    private val accessTokenRepository: AccessTokenRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            accessTokenRepository.load()?.let { accessToken ->
                addHeader("Authorization", accessToken.value)
            }
        }.build()

        return chain.proceed(request)
    }
}
