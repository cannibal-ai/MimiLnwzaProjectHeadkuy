package com.harvey.nuandsu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val allProducts: List<Product>,
    private val onItemClick: (Int) -> Unit,
    private val onEditClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredProducts: List<Product> = allProducts.toList()

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvImg: ImageView = itemView.findViewById(R.id.imgIngredient)
        val tvName: TextView = itemView.findViewById(R.id.txtName)
        val tvQuantity: TextView = itemView.findViewById(R.id.txtStock)
        val tvStatus: TextView = itemView.findViewById(R.id.status)
        val btnEdit: View = itemView.findViewById(R.id.btnEdit)

        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
            btnEdit.setOnClickListener {
                val product = filteredProducts[adapterPosition]
                onEditClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredProducts[position]
        holder.tvName.text = product.name
        holder.tvQuantity.text = product.quantity.toString()
        holder.tvStatus.text = product.status
        holder.tvImg.setImageResource(product.imag)
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
}
