package com.example.innotest

import android.graphics.Rect
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface HeaderClickListener {
    fun onHeaderClicked(header: String)
}

class HeaderAdapter(private val headers: List<String>) : RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(headers[position])
        val header = headers[position]


    }

    override fun getItemCount() = headers.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(header: String) {
            itemView.findViewById<TextView>(R.id.textViewHeader).text = header

        }
    }
}

class SpacesItemDecoration(private val space : Int) : RecyclerView.ItemDecoration(){
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = space
        outRect.right = space

    }
}