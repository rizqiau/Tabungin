package com.example.ones.ui.goals.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ones.databinding.FragmentAddGoalsBinding
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.goals.GoalsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddGoalsFragment : Fragment() {
    private var _binding: FragmentAddGoalsBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    private lateinit var goalsViewModel: GoalsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireContext())
        goalsViewModel = ViewModelProvider(this, factory).get(GoalsViewModel::class.java)
        _binding = FragmentAddGoalsBinding.inflate(inflater, container, false)

        binding.deadlineEditText.setOnClickListener {
            showDatePicker()
        }

        // Observasi data error
        goalsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                // Tampilkan pesan error
            }
        }

        // Observasi response setelah goal ditambahkan
        goalsViewModel.goalResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                // Tampilkan data goal yang berhasil ditambahkan
                Log.d("Add Goals Response", "$response")
                findNavController().popBackStack()
            }
        }

        binding.buttonAddGoal.setOnClickListener {
            val title = binding.goalEditText.text.toString()
            val targetAmount = binding.targetAmountEditText.text.toString().toLongOrNull() ?: 0L
            val deadline = binding.deadlineEditText.text.toString()

            // Panggil addGoal
            goalsViewModel.addGoal(title, targetAmount, deadline)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed() // Will handle going back to the previous fragment
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
                binding.deadlineEditText.setText(selectedDate)
            },
            year,
            month,
            day
        )

        // Tampilkan dialog
        datePickerDialog.show()
    }
}