package com.harvey.nuandsu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harvey.nuandsu.ui.editproduct.DeleteDialogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ProductAdapter(

    private var productList: MutableList<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit,
    private val fragmentManager: FragmentManager


) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txtName)
        val status: TextView = itemView.findViewById(R.id.status)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
        val img: ImageView = itemView.findViewById(R.id.imgIngredient)

        val txtTotal: TextView = itemView.findViewById(R.id.txttotal)

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
        holder.txtTotal.text = product.pc.toString()

        Glide.with(holder.itemView.context)
            .load(product.imageUri)
            .into(holder.img)

        // คำนวณสถานะจากวันที่เพิ่มสินค้า (product.date)
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val addedDate = LocalDate.parse(product.date.substring(0, 10)) 
            val today = LocalDate.now()
            val diffDays = ChronoUnit.DAYS.between(addedDate, today)

            when {
                diffDays >= 7L -> {
                    // ปรับเป็น 1 วันเพื่อใช้ในการทดสอบและจัดการปัญหา
                    holder.status.text = "หมดอายุ"
                    holder.status.setTextColor(Color.parseColor("#b0120a"))
                }
                else -> {
                    holder.status.text = ""
                }
            }
        } catch (e: Exception) {
            holder.status.text = ""
        }

        holder.itemView.setOnClickListener { onItemClick(product) }
        holder.btnEdit.setOnClickListener { onEditClick(product) }
        holder.btnDelete.setOnClickListener { onDeleteClick(product) }
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

    fun updateProduct(updatedProduct: Product) {
        val index = productList.indexOfFirst { it.id == updatedProduct.id }
        if (index >= 0) {
            productList[index] = updatedProduct
            notifyItemChanged(index)
        }
    }
}
