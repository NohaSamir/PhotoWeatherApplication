package com.example.photoweather.data.source.network.models

import com.google.gson.annotations.SerializedName

data class WeatherInfoResponse(
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val dt: Long,
    val sys: Sys?,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long
)

data class Main(
    val temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    @SerializedName("temp_min")
    val tempMin: Double,

    @SerializedName("temp_max")
    val tempMax: Double,

    val pressure: Long,
    val humidity: Long
)

data class Sys(
    val type: Long,
    val id: Long,
    val message: Double,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)
