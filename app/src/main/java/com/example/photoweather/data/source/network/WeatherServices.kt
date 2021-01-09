package com.example.photoweather.data.source.network

import com.example.photoweather.data.source.network.models.WeatherInfoResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

val apiServices: WeatherServices by lazy {
    retrofit.create(WeatherServices::class.java)
}

interface WeatherServices {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): WeatherInfoResponse
}