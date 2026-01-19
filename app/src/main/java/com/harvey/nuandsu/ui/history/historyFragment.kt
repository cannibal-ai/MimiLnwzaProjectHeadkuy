package com.harvey.nuandsu.ui.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.harvey.nuandsu.DBHelper
import com.harvey.nuandsu.HistoryAdapter
import com.harvey.nuandsu.ProductHis
import com.harvey.nuandsu.R
import com.harvey.nuandsu.databinding.FragmentHistoryBinding

class historyFragment : Fragment() {



    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyList = mutableListOf<ProductHis>()




    private fun loadHistory() {
        val dbHelper = DBHelper(requireContext())
        val history = dbHelper.getAllHistory()

        historyList.clear()
        historyList.addAll(history)
        adapter.updateData(historyList)
    }
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearch()
        loadHistory()

        return binding.root
    }


    private fun setupRecyclerView() {
        adapter = HistoryAdapter(historyList) { history ->
        }

        binding.history.layoutManager = LinearLayoutManager(requireContext())
        binding.history.adapter = adapter
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
