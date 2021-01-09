package com.example.photoweather.presentation.addphoto

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.photoweather.MainActivity
import com.example.photoweather.data.source.repository.galleryRepository
import com.example.photoweather.data.source.repository.weatherRepository
import com.example.photoweather.databinding.FragmentNewPhotoBinding
import com.example.photoweather.domain.model.ImageProvider
import com.example.photoweather.domain.model.Photo
import com.example.photoweather.utils.LocationManager
import com.example.photoweather.utils.LocationManagerInteraction
import com.example.photoweather.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NewPhotoFragment : Fragment(), LocationManagerInteraction {

    private lateinit var binding: FragmentNewPhotoBinding

    private val args: NewPhotoFragmentArgs by navArgs()

    private val imageProvider: ImageProvider by lazy {
        if (args.imageProvider == ImageProvider.GALLERY.id)
            ImageProvider.GALLERY
        else
            ImageProvider.CAMERA
    }

    private val locationManager: LocationManager by lazy {
        LocationManager(
            activity as MainActivity,
            this
        )
    }

    private val viewModel: NewPhotoViewModel by viewModels {

        NewPhotoViewModel.Factory(
            args.photoPath,
            imageProvider,
            weatherRepository,
            galleryRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPhotoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        lifecycleScope.launch {
            withContext(Dispatchers.IO)
            {
                locationManager.startLocationUpdates()
            }
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner, {
            if (it.takeScreenShot) {
                val bitmap = screenShot(binding.imageContainer)
                viewModel.onScreenShotFinish(bitmap)
            }

            if (it.error != null) {
                showToast(context, it.error)
                viewModel.onErrorMsgDisplayed()
            }

            val newPhoto = it.newPhoto
            if (it.navigateToPhotoDetails && newPhoto != null) {
                viewModel.onNavigateSuccess()
                navigateToPhotoDetails(newPhoto)
            }
        })

        setUpOnWeatherViewDrag()
        return binding.root
    }

    private fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun navigateToPhotoDetails(photo: Photo) {
        val action = NewPhotoFragmentDirections.actionNewPhotoFragmentToPhotoDetailsFragment(photo)
        findNavController().navigate(action)
    }

    override fun onLocationRetrieved(location: Location?, address: String) {
        if (location != null) {
            viewModel.setLocation(location, address)
            locationManager.stopLocationUpdates()
        }
    }

    override fun onStop() {
        super.onStop()
        locationManager.stopLocationUpdates()
    }

    private var dY: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpOnWeatherViewDrag() {
        binding.weatherLayout.container.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    val newY = event.rawY + dY
                    val height =
                        binding.imageContainer.height - binding.weatherLayout.container.height
                    if (newY > 0 && newY < height)
                        binding.weatherLayout.container.y = event.rawY + dY
                }
            }
            return@setOnTouchListener true
        }
    }
}