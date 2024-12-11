package com.example.ones.ui.transaction.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ones.data.model.Category
import com.example.ones.data.model.Result
import com.example.ones.databinding.FragmentUpdateTransactionBinding
import com.example.ones.ui.transaction.adapter.CategoryAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.transaction.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class UpdateTransactionFragment : Fragment() {
    private var _binding: FragmentUpdateTransactionBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    private lateinit var transactionViewModel: TransactionViewModel

    private var transactionId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateTransactionBinding.inflate(inflater, container, false)

        transactionViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext()) // Gunakan ViewModelFactory
        ).get(TransactionViewModel::class.java)

        transactionId = arguments?.getString("transactionId")
        Log.d("UpdateTransactionFragment", "transactionId: $transactionId")

        transactionViewModel.incomeCategories.observe(viewLifecycleOwner) { categories ->
            setupCategoryDropdown(categories)
        }

        transactionViewModel.outcomeCategories.observe(viewLifecycleOwner) { categories ->
            setupCategoryDropdown(categories)
        }

        transactionViewModel.loadCategories()

        binding.dateInput.setOnClickListener {
            showDatePicker()
        }

        binding.btnSubmit.setOnClickListener {
            onSubmit()
        }

        observeViewModel()

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
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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

    private fun onSubmit() {
        // Ambil input pengguna
        val category = binding.autoCompleteCategory.text.toString()
        val amountText = binding.amountEditText.text.toString()
        val description = binding.descEditText.text.toString()
        val date = binding.dateInput.text.toString()

        // Validasi input
        if (category.isEmpty() || amountText.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toLongOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        // Pastikan transactionId tersedia
        val transactionId = this.transactionId
        if (transactionId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Transaction ID is missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Panggil fungsi updateSavings di ViewModel
        transactionViewModel.updateSavings(
            transactionId = transactionId,
            category = category,
            amount = amount,
            description = description,
            date = date
        )
    }

    private fun observeViewModel() {
        transactionViewModel.updateSavingsResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    // Tampilkan loading indicator
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSubmit.isEnabled = false
                }
                is Result.Success -> {
                    // Sembunyikan loading dan tampilkan pesan sukses
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "Transaction updated successfully", Toast.LENGTH_SHORT).show()
                    // Kembali ke fragment sebelumnya
                    parentFragmentManager.popBackStack()
                }
                is Result.Error -> {
                    // Sembunyikan loading dan tampilkan pesan error
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    Log.e("UpdateTransactionFragment", "Error updating transaction: ${result.error}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}