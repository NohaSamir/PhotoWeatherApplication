package com.example.photoweather.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    var id: Long? = null,
    var photoPath: String
) : Parcelable