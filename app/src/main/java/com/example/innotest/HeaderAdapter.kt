package com.example.innotest

import android.graphics.Rect
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

interface HeaderClickListener {
    fun onHeaderClicked(header: String)
}

class HeaderAdapter(
    private val headers: List<String>,
    private val clickListener: HeaderClickListener,
    private var selectedPosition: Int = RecyclerView.NO_POSITION

) : RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(headers[position])
        val header = headers[position]

        // Установка слушателя нажатия на элемент списка
        holder.itemView.setOnClickListener {
            // Сохранение текущего выбранного элемента
            val previousSelectedPosition = selectedPosition
            selectedPosition = position

            // Обновление внешнего вида предыдущего выбранного элемента
            notifyItemChanged(previousSelectedPosition)

            // Обновление внешнего вида текущего выбранного элемента
            notifyItemChanged(selectedPosition)
        }
        //Завтрашний Вова,я тут убился и хуй пойми чо тут делать, разберись пж, нихуя не работает
        // Установка ресурса drawable фона элемента в соответствии с выбранным состоянием
        val drawable = if (selectedPosition == position) R.drawable.header_background_active else R.drawable.header_background
        val textColor = if (selectedPosition == position) R.color.white else R.color.black
        holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, drawable)
        holder.itemView.findViewById<TextView>(R.id.textViewHeader).setTextColor(ContextCompat.getColor(holder.itemView.context, textColor))
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
        val position = parent.getChildAdapterPosition(view)
        if (position != parent.adapter?.itemCount?.minus(1)) {
            outRect.right = space
        }
    }
}