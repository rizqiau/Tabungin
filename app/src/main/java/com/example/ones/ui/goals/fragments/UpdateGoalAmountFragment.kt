package com.example.ones.ui.goals.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.ones.data.model.Result
import com.example.ones.databinding.FragmentUpdateGoalAmountBinding
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.goals.GoalsViewModel

class UpdateGoalAmountFragment : Fragment() {
    private var _binding: FragmentUpdateGoalAmountBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalsViewModel: GoalsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateGoalAmountBinding.inflate(inflater, container, false)

        goalsViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        ).get(GoalsViewModel::class.java)

        binding.btnSubmit.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toLongOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val goalId = arguments?.getString("goalId")
            if (goalId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Goal ID is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil ViewModel untuk update
            goalsViewModel.updateGoalAmount(goalId, amount)
        }

        goalsViewModel.updateGoalAmountResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSubmit.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "Goal updated successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack() // Navigasi ke fragment sebelumnya
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    Log.e("UpdateGoalFragment", "Error updating goal: ${result.error}")
                }
            }
        }

        return binding.root
    }
}