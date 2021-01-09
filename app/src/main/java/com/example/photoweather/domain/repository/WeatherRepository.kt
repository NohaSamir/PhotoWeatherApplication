package com.example.photoweather.domain.repository

import android.location.Location
import com.example.photoweather.domain.model.WeatherInfo

interface WeatherRepository {

    suspend fun getWeatherData(location: Location): WeatherInfo
}