package com.example.innotest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoRecyclerAdapter(private val photoUrls: List<String>) : RecyclerView.Adapter<PhotoRecyclerAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUrl = photoUrls[position]

        // Используем Glide для загрузки фотографии по URL в ImageView
        Glide.with(holder.itemView)
            .load(photoUrl)
            .override(300, 300) // Установите здесь фиксированные размеры для изображения
            .into(holder.photoImageView)
    }

    override fun getItemCount(): Int {
        return photoUrls.size
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoView)
    }
}
