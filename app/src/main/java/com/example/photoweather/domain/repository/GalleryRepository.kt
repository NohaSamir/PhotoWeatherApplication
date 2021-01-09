package com.example.photoweather.domain.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.example.photoweather.domain.model.ImageProvider
import com.example.photoweather.domain.model.Photo

interface GalleryRepository {
    fun getPhotos(): LiveData<List<Photo>>
    suspend fun saveNewPhoto(photoPath: String, bitmap: Bitmap, provider: ImageProvider): Photo?
    suspend fun deletePhoto(photo: Photo): Int
}