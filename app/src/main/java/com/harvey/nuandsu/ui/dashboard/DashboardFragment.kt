package com.harvey.nuandsu.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.harvey.nuandsu.Product
import com.harvey.nuandsu.ProductAdapter
import com.harvey.nuandsu.R
import com.harvey.nuandsu.databinding.FragmentDashboardBinding
import com.harvey.nuandsu.ui.addproduct.AddProductDialogFragment
import com.harvey.nuandsu.ui.addproduct.AddProductFragment
import com.harvey.nuandsu.ui.editproduct.EditDialogFragment

class DashboardFragment : Fragment() {

  private var _binding: FragmentDashboardBinding? = null
  private val binding get() = _binding!!

  private val productList = mutableListOf(
    Product("Coke", 10, "หมด", R.drawable.mu),
    Product("Pepsi", 5, "น้อย", R.drawable.mu),
    Product("Sprite", 20, null, R.drawable.mu),
    Product("Coke", 10, "หมด", R.drawable.mu),
    Product("Pepsi", 5, "น้อย", R.drawable.mu),
    Product("Sprite", 20, null, R.drawable.mu),
    Product("Coke", 10, "หมด", R.drawable.mu),
    Product("Pepsi", 5, "น้อย", R.drawable.mu),
    Product("Sprite", 20, null, R.drawable.mu),
    Product("Coke", 10, "หมด", R.drawable.mu),
    Product("Pepsi", 5, "น้อย", R.drawable.mu),
    Product("Sprite", 20, null, R.drawable.mu)
  )

  private lateinit var adapter: ProductAdapter

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
        val dialog = EditDialogFragment()
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