package com.example.ones.ui.transaction.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.example.ones.R
import com.example.ones.databinding.FragmentAddTransactionBinding
import com.example.ones.databinding.FragmentTransactionOutcomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionOutcomeFragment : Fragment() {
    private var _binding: FragmentTransactionOutcomeBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionOutcomeBinding.inflate(inflater, container, false)

        val category = listOf("Shop", "Health", "Food")
        val categoryInput : AutoCompleteTextView = binding.categoryInput
        val adapter = ArrayAdapter(requireContext(), R.layout.item_category, category)

        categoryInput.setAdapter(adapter)

        categoryInput.setOnItemClickListener { adapterView, view, i, l ->
            val categorySelected = adapterView.getItemAtPosition(i)
            if (isAdded) { // Pastikan fragment terhubung ke aktivitas
                Toast.makeText(requireContext(), "Item : $categorySelected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.dateInput.setOnClickListener {
            showDatePicker()
        }
        binding.btnSubmit

        return binding.root
    }

    private fun showDatePicker() {
        // Ambil tanggal saat ini
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Buat DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Perbarui Calendar dengan tanggal yang dipilih
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Format tanggal
                val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                val selectedDate = format.format(calendar.time)

                // Tampilkan tanggal di dateInput
                binding.dateInput.setText(selectedDate)
            },
            year,
            month,
            day
        )

        // Tampilkan dialog
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}