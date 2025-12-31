package com.harvey.nuandsu.ui.editproduct

import DBHelper
import android.app.Activity
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.R
import org.w3c.dom.Text


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

    private fun getUpdatedStatus(product: Product, qty: Int): String {
        val today = java.time.LocalDate.now()
        val expiry = try {
            java.time.LocalDate.parse(product.expiryDate)
        } catch (e: Exception) {
            today.plusDays(30)
        }

        val lastUpdate = try {
            java.time.LocalDate.parse(product.lastUpdateDate)
        } catch (e: Exception) {
            today
        }

        return when {
            today.isAfter(expiry.minusDays(3)) -> "ใกล้หมดอายุ"
            today.isAfter(lastUpdate.plusDays(7)) -> "ใกล้หมดอายุ"
            else -> "ปกติ"
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit, container, false)

        val name = view.findViewById<EditText>(R.id.edtName)
        val price = view.findViewById<EditText>(R.id.etPriceEdit)
        val type = view.findViewById<Spinner>(R.id.etTypeEdit)
        val detail = view.findViewById<EditText>(R.id.etDescriptionEdit)
        imgEdit = view.findViewById(R.id.imgFoodEdit)
        val save = view.findViewById<Button>(R.id.btnAddEdit)
        val Plus = view.findViewById<TextView>(R.id.P)
        val Minus = view.findViewById<TextView>(R.id.M)
        val Add = view.findViewById<EditText>(R.id.A)

        var addQty = 0
        Add.setText("0")


        Plus.setOnClickListener {
            addQty++
            Add.setText(addQty.toString())
        }

        Minus.setOnClickListener {
            if (addQty > 0) {
                addQty--
                Add.setText(addQty.toString())
            }
        }



        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.product_types_filter,
            R.layout.spinner_itemhe
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        type.adapter = adapter


        name.setText(product.name)
        price.setText(product.pc.toString())
        val index = adapter.getPosition(product.typ)
        if (index >= 0) {
            type.setSelection(index)
        }

        detail.setText(product.des)

        if (!product.imageUri.isNullOrEmpty()) {
            Glide.with(requireContext()).load(product.imageUri).into(imgEdit)
        }

        imgEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }


        save.setOnClickListener {

            val add = Add.text.toString().toIntOrNull() ?: 0

            val newQuantity = product.quantity + add
            val newTotalCost = product.totalCost + (add * product.pc)

            val updatedProduct = product.copy(
                name = name.text.toString(),
                pc = price.text.toString().toInt(),
                typ = type.selectedItem.toString(),
                des = detail.text.toString(),
                quantity = newQuantity,
                totalCost = newTotalCost,
                status = getUpdatedStatus(product, newQuantity),
                lastUpdateDate = java.time.LocalDate.now().toString()
            )

            db.updateProduct(updatedProduct)

            parentFragmentManager.setFragmentResult(
                "product_changed",
                Bundle()
            )

            dismiss()
        }

        return view
    }
}
