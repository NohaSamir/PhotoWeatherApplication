package com.example.photoweather.utils

import android.location.Location

interface LocationManagerInteraction {
    fun onLocationRetrieved(location: Location?, address: String)
}