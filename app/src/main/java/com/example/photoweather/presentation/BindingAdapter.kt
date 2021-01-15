package com.example.photoweather.presentation

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.photoweather.R
import com.example.photoweather.domain.model.Photo
import com.example.photoweather.presentation.gallery.GalleryAdapter

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

@BindingAdapter("bindGalleryAdapter")
fun bindListAdapter(recyclerView: RecyclerView, list: List<Photo>?) {
    list?.let {
        if (recyclerView.adapter != null) {
            if (recyclerView.adapter is GalleryAdapter)
                (recyclerView.adapter as GalleryAdapter).submitList(list)
        } else {
            val adapter = GalleryAdapter()
            adapter.submitList(list)
        }
    }
}