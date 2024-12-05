package com.example.ones.ui.transaction.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ones.data.model.Category
import com.example.ones.databinding.FragmentTransactionIncomeBinding
import com.example.ones.ui.transaction.adapter.CategoryAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.transaction.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionIncomeFragment : Fragment() {
    private var _binding: FragmentTransactionIncomeBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionIncomeBinding.inflate(inflater, container, false)

        transactionViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext()) // Gunakan ViewModelFactory
        ).get(TransactionViewModel::class.java)

        transactionViewModel.incomeCategories.observe(viewLifecycleOwner) { categories ->
            setupCategoryDropdown(categories)
        }

        // Load kategori ketika fragment dibuat
        transactionViewModel.loadCategories()

        binding.dateInput.setOnClickListener {
            showDatePicker()
        }

        binding.btnSubmit.setOnClickListener {
            // Ambil data dari form
            val category = binding.autoCompleteCategory.text.toString()
            val amount = binding.amountEditText.text.toString().toLongOrNull()
            val description = binding.descEditText.text.toString()

            if (category.isNotEmpty() && amount != null && description.isNotEmpty()) {
                transactionViewModel.addSavings(category, amount, description)
            } else {
                Toast.makeText(context, "Form tidak lengkap", Toast.LENGTH_SHORT).show()
            }
        }

        transactionViewModel.addSavingsResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                // Berhasil menambahkan savings
                findNavController().popBackStack()
            } else {
                // Gagal menambahkan savings
                Toast.makeText(requireContext(), "Failed to add savings", Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun setupCategoryDropdown(categories: List<Category>) {
        val categoryAdapter = CategoryAdapter(requireContext(), categories)
        binding.autoCompleteCategory.setAdapter(categoryAdapter)

        binding.autoCompleteCategory.setOnItemClickListener { parent, view, position, id ->
            val selectedCategory = parent.getItemAtPosition(position) as Category
            // Menampilkan nama kategori yang dipilih di AutoCompleteTextView
            binding.autoCompleteCategory.setText(selectedCategory.name)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
