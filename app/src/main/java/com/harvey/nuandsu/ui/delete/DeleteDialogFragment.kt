package com.harvey.nuandsu.ui.editproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.harvey.nuandsu.R

class DeleteDialogFragment : DialogFragment() {

    private var productName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productName = arguments?.getString("productName")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_delete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val btnYesDe: Button = view.findViewById(R.id.btnYesDe)

        tvMessage.text = "คุณแน่ใจว่าจะลบ ${productName ?: ""} หรือไม่?"

        btnYesDe.setOnClickListener {
            dismiss() // ปิด dialog สำหรับตอนนี้
        }
    }

    companion object {
        fun newInstance(productName: String): DeleteDialogFragment {
            val fragment = DeleteDialogFragment()
            val bundle = Bundle()
            bundle.putString("productName", productName)
            fragment.arguments = bundle
            return fragment
        }
    }
}
