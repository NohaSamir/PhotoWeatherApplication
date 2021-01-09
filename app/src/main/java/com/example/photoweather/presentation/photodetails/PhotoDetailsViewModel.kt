package com.example.photoweather.presentation.photodetails

import androidx.lifecycle.*
import com.example.photoweather.domain.model.Photo
import com.example.photoweather.domain.repository.GalleryRepository
import kotlinx.coroutines.launch

class PhotoDetailsViewModel(
    private val photo: Photo,
    private val galleryRepository: GalleryRepository
) : ViewModel() {

    private val _currentPhoto = MutableLiveData(photo)
    val currentPhoto: LiveData<Photo>
        get() = _currentPhoto


    private val _isDeleteSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isDeleteSuccess: LiveData<Boolean>
        get() = _isDeleteSuccess

    fun delete() {
        viewModelScope.launch {
            val photo = currentPhoto.value
            photo?.let {
                val result = galleryRepository.deletePhoto(it)
                _isDeleteSuccess.value = result > 0
            }
        }
    }

    /**
     * Simple ViewModel factory that provides the DummyData details and context to the ViewModel.
     */
    class DetailViewModelFactory(
        private val photo: Photo,
        private val galleryRepository: GalleryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PhotoDetailsViewModel::class.java)) {
                return PhotoDetailsViewModel(photo, galleryRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
