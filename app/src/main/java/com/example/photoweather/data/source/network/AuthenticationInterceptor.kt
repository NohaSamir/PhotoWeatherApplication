package com.example.photoweather.data.source.network

import com.example.photoweather.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

object AuthenticationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.request().let {
        val url = it.url.newBuilder()
            .addQueryParameter("appid", BuildConfig.API_KEY)
            .addQueryParameter("units", "metric")
            .build()

        val newRequest = it.newBuilder()
            .url(url)
            .build()

        chain.proceed(newRequest)
    }
}
