package com.harvey.nuandsu.ui.notifications

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.harvey.nuandsu.DBHelper
import com.harvey.nuandsu.databinding.FragmentNotificationsBinding
import java.util.Calendar
import java.util.Locale

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val monthFormat = java.text.SimpleDateFormat("MMMM yyyy", Locale("th", "TH"))
        binding.textViewDate.text = monthFormat.format(java.util.Date())
        
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // เพิ่ม android.R.style.Theme_DeviceDefault_Dialog_Alert เพื่อบังคับใช้ธีมมืดในปฏิทิน
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            android.R.style.Theme_DeviceDefault_Dialog_Alert, 
            { _, selectedYear, selectedMonth, selectedDay ->
                val dateString = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                val displayDate = String.format(Locale("th", "TH"), "%d/%d/%d", selectedDay, selectedMonth + 1, selectedYear)
                updateSpentByDate(dateString, displayDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun updateSpentByDate(dateQuery: String, displayDate: String) {
        val dbHelper = DBHelper(requireContext())
        val spentOnDate = dbHelper.getTotalSpentByDate(dateQuery)
        
        binding.textViewTodayLabel.text = "$displayDate"
        binding.textViewTodayAmount.text = "%,d".format(spentOnDate)
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        val dbHelper = DBHelper(requireContext())
        
        val currentMonthTotal = dbHelper.getTotalSpentCurrentMonth()
        val lastMonthTotal = dbHelper.getTotalSpentLastMonth()
        val diff = currentMonthTotal - lastMonthTotal

        binding.textViewMonthlyAmount.text = "%,d".format(currentMonthTotal)
        binding.textViewTodayAmount.text = "%,d".format(dbHelper.getTotalSpentToday())
        binding.textViewTodayLabel.text = "ราคาวัตถุดิบวันนี้"
        
        binding.textViewDifferenceAmount.text = if(diff >= 0) "+%,d".format(diff) else "%,d".format(diff)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
