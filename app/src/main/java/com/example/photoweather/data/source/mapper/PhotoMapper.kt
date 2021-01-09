package com.example.photoweather.data.source.mapper

import com.example.photoweather.data.source.database.models.PhotoDB
import com.example.photoweather.domain.model.Photo

fun PhotoDB.toDomainModel(): Photo {
    return Photo(id, photoPath)
}

fun Photo.toDatabaseModel(): PhotoDB {
    return PhotoDB(id, photoPath)
}