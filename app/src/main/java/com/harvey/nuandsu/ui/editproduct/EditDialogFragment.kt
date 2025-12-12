package com.harvey.nuandsu.ui.editproduct

import DBHelper
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.R

class EditDialogFragment : DialogFragment() {

    private lateinit var product: Product
    private lateinit var db: DBHelper
    private lateinit var imgEdit: ImageView

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    product = product.copy(imageUri = uri.toString())
                    Glide.with(requireContext()).load(uri).into(imgEdit)
                }
            }
        }

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        val name = view.findViewById<EditText>(R.id.edtName)
        val price = view.findViewById<EditText>(R.id.etPriceEdit)   // ราคา
        val type = view.findViewById<EditText>(R.id.etTypeEdit)
        val qty = view.findViewById<EditText>(R.id.edtQuantity)
        val detail = view.findViewById<EditText>(R.id.etDescriptionEdit)
        imgEdit = view.findViewById(R.id.imgFoodEdit)
        val save = view.findViewById<Button>(R.id.btnAddEdit)
        val delete = view.findViewById<Button>(R.id.btnDelete)

        // ใส่ค่าลง EditText
        name.setText(product.name)
        price.setText(product.pc.toString())    // ราคามาแล้ววว
        type.setText(product.typ)
        qty.setText(product.quantity.toString())
        detail.setText(product.des)

        if (!product.imageUri.isNullOrEmpty()) {
            Glide.with(requireContext()).load(product.imageUri).into(imgEdit)
        }

        imgEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        delete.setOnClickListener {
            val dialog = DeleteDialogFragment.newInstance(product)
            dialog.show(parentFragmentManager, "DeleteDialog")
        }

        save.setOnClickListener {

            val updatedProduct = product.copy(
                name = name.text.toString(),
                pc = price.text.toString().toInt(),
                typ = type.text.toString(),
                quantity = qty.text.toString().toInt(),
                des = detail.text.toString(),
                imageUri = product.imageUri
            )

            db.updateProduct(updatedProduct)
            dismiss()
        }

        return view
    }
}
