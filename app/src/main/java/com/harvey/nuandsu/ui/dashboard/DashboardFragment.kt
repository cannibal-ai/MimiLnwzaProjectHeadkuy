package com.harvey.nuandsu.ui.dashboard

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
import com.harvey.nuandsu.R
import com.harvey.nuandsu.databinding.FragmentDashboardBinding
import com.harvey.nuandsu.ui.addproduct.AddProductDialogFragment
import com.harvey.nuandsu.ui.editproduct.EditDialogFragment

class DashboardFragment : Fragment() {

    private lateinit var fullList: List<Product>
    private lateinit var adapter: ProductAdapter

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val productList = mutableListOf(
        Product("เส้นใหญ่", 10, null, R.drawable.o,"เครื่องเคียง",20,"เส้นใหญ่ไม่ใช่เส้นเล็ก","2025-12-10"),
        Product("ผักกาด", 5, "น้อย", R.drawable.images,"ผัก",10,"ผักกากมันใส่ในก๋วยจั๊บด้วยหรอวะ","2025-12-10"),
        Product("หมูกรอบ", 0, "หมด", R.drawable.mugrob,"เนื้อสัตว์",200,"หมูกรอบมีมี่ชอบ","2025-12-10"),
        Product("น้ำตาล", 0, "หมด", R.drawable.num,"เครื่องเคียง",5,"น้ำตาลหวานร้อย","2025-12-10"),
        Product("น้ำปลา", 5, "น้อย", R.drawable.numpra,"เครื่องเคียง",20,"น้ำตาลเค็มนำ","2025-12-10"),
        Product("พริกป่น", 20, null, R.drawable.prik,"เครื่องเคียง",20,"เผ็ดจนแสบตูด","2025-12-10"),
        Product("ก๋วยจั๊บ", 20, null, R.drawable.mama,"เครื่องเคียง",20,"ก๋วยจั๊บเขม","2025-12-10"),
        Product("ตับ", 20, null, R.drawable.tub,"เนื้อสัตว์",20,"ตับคน","2025-12-10")
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fullList = productList
        adapter = ProductAdapter(
            fullList,
            onItemClick = { product -> },
            onEditClick = { product ->
                val dialog = EditDialogFragment.newInstance(product)
                dialog.show(parentFragmentManager, "EditProductDialog")
            }
        )

        binding.statusGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btnall -> filterByStatus("ทั้งหมด")
                R.id.btnlow -> filterByStatus("น้อย")
                R.id.btnout -> filterByStatus("หมด")
            }
        }


        binding.recyclerViewProducts.adapter = adapter
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())

        binding.planetsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = parent.getItemAtPosition(position).toString()
                filterList(selectedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun filterByStatus(status: String) {
        val filtered = when (status) {
            "ทั้งหมด" -> productList
            "น้อย" -> productList.filter { it.status == "น้อย" }
            "หมด" -> productList.filter { it.status == "หมด" }
            else -> productList
        }

        adapter.updateData(filtered)
    }



    private fun filterList(type: String) {
        val newList = when (type) {
            "ทั้งหมด" -> fullList
            "ผัก" -> fullList.filter { it.typ == "ผัก" }
            "เครื่องเคียง" -> fullList.filter { it.typ == "เครื่องเคียง" }
            "เนื้อสัตว์" -> fullList.filter { it.typ == "เนื้อสัตว์" }
            else -> fullList
        }

        adapter.updateData(newList)
    }






    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupSearch()
        setupAddProductClick()

        return root
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(productList,
            onItemClick = { position -> },
            onEditClick = { product ->
                val dialog = EditDialogFragment.newInstance(product)
                dialog.show(parentFragmentManager, "EditProductDialog")
            }
        )

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = adapter
    }

    private fun setupSearch() {
        binding.Search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }



    private fun setupAddProductClick() {
        binding.Card.setOnClickListener {
            val dialog = AddProductDialogFragment()
            dialog.show(parentFragmentManager, "AddProductDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}