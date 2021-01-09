package com.example.photoweather.data.source.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.photoweather.data.source.database.models.PhotoDB

@Dao
interface GalleryDao {

    @Query("Select * from PhotoDB ORDER BY id DESC")
    fun getPhotos(): LiveData<List<PhotoDB>>

    @Query("Select * from PhotoDB where id = :id ")
    suspend fun getPhoto(id: String): PhotoDB

    @Delete
    suspend fun deletePhoto(photo: PhotoDB): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photo: PhotoDB): Long
}