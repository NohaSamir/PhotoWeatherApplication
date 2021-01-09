package com.example.photoweather.presentation.addphoto

import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photoweather.R
import com.example.photoweather.domain.model.ImageProvider
import com.example.photoweather.domain.model.Photo
import com.example.photoweather.domain.model.WeatherInfo
import com.example.photoweather.domain.repository.GalleryRepository
import com.example.photoweather.domain.repository.WeatherRepository
import com.example.photoweather.presentation.base.BaseAction
import com.example.photoweather.presentation.base.BaseViewModel
import com.example.photoweather.presentation.base.BaseViewState
import kotlinx.coroutines.launch

class NewPhotoViewModel(
    private val photoPath: String,
    private val imageProvider: ImageProvider,
    private val repository: WeatherRepository,
    private val galleryRepo: GalleryRepository
) :
    BaseViewModel<NewPhotoViewModel.ViewState, NewPhotoViewModel.ViewAction>(
        ViewState()
    ) {

    init {
        sendAction(ViewAction.PhotoPathReceived(photoPath))
    }

    fun setLocation(location: Location?, address: String) =
        if (location != null) sendAction(ViewAction.LocationLoadingSuccess(location, address))
        else sendAction(ViewAction.LocationLoadingFail)

    fun onScreenShotFinish(bitmap: Bitmap?) {
        if (bitmap != null)
            sendAction(ViewAction.OnScreenShotSuccess(bitmap))
        else
            sendAction(ViewAction.OnScreenShotFail(R.string.error_try_again))
    }

    fun onSaveClick() = sendAction(ViewAction.OnSaveClick)
    fun onNavigateSuccess() = sendAction(ViewAction.OnNavigateSuccess)
    fun onErrorMsgDisplayed() = sendAction(ViewAction.OnErrorMsgDisplayed)

    private fun getWeatherInfo(location: Location) {
        viewModelScope.launch {
            val action = try {
                val result = repository.getWeatherData(location)
                ViewAction.WeatherInfoLoadingSuccess(result)
            } catch (e: Exception) {
                ViewAction.WeatherInfoLoadingFail
            }
            sendAction(action)
        }
    }

    private fun saveNewPhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            val newPhoto = galleryRepo.saveNewPhoto(photoPath, bitmap, imageProvider)
            if (newPhoto == null) {
                sendAction(ViewAction.SaveImageFail(R.string.error_try_again))
            } else
                sendAction(ViewAction.SaveImageSuccess(newPhoto))
        }
    }

    data class ViewState(
        var isLoading: Boolean = true,
        var weatherInfo: WeatherInfo? = null,
        var photoPath: String = "",
        var location: Location? = null,
        var address: String = "",
        var takeScreenShot: Boolean = false,
        var bitmap: Bitmap? = null,
        var error: Int? = null,
        var newPhoto: Photo? = null,
        var navigateToPhotoDetails: Boolean = false

    ) : BaseViewState

    sealed class ViewAction : BaseAction {
        class PhotoPathReceived(val photoPath: String) : ViewAction()

        class LocationLoadingSuccess(val location: Location, val address: String) :
            ViewAction()

        object LocationLoadingFail : ViewAction()

        class WeatherInfoLoadingSuccess(val weatherInfo: WeatherInfo) : ViewAction()
        object WeatherInfoLoadingFail : ViewAction()

        object OnSaveClick : ViewAction()

        class OnScreenShotSuccess(val bitmap: Bitmap) : ViewAction()
        class OnScreenShotFail(val error: Int) : ViewAction()

        class SaveImageSuccess(val photo: Photo) : ViewAction()
        class SaveImageFail(val error: Int) : ViewAction()

        object OnNavigateSuccess : ViewAction()
        object OnErrorMsgDisplayed : ViewAction()
    }


    class Factory(
        private val photoPath: String,
        private val imageProvider: ImageProvider,
        private val repository: WeatherRepository,
        private val galleryRepo: GalleryRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewPhotoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewPhotoViewModel(photoPath, imageProvider, repository, galleryRepo) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    override fun onReduceState(viewAction: ViewAction): ViewState = when (viewAction) {

        is ViewAction.WeatherInfoLoadingSuccess ->
            state.copy(isLoading = false, weatherInfo = viewAction.weatherInfo)

        is ViewAction.WeatherInfoLoadingFail ->
            state.copy(isLoading = false, weatherInfo = WeatherInfo())

        is ViewAction.PhotoPathReceived ->
            state.copy(photoPath = viewAction.photoPath)

        is ViewAction.LocationLoadingSuccess -> {
            getWeatherInfo(viewAction.location)
            state.copy(
                isLoading = true,
                location = viewAction.location,
                address = viewAction.address
            )
        }

        ViewAction.LocationLoadingFail -> state.copy(
            isLoading = false,
            error = R.string.error_try_again
        )

        ViewAction.OnSaveClick ->
            state.copy(isLoading = true, takeScreenShot = true)

        is ViewAction.OnScreenShotSuccess -> {
            saveNewPhoto(viewAction.bitmap)
            state.copy(isLoading = true, takeScreenShot = false)
        }

        is ViewAction.OnScreenShotFail -> state.copy(
            isLoading = false,
            takeScreenShot = false,
            error = viewAction.error
        )

        is ViewAction.SaveImageSuccess ->
            state.copy(
                isLoading = false,
                newPhoto = viewAction.photo,
                navigateToPhotoDetails = true
            )

        is ViewAction.SaveImageFail ->
            state.copy(isLoading = false, error = viewAction.error)

        ViewAction.OnNavigateSuccess ->
            state.copy(navigateToPhotoDetails = false)

        ViewAction.OnErrorMsgDisplayed -> state.copy(error = null)

    }
}