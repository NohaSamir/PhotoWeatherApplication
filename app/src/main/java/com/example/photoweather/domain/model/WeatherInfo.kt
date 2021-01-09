package com.example.photoweather.domain.model

import com.example.photoweather.utils.getCurrentDate

data class WeatherInfo(
    var temp: Int = 0,
    var description: String = "",
    var time:String = getCurrentDate(),
    var country: String = "",
)

