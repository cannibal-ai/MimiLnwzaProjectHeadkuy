package com.harvey.nuandsu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harvey.nuandsu.ui.history.historyFragment

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
        holder.txtimg.setImageResource(history.image)
        holder.txtName.text = history.name
        holder.txtTime.text = history.time.toString()
        holder.txtNew.text = history.new
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

