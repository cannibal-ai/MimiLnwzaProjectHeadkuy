package com.harvey.nuandsu.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.harvey.nuandsu.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lineChart = view.findViewById<LineChart>(R.id.lineChart)

        val entries = ArrayList<Entry>()
        for (day in 1..30) {
            val value = (10..50).random().toFloat()
            entries.add(Entry(day.toFloat(), value))
        }

        val dataSet = LineDataSet(entries, "ยอดขาย 30 วัน")
        dataSet.color = Color.parseColor("#6C4F35")
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setCircleColor(Color.RED)
        dataSet.setDrawFilled(true)
        dataSet.fillColor =  Color.parseColor("#e8d3bd")
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER


        val lineData = LineData(dataSet)

        lineChart.data = lineData
        lineChart.description.isEnabled = false
        lineChart.invalidate()
    }
}
