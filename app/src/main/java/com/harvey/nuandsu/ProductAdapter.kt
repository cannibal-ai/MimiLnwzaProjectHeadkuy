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
        val btnEdit: LinearLayout = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
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


        val displayStatus = getDisplayStatus(product)

        holder.name.text = product.name
        holder.status.text = displayStatus

        Glide.with(holder.itemView.context)
            .load(product.imageUri)
            .into(holder.img)

        holder.status.setTextColor(
            if (displayStatus == "ใกล้หมดอายุ") Color.parseColor("#FFA726") else Color.parseColor("#31653d")
        )

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

    private fun getDisplayStatus(product: Product): String {
        val today = LocalDate.now()
        val expiry = try {
            LocalDate.parse(product.expiryDate)
        } catch (e: Exception) {
            today.plusDays(30)
        }
        val lastUpdate = try {
            LocalDate.parse(product.lastUpdateDate)
        } catch (e: Exception) {
            today
        }

        return when {
            today.isAfter(expiry.minusDays(3)) -> "ใกล้หมดอายุ"
            today.isAfter(lastUpdate.plusDays(7)) -> "ใกล้หมดอายุ"
            else -> "ปกติ"
        }
    }
}
