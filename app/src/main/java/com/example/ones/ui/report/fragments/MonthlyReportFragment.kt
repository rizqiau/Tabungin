package com.example.ones.ui.report.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ones.R
import com.example.ones.data.model.MonthlyReportData
import com.example.ones.databinding.FragmentMonthlyReportBinding
import com.example.ones.ui.report.adapters.TransactionAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.report.MonthlyReportViewModel
import com.example.ones.data.model.Result
import com.example.ones.ui.customview.CustomMarkerView
import com.example.ones.viewmodel.predict.PredictViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class MonthlyReportFragment : Fragment() {
    private var _binding: FragmentMonthlyReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var monthlyReportViewModel: MonthlyReportViewModel
    private lateinit var predictViewModel: PredictViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyReportBinding.inflate(inflater, container, false)

        val factory = ViewModelFactory.getInstance(requireContext())
        monthlyReportViewModel = ViewModelProvider(this, factory).get(MonthlyReportViewModel::class.java)
        predictViewModel = ViewModelProvider(this, factory).get(PredictViewModel::class.java)

        observeViewModel()
        setupListeners()
        setupRecyclerView()

        predictViewModel.predictSavings()

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
                    updateUI(result.data)
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Show loading if needed
                }
            }
        }

        // Observasi predictResult untuk menampilkan nilai rekomendasi tabungan
        predictViewModel.predictResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val recommendation = result.data.Rekomendasi_Tabungan_Bulanan_Anda
                    val alert = result.data.Alert
                    binding.nilaiRekomendasi.text = recommendation // Tampilkan rekomendasi di TextView
                    binding.alert.text = alert
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Tampilkan loading jika perlu
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
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // Mapping data ke bar chart
        for ((index, entry) in barChartData.withIndex()) {
            entries.add(BarEntry(index.toFloat(), entry.second))
            labels.add(entry.first) // Menambahkan label (kategori)
        }

        // Membuat dataset dan menyesuaikan gaya
        val dataSet = BarDataSet(entries, "Income vs Outcome").apply {
            colors = listOf(Color.BLUE, Color.RED, Color.GREEN) // Warna untuk income, outcome, savings
            setDrawValues(false) // Menyembunyikan nilai di atas bar
        }

        val barData = BarData(dataSet)

        // Menyiapkan bar chart
        binding.barChart.apply {
            data = barData

            // Set format label sumbu X
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false) // Menghilangkan garis grid horizontal
            }

            // Hilangkan sumbu Y kiri dan kanan
            axisLeft.isEnabled = false
            axisRight.isEnabled = false

            // Hilangkan deskripsi chart
            description.isEnabled = false

            // Hilangkan zoom dan scale
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            setScaleEnabled(false)

            // Aktifkan interaksi dengan marker view
            val markerView = CustomMarkerView(requireContext(), R.layout.marker_view)
            marker = markerView

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let {
                        val barValue = it.y
                        markerView.setDataValue(barValue)
                        markerView.refreshContent(e, h) // Refresh konten marker view
                    }
                }

                override fun onNothingSelected() {}
            })

            // Animasi chart
            animateY(1500, Easing.EaseInOutQuad)

            // Refresh chart
            invalidate()
        }
    }

    private fun setupPieChart(pieChartData: List<Pair<String, Float>>) {
        val entries = pieChartData.map { data ->
            PieEntry(data.second, data.first)
        }

        val dataSet = PieDataSet(entries, "Income Distribution").apply {
            colors = listOf(Color.BLUE, Color.RED, Color.GREEN) // Warna untuk income, outcome, savings
            setDrawValues(false) // Hilangkan label di atas segmen
        }

        val pieData = PieData(dataSet)

        binding.pieChart.apply {
            data = pieData

            // Hilangkan deskripsi chart
            description.isEnabled = false
            legend.isEnabled = false // Hilangkan legenda

            // Listener untuk klik segmen
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let {
                        Toast.makeText(
                            requireContext(),
                            "Value: ${it.y}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onNothingSelected() {}
            })

            invalidate() // Refresh chart
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
