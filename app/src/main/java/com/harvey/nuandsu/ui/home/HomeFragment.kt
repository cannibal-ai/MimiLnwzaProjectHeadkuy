package com.harvey.nuandsu.ui.home

import DBHelper
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.harvey.nuandsu.HistoryAdapter
import com.harvey.nuandsu.ProductHis
import com.harvey.nuandsu.R
import com.harvey.nuandsu.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HistoryAdapter
    private val historyList = mutableListOf<ProductHis>()



    private fun loadHistory() {
        val dbHelper = DBHelper(requireContext())
        val products = dbHelper.getAllProducts()


        historyList.clear()

        for ((index, p) in products.withIndex()) {
            historyList.add(
                ProductHis(
                    image = R.drawable.images,
                    name = p.name,
                    time = p.date,
                    new = if (index == 0) "ล่าสุด" else null
                )
            )
        }
        adapter.updateData(historyList)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadHistory()
        setupDashboard()
        setupChart()
        return binding.root
    }


    private val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = "ยินดีต้อนรับสู่แอปพลิเคชันของเรา 🔥🔥 "
        var index = 0

        handler.post(object : Runnable {
            override fun run() {
                _binding?.hello?.text = text.substring(0, index)
                index++
                if (index <= text.length) handler.postDelayed(this, 100)
            }
        })
    }


    private fun setupRecyclerView() {
        adapter = HistoryAdapter(historyList) { history ->
        }

        binding.history2.layoutManager = LinearLayoutManager(requireContext())
        binding.history2.adapter = adapter
    }


    private fun setupDashboard() {
        val dbHelper = DBHelper(requireContext())

        binding.low.text = dbHelper.getLowStatusCount().toString()
        binding.total.text = dbHelper.getTotalProductCount().toString()
    }

    private fun setupChart() {
        val entries = ArrayList<Entry>()
        for (day in 1..12) {
            entries.add(Entry(day.toFloat(), (10..50).random().toFloat()))
        }

        val dataSet = LineDataSet(entries, "รายจ่ายวัตถุดิบในเดือนนี้")
        dataSet.color = Color.parseColor("#6C4F35")
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setCircleColor(Color.RED)
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.parseColor("#e8d3bd")
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.description.isEnabled = false
        binding.lineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
