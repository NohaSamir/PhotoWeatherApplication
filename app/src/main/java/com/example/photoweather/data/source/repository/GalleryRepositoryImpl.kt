package com.example.photoweather.data.source.repository

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.photoweather.MyApp
import com.example.photoweather.data.source.database.AppDatabase
import com.example.photoweather.data.source.database.GalleryDao
import com.example.photoweather.data.source.mapper.toDatabaseModel
import com.example.photoweather.data.source.mapper.toDomainModel
import com.example.photoweather.domain.model.ImageProvider
import com.example.photoweather.domain.model.Photo
import com.example.photoweather.domain.repository.GalleryRepository
import com.example.photoweather.utils.FileOperations
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


val galleryRepository by lazy {
    GalleryRepositoryImpl(AppDatabase.getInstance(MyApp.application).galleryDao)
}

class GalleryRepositoryImpl(
    private val galleryDao: GalleryDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : GalleryRepository {

    override fun getPhotos(): LiveData<List<Photo>> =
        Transformations.map(galleryDao.getPhotos()) { list ->
            list.map { photoDB ->
                photoDB.toDomainModel()
            }
        }


    override suspend fun saveNewPhoto(
        photoPath: String,
        bitmap: Bitmap,
        provider: ImageProvider
    ): Photo? {
        return withContext(dispatcher)
        {
            val newPath: String? = if (provider == ImageProvider.GALLERY) {
                FileOperations.insertBitmapInAppFolder(bitmap)
            } else {
                FileOperations.replaceOriginalBitmapWithGeneratedBitmap(photoPath, bitmap)
            }
            if (newPath != null && newPath.isNotBlank()) {
                val photo = Photo(photoPath = newPath)
                val id = insertNewPhotoInDatabase(photo)
                photo.id = id
                return@withContext photo
            }
            return@withContext null
        }
    }

    override suspend fun deletePhoto(photo: Photo): Int {
        return withContext(dispatcher)
        {
            FileOperations.delete(photo.photoPath)
            return@withContext galleryDao.deletePhoto(photo.toDatabaseModel())
        }
    }

    private fun insertNewPhotoInDatabase(photo: Photo): Long {
        return galleryDao.insert(photo.toDatabaseModel())
    }
}