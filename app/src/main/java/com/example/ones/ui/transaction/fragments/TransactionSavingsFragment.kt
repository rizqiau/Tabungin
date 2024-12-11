package com.example.ones.ui.transaction.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ones.R
import com.example.ones.data.model.GoalCategory
import com.example.ones.databinding.FragmentTransactionSavingsBinding
import com.example.ones.ui.transaction.adapter.CategoryAdapter
import com.example.ones.ui.transaction.adapter.GoalCategoryAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.goals.GoalsViewModel
import com.example.ones.viewmodel.transaction.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionSavingsFragment : Fragment() {
    private var _binding: FragmentTransactionSavingsBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    private lateinit var goalsViewModel: GoalsViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    private var selectedGoalId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionSavingsBinding.inflate(inflater, container, false)

        val factory = ViewModelFactory.getInstance(requireContext())
        goalsViewModel = ViewModelProvider(this, factory).get(GoalsViewModel::class.java)
        transactionViewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)

        binding.dateInput.setOnClickListener {
            showDatePicker()
        }

        setupObservers()
        setupListeners()
        // Fetch data dari API untuk mendapatkan goals
        goalsViewModel.fetchGoalsAndMapToCategories()
        return binding.root
    }

    private fun setupObservers() {
        // Observe goals untuk mengisi dropdown
        goalsViewModel.goalsList.observe(viewLifecycleOwner) { goals ->
            val goalCategories = goals.map {
                GoalCategory(
                    id = it.id,
                    title = it.title,
                    icon = R.drawable.ic_note // Icon statis
                )
            }

            val adapter = GoalCategoryAdapter(requireContext(), goalCategories)
            binding.categoryInput.setAdapter(adapter)

            // Handle pemilihan kategori
            binding.categoryInput.setOnItemClickListener { parent, _, position, _ ->
                val selectedCategory = parent.getItemAtPosition(position) as GoalCategory
                selectedGoalId = selectedCategory.id
                binding.categoryInput.setText(selectedCategory.title)
            }
        }

        // Observe response untuk penambahan goal amount
        transactionViewModel.addGoalAmountResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                Toast.makeText(requireContext(), "Goal amount added successfully!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Kembali ke fragment sebelumnya
            } ?: run {
                Toast.makeText(requireContext(), "Failed to add goal amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        // Tanggal picker
        binding.dateInput.setOnClickListener { showDatePicker() }

        // Submit button
        binding.btnSubmit.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toLongOrNull()

            if (selectedGoalId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select a goal", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil ViewModel untuk mengirimkan request
            transactionViewModel.addGoalAmount(selectedGoalId!!, amount)
        }
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