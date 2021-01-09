package com.example.photoweather.data.source.repository

import android.location.Location
import com.example.photoweather.data.source.mapper.toDomainModel
import com.example.photoweather.data.source.network.WeatherServices
import com.example.photoweather.data.source.network.apiServices
import com.example.photoweather.domain.model.WeatherInfo
import com.example.photoweather.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val weatherRepository by lazy {
    WeatherRepositoryImpl(webServices = apiServices)
}

class WeatherRepositoryImpl(
    private val webServices: WeatherServices,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherRepository {

    override suspend fun getWeatherData(location: Location): WeatherInfo {
        return withContext(dispatcher)
        {
            val response =
                webServices.getCurrentWeather(location.latitude, location.longitude)

            return@withContext response.toDomainModel()
        }
    }
}