package com.android.example.data.api_builder

import com.android.example.data.api.GithubAuthService
import com.android.example.data.api.GithubService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class ApiBuilder @Inject constructor(
    private val authenticationInterceptor: AuthenticationInterceptor
) {
    fun buildGithubService(
        baseUrl: String,
        loggingLevel: HttpLoggingInterceptor.Level
    ): GithubService {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = loggingLevel
            })
            .addInterceptor(authenticationInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(client)
            .build()
            .create(GithubService::class.java)
    }

    fun buildGithubAuthService(
        baseUrl: String
    ): GithubAuthService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubAuthService::class.java)
    }
}
