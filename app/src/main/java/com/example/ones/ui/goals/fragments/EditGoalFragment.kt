package com.example.ones.ui.goals.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ones.data.model.Result
import com.example.ones.databinding.FragmentEditGoalBinding
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.goals.GoalsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditGoalFragment : Fragment() {
    private var _binding: FragmentEditGoalBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    private lateinit var goalsViewModel: GoalsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditGoalBinding.inflate(inflater, container, false)

        goalsViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        ).get(GoalsViewModel::class.java)

        binding.deadlineEditText.setOnClickListener {
            showDatePicker()
        }

        binding.buttonEditGoal.setOnClickListener {
            val title = binding.goalEditText.text.toString()
            val targetAmount = binding.targetAmountEditText.text.toString().toLongOrNull()
            val deadline = binding.deadlineEditText.text.toString()

            val goalId = arguments?.getString("goalId")
            if (goalId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Goal ID is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (targetAmount != null) {
                goalsViewModel.updateGoal(goalId, title, targetAmount, deadline)
            }
        }

        goalsViewModel.updateGoalResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonEditGoal.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonEditGoal.isEnabled = true
                    Toast.makeText(requireContext(), "Goal updated successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack() // Navigasi ke fragment sebelumnya
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonEditGoal.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    Log.e("UpdateGoalFragment", "Error updating goal: ${result.error}")
                }
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
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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