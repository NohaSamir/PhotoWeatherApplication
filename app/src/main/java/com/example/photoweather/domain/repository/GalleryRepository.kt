package com.example.photoweather.domain.repository

import android.graphics.Bitmap
import com.example.photoweather.domain.model.ImageProvider
import com.example.photoweather.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getPhotos(): Flow<List<Photo>>
    suspend fun saveNewPhoto(photoPath: String, bitmap: Bitmap, provider: ImageProvider): Photo?
    suspend fun deletePhoto(photo: Photo): Int
}