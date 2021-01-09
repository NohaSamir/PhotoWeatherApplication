package com.example.photoweather.presentation.photodetails

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.photoweather.R
import com.example.photoweather.data.source.repository.galleryRepository
import com.example.photoweather.databinding.FragmentPhotoDetailsBinding
import com.example.photoweather.utils.showToast
import java.io.File


class PhotoDetailsFragment : Fragment() {

    private val args: PhotoDetailsFragmentArgs by navArgs()

    private val viewModel: PhotoDetailsViewModel by viewModels {
        PhotoDetailsViewModel.DetailViewModelFactory(
            args.photo,
            galleryRepository = galleryRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentPhotoDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.shareImageButton.setOnClickListener {
            onShareClicked()
        }

        binding.deleteImageButton.setOnClickListener {
            viewModel.delete()
        }

        viewModel.isDeleteSuccess.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().popBackStack()
            } else {
                showToast(context, R.string.error_try_again)
            }
        }
        return binding.root
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun onShareClicked() {
        context?.let {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            val photoFile = File(args.photo.photoPath)

            val myPhotoFileUri = activity?.let { it1 ->
                FileProvider.getUriForFile(
                    it1, it.applicationContext.packageName
                            + ".provider", photoFile
                )
            }
            intent.putExtra(Intent.EXTRA_STREAM, myPhotoFileUri)
            val chooser = Intent.createChooser(intent, "Share File")

            val resInfoList: List<ResolveInfo> =
                it.applicationContext.packageManager.queryIntentActivities(
                    chooser,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                activity?.grantUriPermission(
                    packageName,
                    myPhotoFileUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            startActivity(chooser)
        }
    }
}
