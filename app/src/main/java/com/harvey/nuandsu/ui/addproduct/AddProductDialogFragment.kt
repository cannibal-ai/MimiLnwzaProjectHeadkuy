package com.harvey.nuandsu.ui.addproduct

import DBHelper
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.R
import com.harvey.nuandsu.ui.dashboard.DashboardFragment

class AddProductDialogFragment : DialogFragment() {

    private var selectedImageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    private lateinit var db: DBHelper

    private lateinit var nameEt: EditText
    private lateinit var qtyEt: EditText
    private lateinit var typeEt: EditText
    private lateinit var statusEt: EditText
    private lateinit var descEt: EditText

    private lateinit var previewImage: ImageView
    private lateinit var chooseImageBtn: ImageView
    private lateinit var addBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_add_product, container, false)

        db = DBHelper(requireContext())

        nameEt = view.findViewById(R.id.etProductName)
        qtyEt = view.findViewById(R.id.etMany)
        typeEt = view.findViewById(R.id.etType)
        statusEt = view.findViewById(R.id.etPrice)
        descEt = view.findViewById(R.id.etDescription)

        chooseImageBtn = view.findViewById(R.id.btnChooseImage)
        previewImage = view.findViewById(R.id.imgPreview)
        addBtn = view.findViewById(R.id.btnAdd)

        chooseImageBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        addBtn.setOnClickListener {
            val name = nameEt.text.toString()
            val type = typeEt.text.toString()
            val qty = qtyEt.text.toString().toIntOrNull() ?: 0
            val status = when {
                qty == 0 -> "หมด"
                qty < 10 -> "น้อย"
                else -> null
            }
            val desc = descEt.text.toString()
            val date = java.time.LocalDate.now().toString()



            val newId = db.insertProduct(
                Product(
                    name = name,
                    quantity = qty,
                    status = status,
                    imageUri = selectedImageUri?.toString(),
                    typ = type,
                    des = desc,
                    date = date,
                )
            ).toInt()

            val newProduct = Product(
                id = newId,
                name = name,
                quantity = qty,
                status = status,
                imageUri = selectedImageUri?.toString(),
                typ = type,
                des = desc,
                date = date,
            )

            (parentFragment as? DashboardFragment)?.adapter?.addProduct(newProduct)
            dismiss()
        }

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

        // คลิกเพื่อเลือกรูป
        chooseImageBtn.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

}
