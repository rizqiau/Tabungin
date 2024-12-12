package com.example.ones.ui.report.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ones.data.model.MonthlyReportData
import com.example.ones.databinding.FragmentMonthlyReportBinding
import com.example.ones.ui.report.adapters.TransactionAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.report.MonthlyReportViewModel
import com.example.ones.data.model.Result
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class MonthlyReportFragment : Fragment() {
    private var _binding: FragmentMonthlyReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var monthlyReportViewModel: MonthlyReportViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyReportBinding.inflate(inflater, container, false)

        val factory = ViewModelFactory.getInstance(requireContext())
        monthlyReportViewModel = ViewModelProvider(this, factory).get(MonthlyReportViewModel::class.java)

        observeViewModel()
        setupListeners()
        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList())
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun observeViewModel() {
        monthlyReportViewModel.reportData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    transactionAdapter.submitList(result.data.transactions)
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Show loading if needed
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnPreviousMonth.setOnClickListener {
            monthlyReportViewModel.previousMonth()
        }

        binding.btnNextMonth.setOnClickListener {
            monthlyReportViewModel.nextMonth()
        }
    }

    private fun updateUI(reportData: MonthlyReportData) {
        // Update total pemasukan, pengeluaran, dan tabungan
        binding.tvIncomeAmount.text = "Rp${reportData.totalIncome}"
        binding.tvOutcomeAmount.text = "Rp${reportData.totalOutcome}"
        binding.tvSavingsAmount.text = "Rp${reportData.savings}"

        // Update grafik batang (Bar Chart)
        setupBarChart(reportData.barChartData)

        // Update grafik lingkaran (Pie Chart)
        setupPieChart(reportData.pieChartData)

        // Update daftar transaksi
        transactionAdapter.submitList(reportData.transactions)
    }

    private fun setupBarChart(barChartData: List<Pair<String, Float>>) {
        val entries = barChartData.mapIndexed { index, data ->
            BarEntry(index.toFloat(), data.second)
        }

        val dataSet = BarDataSet(entries, "Income vs Outcome")
        val barData = BarData(dataSet)

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            setFitBars(true)
            invalidate()
        }
    }

    private fun setupPieChart(pieChartData: List<Pair<String, Float>>) {
        val entries = pieChartData.map { data ->
            PieEntry(data.second, data.first)
        }

        val dataSet = PieDataSet(entries, "Income Distribution")
        val pieData = PieData(dataSet)

        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
