package com.harvey.nuandsu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.Normalizer

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
        holder.tvImg.setImageResource(product.image)

        when (product.status) {
            "หมด" -> holder.tvStatus.setTextColor(android.graphics.Color.RED)
            "น้อย" -> holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FFA500"))
            "ทั้งหมด" -> holder.tvStatus.setTextColor(android.graphics.Color.GREEN)
            null -> holder.tvStatus.setTextColor(android.graphics.Color.GRAY)
            else -> holder.tvStatus.setTextColor(android.graphics.Color.GRAY)
        }
    }

    override fun getItemCount(): Int = filteredProducts.size


    fun updateData(newList: List<Product>) {
        filteredProducts = newList
        notifyDataSetChanged()
    }

    private fun normalizeThai(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .lowercase()
    }

    fun filter(query: String) {
        val normalizedQuery = normalizeThai(query)

        filteredProducts = if (normalizedQuery.isEmpty()) {
            allProducts
        } else {
            allProducts.filter {
                normalizeThai(it.name).contains(normalizedQuery)
            }
        }

        notifyDataSetChanged()
    }

    fun filterByStatus(status: String?) {
        filteredProducts = when (status) {
            null, "all" -> allProducts
            else -> allProducts.filter { it.status == status }
        }
        notifyDataSetChanged()
    }

}
