package com.harvey.nuandsu.ui.editproduct

import DBHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.R
import com.harvey.nuandsu.ui.dashboard.DashboardFragment

class DeleteDialogFragment : DialogFragment() {

    private lateinit var product: Product
    private lateinit var db: DBHelper

    companion object {
        fun newInstance(product: Product): DeleteDialogFragment {
            val fragment = DeleteDialogFragment()
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
        val view = inflater.inflate(R.layout.fragment_delete, container, false)

        val btnConfirm = view.findViewById<Button>(R.id.btnYesDe)
        val txtMessage = view.findViewById<TextView>(R.id.tvMessage)

        txtMessage.text = "ต้องการลบ \"${product.name}\" ใช่ไหม?"

        btnConfirm.setOnClickListener {
            db.deleteProduct(product.id)

            // แจ้งให้หน้า Dashboard refresh list
            (parentFragment as? DashboardFragment)?.let { dash ->
                dash.refreshList()
            }

            dismiss()               // ปิด popup ลบ
            parentFragmentManager.findFragmentByTag("EditProductDialog")?.let {
                (it as DialogFragment).dismiss()  // ปิดหน้า Edit ด้วย
            }
        }


        return view
    }
}
