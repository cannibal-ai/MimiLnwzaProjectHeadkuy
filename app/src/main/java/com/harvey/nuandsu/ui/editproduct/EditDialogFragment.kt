package com.harvey.nuandsu.ui.editproduct

import com.harvey.nuandsu.DBHelper
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
import com.bumptech.glide.Glide
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.R
import com.harvey.nuandsu.ui.dashboard.DashboardFragment
import java.util.Date
import java.util.Locale

class EditDialogFragment : DialogFragment() {

    private lateinit var product: Product
    private lateinit var db: DBHelper

    private lateinit var nameEt: EditText
    private lateinit var typeEt: Spinner
    private lateinit var priceEt: EditText
    private lateinit var descEt: EditText
    private lateinit var qtyEt: EditText
    private lateinit var previewImage: ImageView
    private lateinit var updateBtn: Button
    
    private lateinit var btnPlus: TextView
    private lateinit var btnMinus: TextView

    private var selectedImageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    companion object {
        fun newInstance(product: Product): EditDialogFragment {
            val fragment = EditDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable("product", product)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = requireArguments().getSerializable("product") as Product
        db = DBHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        nameEt = view.findViewById(R.id.edtName)
        typeEt = view.findViewById(R.id.etTypeEdit)
        priceEt = view.findViewById(R.id.etPriceEdit)
        descEt = view.findViewById(R.id.etDescriptionEdit)
        qtyEt = view.findViewById(R.id.A)
        previewImage = view.findViewById(R.id.imgFoodEdit)
        updateBtn = view.findViewById(R.id.btnAddEdit)
        btnPlus = view.findViewById(R.id.P)
        btnMinus = view.findViewById(R.id.M)

        nameEt.setText(product.name)
        priceEt.setText(product.pc.toString())
        qtyEt.setText("0") 
        descEt.setText(product.des)

        if (!product.imageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(product.imageUri)
                .centerCrop()
                .into(previewImage)
        }

        val typeArray = resources.getStringArray(R.array.product_types_filter)
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, typeArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeEt.adapter = adapter
        val typeIndex = typeArray.indexOf(product.typ)
        if (typeIndex >= 0) typeEt.setSelection(typeIndex)

        btnPlus.setOnClickListener {
            val currentQty = qtyEt.text.toString().toIntOrNull() ?: 0
            qtyEt.setText((currentQty + 1).toString())
        }
        btnMinus.setOnClickListener {
            val currentQty = qtyEt.text.toString().toIntOrNull() ?: 0
            qtyEt.setText((currentQty - 1).toString())
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(previewImage)
            }
        }

        previewImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        updateBtn.setOnClickListener {
            val addQty = qtyEt.text.toString().toIntOrNull() ?: 0
            val newPrice = priceEt.text.toString().toIntOrNull() ?: 0
            
            val finalQuantity = product.quantity + addQty
            val safeQuantity = if (finalQuantity < 0) 0 else finalQuantity
            val newTotalCost = newPrice * safeQuantity

            // บันทึกวันที่อัปเดตเฉพาะเมื่อเพิ่มจำนวน
            val finalDate = if (addQty > 0) {
                java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            } else {
                product.date
            }

            // *** LOGIC สำคัญ: บันทึกเฉพาะส่วนต่างราคาที่จ่ายเพิ่มจริงลงใน transactions ***
            if (addQty > 0) {
                val addedAmount = newPrice * addQty
                db.insertTransaction(addedAmount)
            }

            val updatedProduct = product.copy(
                name = nameEt.text.toString(),
                pc = newPrice,
                quantity = safeQuantity,
                totalCost = newTotalCost,
                des = descEt.text.toString(),
                typ = typeEt.selectedItem.toString(),
                imageUri = selectedImageUri?.toString() ?: product.imageUri,
                date = finalDate,
            )

            db.updateProduct(updatedProduct)

            (parentFragment as? DashboardFragment)?.refreshList()
            parentFragmentManager.setFragmentResult("product_changed", Bundle())
            dismiss()
        }

        return view
    }
}
