package com.example.photoweather.presentation.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photoweather.domain.model.Photo
import com.example.photoweather.domain.repository.GalleryRepository
import com.example.photoweather.presentation.base.BaseAction
import com.example.photoweather.presentation.base.BaseViewModel
import com.example.photoweather.presentation.base.BaseViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class GalleryViewModel(private val repository: GalleryRepository) :
    BaseViewModel<GalleryViewModel.ViewState, GalleryViewModel.ViewAction>(
        ViewState()
    ) {


    init {
        loadData()
    }

    override fun loadData() {
        viewModelScope.launch {
            repository.getPhotos().collect {
                if (it.isNotEmpty())
                    sendAction(ViewAction.OnPhotoAvailable(it))
                else
                    sendAction(ViewAction.OnNoPhotoAvailable)
            }
        }
    }

    fun onAddButtonClick() = sendAction(ViewAction.OnAddPhotoClick)
    fun onGalleryButtonClick() = sendAction(ViewAction.OnGalleryButtonClick)
    fun onCameraButtonClick() = sendAction(ViewAction.OnCameraButtonClick)

    fun setPermissionResult(granted: Boolean) = sendAction(ViewAction.OnPermissionsResult(granted))
    fun onPermissionErrorDisplayed() = sendAction(ViewAction.OnPermissionErrorDisplayed)

    fun onNewPhotoSuccess(newPhotoPath: String) =
        sendAction(ViewAction.OnAddNewPhotoSuccess(newPhotoPath))

    fun onNewPhotoCanceled() = sendAction(ViewAction.OnAddNewPhotoCanceled)
    fun onNewPhotoError(error: String) = sendAction(ViewAction.OnAddNewPhotoError(error))

    fun onPhotoSelected(photo: Photo) = sendAction(ViewAction.OnPhotoSelected(photo))
    fun onNavigateToPhotoDetails() = sendAction(ViewAction.OnNavigateToNewWindow)


    /**
     * Factory for constructing ListViewModel with parameter
     */
    class Factory(private val repository: GalleryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GalleryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    data class ViewState(
        var isLoading: Boolean = true,
        var photos: List<Photo>? = null,

        var isGalleryButtonVisible: Boolean = false,
        var isCameraButtonVisible: Boolean = false,

        var isPhotoListEmpty: Boolean = false,

        var isPermissionGranted: Boolean = false,
        var checkPermissions: Boolean = false,
        var permissionError: String = "",

        var openCamera: Boolean = false,
        var openGallery: Boolean = false,

        var newPhotoPath: String = "",
        var selectedPhoto: Photo? = null

    ) : BaseViewState

    sealed class ViewAction : BaseAction {
        object OnNoPhotoAvailable : ViewAction()
        class OnPhotoAvailable(val photos: List<Photo>) : ViewAction()

        object OnAddPhotoClick : ViewAction()
        object OnGalleryButtonClick : ViewAction()
        object OnCameraButtonClick : ViewAction()

        class OnPermissionsResult(val granted: Boolean) : ViewAction()
        object OnPermissionErrorDisplayed : ViewAction()

        object OnAddNewPhotoCanceled : ViewAction()
        class OnAddNewPhotoSuccess(val photoPath: String) : ViewAction()
        class OnAddNewPhotoError(val error: String) : ViewAction()

        class OnPhotoSelected(val selectedPhoto: Photo) : ViewAction()
        object OnNavigateToNewWindow : ViewAction()
    }

    override fun onReduceState(viewAction: ViewAction): ViewState =
        when (viewAction) {
            is ViewAction.OnNoPhotoAvailable ->
                state.copy(
                    isLoading = false,
                    isPhotoListEmpty = true
                )

            is ViewAction.OnAddPhotoClick ->
                state.copy(
                    checkPermissions = true,
                )

            is ViewAction.OnPermissionsResult ->
                if (viewAction.granted) {
                    if (state.isCameraButtonVisible || state.isGalleryButtonVisible) {
                        state.copy(
                            isGalleryButtonVisible = false,
                            isCameraButtonVisible = false,
                            checkPermissions = false,
                            permissionError = ""
                        )
                    } else {
                        state.copy(
                            isGalleryButtonVisible = true,
                            isCameraButtonVisible = true,
                            checkPermissions = false,
                            permissionError = ""
                        )
                    }
                } else
                    state.copy(
                        permissionError = "Enable permissions to be able to add new photo",
                        checkPermissions = false
                    )


            is ViewAction.OnGalleryButtonClick ->
                state.copy(
                    openGallery = true,
                    isGalleryButtonVisible = false,
                    isCameraButtonVisible = false
                )

            is ViewAction.OnCameraButtonClick ->
                state.copy(
                    openCamera = true,
                    isGalleryButtonVisible = false,
                    isCameraButtonVisible = false
                )

            is ViewAction.OnPermissionErrorDisplayed ->
                state.copy(permissionError = "")

            is ViewAction.OnAddNewPhotoSuccess ->
                state.copy(
                    openGallery = false,
                    openCamera = false,
                    newPhotoPath = viewAction.photoPath
                )

            is ViewAction.OnAddNewPhotoCanceled ->
                state.copy(openGallery = false, openCamera = false)

            is ViewAction.OnAddNewPhotoError ->
                state.copy(openGallery = false, openCamera = false)

            is ViewAction.OnNavigateToNewWindow ->
                state.copy(newPhotoPath = "", selectedPhoto = null)

            is ViewAction.OnPhotoAvailable -> state.copy(
                isLoading = false,
                isPhotoListEmpty = false,
                photos = viewAction.photos
            )

            is ViewAction.OnPhotoSelected ->
                state.copy(selectedPhoto = viewAction.selectedPhoto)
        }
}
