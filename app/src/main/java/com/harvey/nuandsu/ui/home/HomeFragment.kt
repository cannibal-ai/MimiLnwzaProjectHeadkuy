package com.harvey.nuandsu.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.harvey.nuandsu.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.harvey.nuandsu.Product
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var ProductList: List<Product>
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val productList = listOf(
        // ตัวอย่าง
        Product("2025-12-01", 200),
        Product("2025-12-01", 300),
        Product("2025-12-02", 150),
        Product("2025-12-03", 500)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        setupExpenseChart()   // 👉 เรียกใช้ตรงนี้!

        return root
    }

    private fun setupExpenseChart() {
        val chart = binding.lineChart

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // รวมยอดรายวัน
        val expenseByDay = productList.groupBy { it.date }.mapValues { entry ->
            entry.value.sumOf { it.pc }
        }

        // เรียงวันที่
        val sortedDates = expenseByDay.keys.sortedBy { dateFormat.parse(it) }

        val entries = ArrayList<Entry>()
        sortedDates.forEachIndexed { index, date ->
            entries.add(Entry(index.toFloat(), expenseByDay[date]!!.toFloat()))
        }

        // label แสดงแค่วันที่ เช่น 01, 02, 15
        val labels = sortedDates.map { it.substring(8, 10) }

        val dataSet = LineDataSet(entries, "รายจ่ายรายวันของเดือนนี้")
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 5f
        dataSet.valueTextSize = 12f

        chart.data = LineData(dataSet)

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chart.xAxis.granularity = 1f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false

        chart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
