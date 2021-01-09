package com.example.photoweather.utils

import android.Manifest

object PermissionConstants {
    const val REQUEST_CODE_PERMISSION = 100
    val PERMISSIONS =
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
}


object FileConstants {
    const val PHOTOS_FILE_PATH = "photo_weather"
}