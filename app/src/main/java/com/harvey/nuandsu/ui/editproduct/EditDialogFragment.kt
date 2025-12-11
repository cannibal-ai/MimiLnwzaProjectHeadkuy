package com.harvey.nuandsu.ui.editproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.R
import com.harvey.nuandsu.ui.delete.DeleteFragment

class EditDialogFragment : DialogFragment() {

    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getSerializable(ARG_PRODUCT) as? Product
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val img = view.findViewById<ImageView>(R.id.imgFoodEdit)
        val name = view.findViewById<EditText>(R.id.edtName)
        val qty = view.findViewById<EditText>(R.id.edtQuantity)
        val type = view.findViewById<EditText>(R.id.etTypeEdit)
        val pr = view.findViewById<EditText>(R.id.etPriceEdit)
        val des = view.findViewById<EditText>(R.id.etDescriptionEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)

        btnDelete.setOnClickListener {
            val deleteDialog = DeleteDialogFragment.newInstance(product?.name ?: "")
            deleteDialog.show(parentFragmentManager, "deleteDialog")
        }

        product?.let {
            img.setImageResource(it.image)
            name.setText(it.name)
            qty.setText(it.quantity.toString())
            type.setText(it.typ)
            pr.setText(it.pc.toString())
            des.setText(it.des)
        }
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
