package com.harvey.nuandsu.ui.addproduct

import com.harvey.nuandsu.DBHelper
import android.net.Uri
import android.os.Bundle
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.ProductHis
import com.harvey.nuandsu.R
import com.harvey.nuandsu.ui.dashboard.DashboardFragment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddProductDialogFragment : DialogFragment() {

    private val now = LocalDateTime.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    private val dateTimeNow = now.format(formatter)

    private var selectedImageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var db: DBHelper

    private lateinit var nameEt: EditText
    private lateinit var typeEt: Spinner
    private lateinit var priceEt: EditText
    private lateinit var descEt: EditText
    private lateinit var previewImage: ImageView
    private lateinit var addBtn: Button
    private lateinit var btnPlus: TextView
    private lateinit var btnMinus: TextView
    private lateinit var etAddQty: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        db = DBHelper(requireContext())

        nameEt = view.findViewById(R.id.etProductName)
        typeEt = view.findViewById(R.id.etType)
        priceEt = view.findViewById(R.id.etPrice)
        descEt = view.findViewById(R.id.etDescription)
        previewImage = view.findViewById(R.id.imgPreview)
        addBtn = view.findViewById(R.id.btnAdd)
        btnPlus = view.findViewById(R.id.btnPlus)
        btnMinus = view.findViewById(R.id.btnMinus)
        etAddQty = view.findViewById(R.id.etAddQty)

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                Glide.with(this).load(uri).centerCrop().into(previewImage)
            }
        }

        previewImage.setOnClickListener { imagePickerLauncher.launch("image/*") }

        btnPlus.setOnClickListener {
            val currentQty = etAddQty.text.toString().toIntOrNull() ?: 0
            etAddQty.setText((currentQty + 1).toString())
        }

        btnMinus.setOnClickListener {
            Toast.makeText(requireContext(), "ไม่สามารถลบจำนวนวัตถุดิบได้", Toast.LENGTH_SHORT).show()
        }

        addBtn.setOnClickListener {
            val name = nameEt.text.toString()
            val type = typeEt.selectedItem?.toString() ?: ""
            
            if (type.contains("เลือกประเภท") || type.isEmpty()) {
                val errorText = "กรุณาเลือกประเภทสินค้า"
                val tv = typeEt.selectedView as? TextView
                tv?.error = errorText
                tv?.text = errorText
                tv?.setTextColor(Color.RED)
                typeEt.requestFocus()
                return@setOnClickListener
            }

            val price = priceEt.text.toString().toIntOrNull() ?: 0
            val qty = etAddQty.text.toString().toIntOrNull() ?: 0

            if (price <= 0) {
                priceEt.error = "กรุณากรอกราคาสินค้า"
                priceEt.requestFocus()
                return@setOnClickListener
            }

            if (qty <= 0) {
                Toast.makeText(requireContext(), "กรุณาระบุจำนวนวัตถุดิบ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = price * qty
            
            // ดึงข้อมูลจาก descEt มาเก็บไว้ในตัวแปร desc
            val desc = descEt.text.toString()

            val newId = db.insertProduct(
                Product(
                    name = name, pc = price, quantity = qty, status = "ปกติ",
                    imageUri = selectedImageUri?.toString(), typ = type,
                    des = desc, totalCost = total, date = dateTimeNow
                )
            ).toInt()

            db.insertHistory(
                ProductHis(
                    name = name, time = dateTimeNow, new = "ล่าสุด",
                    imageUri = selectedImageUri?.toString()
                )
            )

            (parentFragment as? DashboardFragment)?.refreshList()
            parentFragmentManager.setFragmentResult("product_changed", Bundle())
            dismiss()
        }

        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.product_types_filter, R.layout.spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeEt.adapter = adapter

        return view
    }
}
