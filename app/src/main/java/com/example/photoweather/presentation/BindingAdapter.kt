package com.example.photoweather.presentation

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.photoweather.R

@BindingAdapter("loadImage")
fun loadImage(imageView: ImageView, path: String?) {

    path?.let {
        Glide.with(imageView.context)
            .apply {
                RequestOptions().error(R.drawable.ic_broken_image)
            }
            .load(path)
            .into(imageView)
    }
}