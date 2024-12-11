package com.example.ones.ui.transaction.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ones.R
import com.example.ones.data.model.Result
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
        entriesAdapter = LatestEntriesAdapter(emptyList(),
            onDeleteClick = { transactionId ->
                // Memanggil ViewModel untuk delete transaksi
                transactionViewModel.deleteTransaction(transactionId)
            },
            onEditClick = { transactionId ->
                navigateToUpdateTransaction(transactionId)
            }
        )

        binding.rvLatestEntries.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = entriesAdapter
        }
    }

    private fun navigateToUpdateTransaction(transactionId: String) {
        val bundle = Bundle().apply {
            putString("transactionId", transactionId)
        }
        findNavController().navigate(R.id.updateTransactionFragment, bundle)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

        transactionViewModel.totalSavings.observe(viewLifecycleOwner) { totalSavings ->
            binding.cvTotalTransaction.tvSavingsAmount.text = totalSavings
        }

        transactionViewModel.deleteStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    transactionViewModel.refreshSavingsData()
                }
                is Result.Error -> {
                    showLoading(false)
                }
            }
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