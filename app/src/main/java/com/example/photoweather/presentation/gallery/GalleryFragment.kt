package com.example.photoweather.presentation.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.photoweather.R
import com.example.photoweather.data.source.repository.galleryRepository
import com.example.photoweather.databinding.FragmentGalleryBinding
import com.example.photoweather.domain.model.ImageProvider
import com.example.photoweather.domain.model.Photo
import com.example.photoweather.utils.FileOperations
import com.example.photoweather.utils.PermissionConstants.PERMISSIONS
import com.example.photoweather.utils.PermissionConstants.REQUEST_CODE_PERMISSION
import com.example.photoweather.utils.showToast
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import pub.devrel.easypermissions.EasyPermissions


/**
 * ToDo:
 *  1- testing
 *  6- add use cases
 */
@ExperimentalCoroutinesApi
class GalleryFragment : Fragment(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private lateinit var adapter: GalleryAdapter
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var imageProvider: ImageProvider
    private val fragment :GalleryFragment by lazy { this }

    /**
     * Lazily initialize our [GalleryViewModel].
     */
    private val viewModel: GalleryViewModel by viewModels {
        GalleryViewModel.Factory(galleryRepository)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the ListViewModel
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        /*Initialize adapter and handle on item click */
        adapter = GalleryAdapter(object : OnClickListener {
            override fun onClick(photo: Photo) {
                viewModel.onPhotoSelected(photo)
            }
        })
        binding.recycler.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collect { viewState ->

                if (viewState.checkPermissions)
                    checkAllPermission()

                if (viewState.permissionError.isNotBlank()) {
                    showToast(context, viewState.permissionError)
                    viewModel.onPermissionErrorDisplayed()
                }

                if (viewState.openCamera) {
                    imageProvider = ImageProvider.CAMERA
                    context?.let {
                        ImagePicker.with(fragment)
                            .cameraOnly()
                            .saveDir(FileOperations.getAppFolder(it))
                            .start()
                    }
                }

                if (viewState.openGallery) {
                    imageProvider = ImageProvider.GALLERY
                    context?.let {
                        ImagePicker.with(fragment)
                            .galleryOnly()
                            .start()
                    }
                }

                if (viewState.newPhotoPath.isNotBlank()) {
                    openPhotoDetails(viewState.newPhotoPath)
                    viewModel.onNavigateToPhotoDetails()
                }

                viewState.selectedPhoto?.let {
                    openPhotoDetails(it)
                    viewModel.onNavigateToPhotoDetails()
                }
            }
        }

        return binding.root
    }

    private fun openPhotoDetails(photoPath: String) {
        findNavController().navigate(
            GalleryFragmentDirections.actionGalleryFragmentToNewPhotoFragment(
                photoPath,
                imageProvider.id
            )
        )
    }

    private fun checkAllPermission() {
        val context = context
        if (context != null) {
            if (EasyPermissions.hasPermissions(context, *PERMISSIONS)) {
                // Already have permission, do the thing
                viewModel.setPermissionResult(true)
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(
                    this, getString(R.string.camera_and_location_rationale),
                    REQUEST_CODE_PERMISSION, *PERMISSIONS
                )
            }
        }
    }

    private fun openPhotoDetails(photo: Photo)
    {
        findNavController().navigate(
            GalleryFragmentDirections.actionGalleryFragmentToPhotoDetailsFragment(
                photo
            )
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (perms.size == PERMISSIONS.size) {
            viewModel.setPermissionResult(true)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        EasyPermissions.requestPermissions(
            this, getString(R.string.camera_and_location_rationale),
            REQUEST_CODE_PERMISSION, *PERMISSIONS
        )
    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data

                Log.d("PhotoPath", fileUri?.path ?: "Error")
                viewModel.onNewPhotoSuccess(fileUri?.path ?: "")

            }
            ImagePicker.RESULT_ERROR -> {
                showToast(context, ImagePicker.getError(data))
                viewModel.onNewPhotoError(ImagePicker.getError(data))
            }
            else -> viewModel.onNewPhotoCanceled()
        }

    }


}

