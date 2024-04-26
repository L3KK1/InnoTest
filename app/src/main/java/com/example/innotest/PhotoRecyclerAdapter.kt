package com.example.innotest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class PhotoRecyclerAdapter(private val photoUrls: List<String>) : RecyclerView.Adapter<PhotoRecyclerAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUrl = photoUrls[position]
        val requestOptions = RequestOptions().transform(RoundedCorners(100))
        Glide.with(holder.itemView)
            .load(photoUrl)
            .transform(RoundedCorners(100))
            .centerCrop()
            .into(holder.photoImageView)
    }


    override fun getItemCount(): Int {
        return photoUrls.size
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photoView)
    }
}
