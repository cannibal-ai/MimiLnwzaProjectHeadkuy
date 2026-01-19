package com.harvey.nuandsu.ui.dashboard

import com.harvey.nuandsu.DBHelper
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
import com.harvey.nuandsu.ui.editproduct.DeleteDialogFragment
import com.harvey.nuandsu.ui.editproduct.EditDialogFragment
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DashboardFragment : Fragment() {

    private lateinit var db: DBHelper
    private lateinit var fullList: List<Product>
    lateinit var adapter: ProductAdapter

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private var isFirstLoad = true

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

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            fullList.toMutableList(),
            onItemClick = { product -> },
            onEditClick = { product ->
                EditDialogFragment
                    .newInstance(product)
                    .show(parentFragmentManager, "EditProductDialog")
            },
            onDeleteClick = { product ->
                DeleteDialogFragment
                    .newInstance(product)
                    .show(parentFragmentManager, "DeleteDialog")
            },
            fragmentManager = parentFragmentManager
        )

        binding.recyclerViewProducts.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = adapter
    }

    private fun setupSearch() {
        binding.Search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun applyFilters() {
        val today = LocalDate.now()
        
        val selectedType = binding.planetsSpinner.selectedItem?.toString() ?: "ทั้งหมด"
        var filteredList = if (selectedType.contains("ทั้งหมด") || selectedType.contains("เลือก")) {
            fullList
        } else {
            fullList.filter { it.typ == selectedType }
        }

        val checkedId = binding.statusGroup.checkedRadioButtonId
        if (checkedId == com.harvey.nuandsu.R.id.btnlow) {
            filteredList = filteredList.filter { product ->
                try {
                    val datePart = product.date.substring(0, 10)
                    val addedDate = LocalDate.parse(datePart)
                    val diffDays = ChronoUnit.DAYS.between(addedDate, today)
                    diffDays >= 7L // เปลี่ยนเป็น 7 วันตามต้องการ
                } catch (e: Exception) {
                    false
                }
            }
        }

        adapter.updateData(filteredList)
    }

    private fun setupStatusFilter() {
        binding.statusGroup.setOnCheckedChangeListener { _, _ ->
            applyFilters()
        }
    }

    private fun setupTypeFilter() {
        val adapterSpinner = android.widget.ArrayAdapter.createFromResource(
            requireContext(),
            com.harvey.nuandsu.R.array.planets_array,
            com.harvey.nuandsu.R.layout.spinner_item
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.planetsSpinner.adapter = adapterSpinner

        binding.planetsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if (isFirstLoad) {
                    isFirstLoad = false
                    return
                }
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupAddProduct() {
        binding.Card.setOnClickListener {
            AddProductDialogFragment().show(parentFragmentManager, "AddProductDialog")
        }
    }

    fun refreshList() {
        fullList = db.getAllProducts()
        applyFilters()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
