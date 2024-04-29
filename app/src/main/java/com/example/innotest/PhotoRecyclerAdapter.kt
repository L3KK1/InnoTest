package com.example.innotest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

interface PhotoClickListener {
    fun onPhotoClicked(url: String, id: Int)
}

class PhotoRecyclerAdapter(
    private val photoUrls: List<String>,
    private val photoIds: List<Int>, // Добавьте список идентификаторов фото
    private val clickListener: PhotoClickListener
) : RecyclerView.Adapter<PhotoRecyclerAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUrl = photoUrls[position]
        val photoId = photoIds[position] // Получаем идентификатор фото

        val radius = 50

        Glide.with(holder.itemView)
            .load(photoUrl)
            .transform(CenterCrop(), RoundedCorners(radius))
            .into(holder.photoImageView)

        holder.itemView.setOnClickListener {
            clickListener.onPhotoClicked(photoUrl, photoId) // Передаем и URL, и ID фото
        }
    }

    override fun getItemCount(): Int {
        return photoUrls.size
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoView)
    }
}