package com.example.photoweather.data.source.network

import com.example.photoweather.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val API_VERSION = "2.5"
private const val BASE_URL = "https://api.openweathermap.org/data/$API_VERSION/"

val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}


private val client by lazy {
    val logging = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG)
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

    OkHttpClient
        .Builder()
        .addInterceptor(AuthenticationInterceptor)
        .addInterceptor(logging)
        .build()

}