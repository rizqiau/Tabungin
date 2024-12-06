package com.example.ones.ui.transaction.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ones.R
import com.example.ones.databinding.FragmentTransactionBinding
import com.example.ones.ui.home.adapters.LatestEntriesAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.transaction.TransactionViewModel

class TransactionFragment : Fragment() {
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var entriesAdapter: LatestEntriesAdapter
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireContext())
        transactionViewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        setupLatestEntries()

        observeSavingsData()

        return binding.root
    }

    private fun setupLatestEntries() {
        entriesAdapter = LatestEntriesAdapter(emptyList()) // Start with empty list
        binding.rvLatestEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLatestEntries.adapter = entriesAdapter
    }

    private fun observeSavingsData() {
        transactionViewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.tvBalance.text = balance
        }

        transactionViewModel.totalIncome.observe(viewLifecycleOwner) { totalIncome ->
            binding.cvTotalTransaction.tvIncomeAmount.text = totalIncome
        }

        transactionViewModel.totalOutcome.observe(viewLifecycleOwner) { totalOutcome ->
            binding.cvTotalTransaction.tvExpenseAmount.text = totalOutcome
        }

        transactionViewModel.latestEntries.observe(viewLifecycleOwner) { latestEntries ->
            entriesAdapter.submitList(latestEntries)
        }

        transactionViewModel.refreshSavingsData()

        transactionViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        transactionViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}