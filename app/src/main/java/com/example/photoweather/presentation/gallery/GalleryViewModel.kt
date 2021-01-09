package com.example.photoweather.presentation.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.photoweather.domain.repository.GalleryRepository
import com.example.photoweather.presentation.base.BaseAction
import com.example.photoweather.presentation.base.BaseViewModel
import com.example.photoweather.presentation.base.BaseViewState


class GalleryViewModel(private val repository: GalleryRepository) :
    BaseViewModel<GalleryViewModel.ViewState, GalleryViewModel.ViewAction>(
        ViewState()
    ) {

    val photos = repository.getPhotos()

    fun onNoPhotoAvailable() = sendAction(ViewAction.OnNoPhotoAvailable)
    fun onPhotoAvailable() = sendAction(ViewAction.OnPhotoAvailable)

    fun onAddButtonClick() = sendAction(ViewAction.OnAddPhotoClick)
    fun onGalleryButtonClick() = sendAction(ViewAction.OnGalleryButtonClick)
    fun onCameraButtonClick() = sendAction(ViewAction.OnCameraButtonClick)

    fun setPermissionResult(granted: Boolean) = sendAction(ViewAction.OnPermissionsResult(granted))
    fun onPermissionErrorDisplayed() = sendAction(ViewAction.OnPermissionErrorDisplayed)

    fun onNewPhotoSuccess(photoPath: String) =
        sendAction(ViewAction.OnAddNewPhotoSuccess(photoPath))

    fun onNewPhotoCanceled() = sendAction(ViewAction.OnAddNewPhotoCanceled)
    fun onNewPhotoError(error: String) = sendAction(ViewAction.OnAddNewPhotoError(error))

    fun onNewWindowOpened() = sendAction(ViewAction.OnNewWindowOpened)


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

        var isGalleryButtonVisible: Boolean = false,
        var isCameraButtonVisible: Boolean = false,

        var isPhotoListEmpty: Boolean = false,

        var isPermissionGranted: Boolean = false,
        var checkPermissions: Boolean = false,
        var permissionError: String = "",

        var openCamera: Boolean = false,
        var openGallery: Boolean = false,

        var photoPath: String = "",

        ) : BaseViewState

    sealed class ViewAction : BaseAction {
        object OnNoPhotoAvailable : ViewAction()
        object OnPhotoAvailable : ViewAction()

        object OnAddPhotoClick : ViewAction()
        object OnGalleryButtonClick : ViewAction()
        object OnCameraButtonClick : ViewAction()

        class OnPermissionsResult(val granted: Boolean) : ViewAction()
        object OnPermissionErrorDisplayed : ViewAction()

        object OnAddNewPhotoCanceled : ViewAction()
        class OnAddNewPhotoSuccess(val photoPath: String) : ViewAction()
        class OnAddNewPhotoError(val error: String) : ViewAction()

        object OnNewWindowOpened : ViewAction()
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
                    photoPath = viewAction.photoPath
                )

            is ViewAction.OnAddNewPhotoCanceled ->
                state.copy(openGallery = false, openCamera = false)

            is ViewAction.OnAddNewPhotoError ->
                state.copy(openGallery = false, openCamera = false)

            is ViewAction.OnNewWindowOpened ->
                state.copy(photoPath = "")

            ViewAction.OnPhotoAvailable -> state.copy(
                isLoading = false,
                isPhotoListEmpty = false
            )
        }
}
