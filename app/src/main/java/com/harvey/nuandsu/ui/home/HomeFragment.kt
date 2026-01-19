package com.harvey.nuandsu.ui.home

import com.harvey.nuandsu.DBHelper
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
import com.github.mikephil.charting.components.XAxis
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
        val history = dbHelper.getAllHistory()

        historyList.clear()
        history.forEachIndexed { index, item ->
            historyList.add(
                ProductHis(
                    name = item.name,
                    time = item.time,
                    imageUri = item.imageUri,
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
        val dbHelper = DBHelper(requireContext())
        val rawData = dbHelper.getMonthlyProductsForChart()

        // 1. รวมยอดรายวัน
        val dailySumMap = rawData.groupBy { it.first }
            .mapValues { entry -> entry.value.sumOf { it.second.toDouble() }.toFloat() }

        // 2. หาข้อมูลของเดือนปัจจุบัน
        val calendar = java.util.Calendar.getInstance()
        // หาว่าเดือนนี้มีกี่วัน (30, 31 หรือ 28)
        val lastDayOfMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH).toFloat()

        val entries = ArrayList<Entry>()

        for ((day, total) in dailySumMap) {
            entries.add(Entry(day.toFloat(), total))
        }

        entries.sortBy { it.x }

        // 3. ตั้งค่า DataSet
        val dataSet = LineDataSet(entries, "รายจ่ายรายวัน").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER // ทำให้เส้นโค้งมน
            color = Color.parseColor("#6C4F35")
            setCircleColor(Color.RED)
            circleRadius = 5f
            lineWidth = 2.5f
            setDrawFilled(true)
            fillColor = Color.parseColor("#E8D3BD")
            fillAlpha = 100
            valueTextSize = 10f
            setDrawValues(true)
        }

        // 4. ตั้งค่า LineChart
        binding.lineChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                granularity = 1f
                isGranularityEnabled = true

                axisMinimum = 1f            // เริ่มวันที่ 1
                axisMaximum = lastDayOfMonth // เปลี่ยนตามเดือนอัตโนมัติ (30 หรือ 31)
            }

            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f

            // ระยะการมองเห็นเริ่มต้น 7 วัน
            setVisibleXRangeMaximum(7f)

            // ถ้าต้องการให้เลื่อนไปดูวันล่าสุดเองอัตโนมัติ
            moveViewToX(entries.lastOrNull()?.x ?: 0f)

            animateX(800)
            invalidate()
        }
    }
    override fun onResume() {
        super.onResume()
        setupDashboard() // เพิ่มเพื่อให้ยอดอัปเดตเมื่อกลับมาหน้านี้
        setupChart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }

}
