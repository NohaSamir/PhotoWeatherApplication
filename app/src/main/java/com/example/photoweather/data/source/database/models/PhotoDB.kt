package com.example.photoweather.data.source.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotoDB(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var photoPath: String
)