package com.example.photoweather.presentation.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.photoweather.databinding.ItemPhotoBinding
import com.example.photoweather.domain.model.Photo

class ListAdapter(private val listener: OnClickListener) :
    ListAdapter<Photo, GalleryViewHolder>(DataDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, listener)

    }
}

class GalleryViewHolder(private var binding: ItemPhotoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(photo: Photo, listener: OnClickListener) {
        binding.path = photo.photoPath
        binding.executePendingBindings()

        itemView.setOnClickListener {
            listener.onClick(photo)
        }
    }

    companion object {
        fun from(parent: ViewGroup): GalleryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemPhotoBinding.inflate(layoutInflater, parent, false)
            return GalleryViewHolder(binding)
        }
    }
}

class DataDiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}

interface OnClickListener {
    fun onClick(photo: Photo)
}


