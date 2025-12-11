package com.harvey.nuandsu.ui.editproduct

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditDialogFragment : DialogFragment() {

    private var product: Product? = null

    interface OnProductDeleteListener {
        fun onProductDeleted(product: Product)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getSerializable(ARG_PRODUCT) as? Product
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.fragment_edit, null)

        // bind view
        val img = dialogView.findViewById<ImageView>(R.id.imgFoodEdit)
        val name = dialogView.findViewById<EditText>(R.id.edtName)
        val qty = dialogView.findViewById<EditText>(R.id.edtQuantity)
        val type = dialogView.findViewById<EditText>(R.id.etTypeEdit)
        val pr = dialogView.findViewById<EditText>(R.id.etPriceEdit)
        val des = dialogView.findViewById<EditText>(R.id.etDescriptionEdit)

        product?.let {
            img.setImageResource(it.image)
            name.setText(it.name)
            qty.setText(it.quantity.toString())
            type.setText(it.typ)
            pr.setText(it.pc.toString())
            des.setText(it.des)
        }

        // สร้าง MaterialAlertDialog
        return MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setTitle("ยืนยันการลบ")
            .setMessage("คุณแน่ใจว่าจะลบ ${product?.name} ใช่หรือไม่?")
            .setPositiveButton("ลบ") { dialog, _ ->
                // callback ไป Activity/Fragment
                (activity as? OnProductDeleteListener)?.onProductDeleted(product!!)
                dialog.dismiss()
                dismiss() // ปิด DialogFragment
            }
            .setNegativeButton("ยกเลิก") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Product): EditDialogFragment {
            val fragment = EditDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_PRODUCT, product)
            fragment.arguments = bundle
            return fragment
        }
    }
}
