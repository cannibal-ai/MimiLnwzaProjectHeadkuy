package com.harvey.nuandsu.ui.addproduct

import DBHelper
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.R
import com.harvey.nuandsu.ui.dashboard.DashboardFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddProductDialogFragment : DialogFragment() {

    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val dateTimeNow = now.format(formatter)


    private var selectedImageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    private lateinit var db: DBHelper

    private lateinit var nameEt: EditText
    private lateinit var typeEt: Spinner

    private lateinit var priceEt: EditText
    private lateinit var descEt: EditText

    private lateinit var previewImage: ImageView
    private lateinit var chooseImageBtn: ImageView
    private lateinit var addBtn: Button

    private lateinit var btnPlus: TextView
    private lateinit var btnMinus: TextView
    private lateinit var etAddQty: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_add_product, container, false)

        db = DBHelper(requireContext())

        nameEt = view.findViewById(R.id.etProductName)
        typeEt = view.findViewById(R.id.etType)
        priceEt = view.findViewById(R.id.etPrice)
        descEt = view.findViewById(R.id.etDescription)
        chooseImageBtn = view.findViewById(R.id.btnChooseImage)
        previewImage = view.findViewById(R.id.imgPreview)
        addBtn = view.findViewById(R.id.btnAdd)
        btnPlus = view.findViewById(R.id.btnPlus)
        btnMinus = view.findViewById(R.id.btnMinus)
        etAddQty = view.findViewById(R.id.etAddQty)

        chooseImageBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        var addQty = 0
        etAddQty.setText("0")

        btnPlus.setOnClickListener {
            addQty++
            etAddQty.setText(addQty.toString())
        }

        btnMinus.setOnClickListener {
            if (addQty > 0) {
                addQty--
                etAddQty.setText(addQty.toString())
            }
        }



        addBtn.setOnClickListener {
            val name = nameEt.text.toString()
            val type = typeEt.selectedItem.toString()
            if (type == "เลือกประเภท") {
                typeEt.requestFocus()
                return@setOnClickListener
            }

            val price = priceEt.text.toString().toIntOrNull() ?: 0
            val qty = etAddQty.text.toString().toIntOrNull() ?: 0

            if (price == null || price <= 0) {
                priceEt.error = "กรุณากรอกราคาสินค้า"
                priceEt.requestFocus()
                return@setOnClickListener
            }

            val total = price * qty
            val date = java.time.LocalDate.now().toString()
            val expiry = try {
                java.time.LocalDate.parse(date)
            } catch (e: Exception) {
                java.time.LocalDate.now().plusDays(30)
            }

            val lastUpdate = java.time.LocalDate.now()

            val status = when {
                java.time.LocalDate.now().isAfter(expiry.minusDays(3)) -> "ใกล้หมดอายุ"
                java.time.LocalDate.now().isAfter(lastUpdate.plusDays(7)) -> "ใกล้หมดอายุ"
                else -> "ปกติ"
            }


            val desc = descEt.text.toString()




            val newId = db.insertProduct(
                Product(
                    name = name,
                    pc = price,
                    quantity = qty,
                    status = status,
                    imageUri = selectedImageUri?.toString(),
                    typ = type,
                    des = desc,
                    totalCost = total,
                    date = dateTimeNow
                )
            ).toInt()




            val newProduct = Product(
                id = newId,
                name = name,
                pc = price,
                quantity = qty,
                status = status,
                imageUri = selectedImageUri?.toString(),
                typ = type,
                totalCost = total,
                des = desc,
            )


            (parentFragment as? DashboardFragment)?.adapter?.addProduct(newProduct)

            parentFragmentManager.setFragmentResult(
                "product_changed",
                Bundle()
            )
            dismiss()
        }
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.product_types_filter,
            R.layout.spinner_item
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        typeEt.adapter = adapter


        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {

                selectedImageUri = uri
                chooseImageBtn.visibility = View.VISIBLE
                chooseImageBtn.setImageURI(uri)
                chooseImageBtn.setImageURI(uri)
            }
        }

        chooseImageBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

}
