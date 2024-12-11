package com.example.ones.ui.goals.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ones.R
import com.example.ones.databinding.FragmentGoalsBinding
import com.example.ones.ui.goals.adapter.GoalsAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.goals.GoalsViewModel

class GoalsFragment : Fragment() {
    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private lateinit var goalsViewModel: GoalsViewModel
    private lateinit var goalsAdapter: GoalsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        val factory = ViewModelFactory.getInstance(requireContext())
        goalsViewModel = ViewModelProvider(this, factory).get(GoalsViewModel::class.java)

        // Setup RecyclerView with callback for popup menu
        goalsAdapter = GoalsAdapter(
            onEditClicked = { goalId ->
                navigateToEditGoal(goalId)
            },
            onCashOutClicked = { goalId ->
                navigateToCashOutGoal(goalId)
            }
        )
        binding.rvGoals.layoutManager = LinearLayoutManager(context)
        binding.rvGoals.adapter = goalsAdapter

        // Observer for loading state
        goalsViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Observer for error message
        goalsViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        // Observer for goals list
        goalsViewModel.goalsList.observe(viewLifecycleOwner, Observer { goals ->
            goals?.let {
                // Update RecyclerView with new data
                goalsAdapter.submitList(it)
            }
        })

        // Fetch goals data when fragment is created
        goalsViewModel.fetchGoalsAndMapToCategories()

        // Navigate to AddGoalFragment
        binding.buttonAddGoals.setOnClickListener {
            findNavController().navigate(R.id.action_goalsFragment_to_addGoalsFragment)
        }

        return binding.root
    }

    private fun navigateToEditGoal(goalId: String) {
        val selectedGoal = goalsViewModel.goalsList.value?.find { it.id == goalId }
        selectedGoal?.let { goal ->
            val bundle = Bundle().apply {
                putString("goalId", goal.id)
                putString("title", goal.title)
                putLong("targetAmount", goal.targetAmount)
                putString("deadline", goal.deadline)
            }
            findNavController().navigate(R.id.action_goalsFragment_to_editGoalFragment, bundle)
        } ?: run {
            Toast.makeText(requireContext(), "Goal not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToCashOutGoal(goalId: String) {
        val bundle = Bundle().apply {
            putString("goalId", goalId)
        }
        findNavController().navigate(R.id.action_goalsFragment_to_updateGoalAmountFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
