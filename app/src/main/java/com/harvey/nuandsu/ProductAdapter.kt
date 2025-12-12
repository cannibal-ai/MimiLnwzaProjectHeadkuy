package com.harvey.nuandsu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(
    private var productList: MutableList<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txtName)
        val qty: TextView = itemView.findViewById(R.id.txtStock)
        val status: TextView = itemView.findViewById(R.id.status)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val img: ImageView = itemView.findViewById(R.id.imgIngredient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        holder.name.text = product.name
        holder.qty.text = product.quantity.toString()
        holder.status.text = product.status ?: ""

        Glide.with(holder.itemView.context)
            .load(product.imageUri)
            .into(holder.img)

        // 🎨 เปลี่ยนสีตามสถานะ
        when (product.status) {
            "หมด" -> {
                holder.status.setTextColor(Color.RED)
            }
            "น้อย" -> {
                holder.status.setTextColor(Color.parseColor("#FFA726"))
            }
            else -> {
                holder.status.setTextColor(Color.BLACK)
            }
        }

        holder.itemView.setOnClickListener { onItemClick(product) }
        holder.btnEdit.setOnClickListener { onEditClick(product) }
    }

    private var originalList: MutableList<Product> = productList.toMutableList()

    fun filter(query: String) {
        val filtered = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        (it.status?.contains(query, ignoreCase = true) ?: false) ||
                        (it.typ?.contains(query, ignoreCase = true) ?: false)
            }.toMutableList()
        }

        productList = filtered
        notifyDataSetChanged()
    }

    fun updateData(newList: List<Product>) {
        originalList = newList.toMutableList()
        productList = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun addProduct(product: Product) {
        productList.add(product)
        notifyItemInserted(productList.size - 1)
    }
}
