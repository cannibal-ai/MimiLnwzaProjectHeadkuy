package com.harvey.nuandsu.ui.dashboard

import DBHelper
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.ProductAdapter
import com.harvey.nuandsu.databinding.FragmentDashboardBinding
import com.harvey.nuandsu.ui.addproduct.AddProductDialogFragment
import com.harvey.nuandsu.ui.editproduct.EditDialogFragment

class DashboardFragment : Fragment() {

    private lateinit var db: DBHelper
    private lateinit var fullList: List<Product>
    lateinit var adapter: ProductAdapter

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        db = DBHelper(requireContext())
        fullList = db.getAllProducts()

        setupRecyclerView()
        setupSearch()
        setupAddProduct()
        setupStatusFilter()
        setupTypeFilter()

        parentFragmentManager.setFragmentResultListener(
            "product_changed",
            viewLifecycleOwner
        ) { _, _ ->
            refreshList()
        }


        return binding.root
    }


    // ---------------- RecyclerView ----------------
    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            fullList.toMutableList(),
            onItemClick = { product -> },
            onEditClick = { product ->
                val dialog = EditDialogFragment.newInstance(product)
                dialog.show(parentFragmentManager, "EditProductDialog")
            },
            onDeleteClick = { product ->
                db.deleteProduct(product.id)
                refreshList()
            }
        )

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = adapter
    }


    // ---------------- Search ----------------
    private fun setupSearch() {
        binding.Search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }


    // ---------------- Status Filter ----------------
    private fun setupStatusFilter() {
        binding.statusGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                com.harvey.nuandsu.R.id.btnall -> filterByStatus("ทั้งหมด")
                com.harvey.nuandsu.R.id.btnlow -> filterByStatus("น้อย")
                com.harvey.nuandsu.R.id.btnout -> filterByStatus("หมด")
            }
        }
    }

    private fun filterByStatus(status: String) {
        val filtered = when (status) {
            "ทั้งหมด" -> fullList
            else -> fullList.filter { it.status == status }
        }
        adapter.updateData(filtered)
    }


    // ---------------- Type Filter (Spinner) ----------------


    private fun setupTypeFilter() {
        val adapterSpinner = android.widget.ArrayAdapter.createFromResource(
            requireContext(),
            com.harvey.nuandsu.R.array.planets_array, // array ของ Spinner
            com.harvey.nuandsu.R.layout.spinner_item  // layout ตัวอักษรสีดำ
        )

        adapterSpinner.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.planetsSpinner.adapter = adapterSpinner

        binding.planetsSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    val type = parent.getItemAtPosition(pos).toString()
                    filterByType(type)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        binding.planetsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val type = parent.getItemAtPosition(pos).toString()
                filterByType(type)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterByType(type: String) {
        val filtered = when (type) {
            "ทั้งหมด" -> fullList
            else -> fullList.filter { it.typ == type }
        }
        adapter.updateData(filtered)
    }




    // ---------------- Add Product Button ----------------
    private fun setupAddProduct() {
        binding.Card.setOnClickListener {
            AddProductDialogFragment().show(parentFragmentManager, "AddProductDialog")
        }
    }


    // ---------------- Refresh Data After Delete/Edit ----------------
    fun refreshList() {
        fullList = db.getAllProducts()

        binding.planetsSpinner.setSelection(0)

        adapter.updateData(fullList)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

}
