package com.harvey.nuandsu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harvey.nuandsu.ui.history.historyFragment
import androidx.core.graphics.toColorInt

class HistoryAdapter(

    private var historyList: List<ProductHis>,
    private val onItemClick: (ProductHis) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var allProducts: List<ProductHis> = historyList.toList()
    private var filteredProducts: List<ProductHis> = historyList.toList()

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtimg: ImageView = itemView.findViewById(R.id.txtimg)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)
        val txtNew: TextView = itemView.findViewById(R.id.newbie)

        init {
            itemView.setOnClickListener {
                val history = filteredProducts[adapterPosition]
                onItemClick(history)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = filteredProducts[position]

        if (!history.imageUri.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(history.imageUri)
                .placeholder(R.drawable.images)
                .error(R.drawable.images)
                .into(holder.txtimg)
        } else {
            holder.txtimg.setImageResource(R.drawable.images)
        }

        holder.txtName.text = history.name
        holder.txtTime.text = history.time
        holder.txtNew.text = history.new
        holder.txtNew.setTextColor(if (history.new == "ล่าสุด") "#4CAF50".toColorInt() else "#fbc02d".toColorInt())

    }


    override fun getItemCount(): Int = filteredProducts.size

    fun filter(query: String) {
        filteredProducts = if (query.isEmpty()) {
            allProducts
        } else {
            allProducts.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateData(newList: List<ProductHis>) {
        allProducts = newList
        filteredProducts = newList
        notifyDataSetChanged()
    }

}

