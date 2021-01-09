package com.example.photoweather.data.source.mapper

import com.example.photoweather.data.source.network.models.WeatherInfoResponse
import com.example.photoweather.domain.model.WeatherInfo

fun WeatherInfoResponse.toDomainModel(): WeatherInfo {
    return WeatherInfo(
        country = sys?.country ?: "Country",
        temp = main.temp.toInt(),
        description = weather[0].description
    )
}