package com.example.ones.ui.home.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ones.R
import com.example.ones.databinding.FragmentHomeBinding
import com.example.ones.ui.customview.CustomMarkerView
import com.example.ones.ui.home.adapters.LatestEntriesAdapter
import com.example.ones.ui.home.adapters.NewsAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.home.HomeViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var entriesAdapter: LatestEntriesAdapter
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireContext()) // Gunakan ViewModelFactory yang sudah disesuaikan
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        barChart = binding.barChart

        setupLatestEntries() // This will now use dynamic data from ViewModel
        setupNewsRecyclerView()

        observeNews()
        observeSavingsData()// Observe savings data to update LatestEntries

        return binding.root
    }

    // Setup RecyclerView for Latest Entries (Tabungan)
    private fun setupLatestEntries() {
        entriesAdapter = LatestEntriesAdapter(emptyList()) // Start with empty list
        binding.rvLatestEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLatestEntries.adapter = entriesAdapter
    }

    private fun setupNewsRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvNews.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvNews.adapter = newsAdapter
    }

    // Observing the savings data (tabungan) from ViewModel
    private fun observeSavingsData() {
        homeViewModel.latestEntries.observe(viewLifecycleOwner) { latestEntries ->
            entriesAdapter.submitList(latestEntries) // Update the RecyclerView with dynamic data
        }

        homeViewModel.refreshSavingsData()

        homeViewModel.reductionsLast7Days.observe(viewLifecycleOwner, { reductions ->
            if (reductions.isNotEmpty()) {
                displayBarChart(reductions)
            }
        })

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Tampilkan error ke user
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                Log.e("HomeViewModel", "Error Parse")
            }
        }
    }

    // Observing the news data
    private fun observeNews() {
        homeViewModel.articles.observe(viewLifecycleOwner) { articles ->
            newsAdapter.submitList(articles)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Menampilkan error ke user
                Toast.makeText(requireContext(), "Error news: $it", Toast.LENGTH_SHORT).show()
                Log.e("News", "Error Fetch News")
            }
        }
    }

    // Fungsi untuk menampilkan bar chart
    private fun displayBarChart(data: List<Pair<String, Long>>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // Mapping data ke bar chart
        for ((index, entry) in data.withIndex()) {
            entries.add(BarEntry(index.toFloat(), entry.second.toFloat()))
            labels.add(entry.first)  // Menambahkan label (tanggal)
        }

        // Membuat dataset dan memasukkan data
        val dataSet = BarDataSet(entries, "Expenses")
        dataSet.color = Color.parseColor("#FF6347") // Set warna untuk bar
        dataSet.setDrawValues(false) // Menyembunyikan nilai di atas bar

        // Data untuk chart
        val barData = BarData(dataSet)
        barChart.data = barData

        // Create custom MarkerView
        val markerView = CustomMarkerView(requireContext(), R.layout.marker_view)
        barChart.marker = markerView

        // Handle bar selection to show data label
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                // Get the selected bar value (Y-axis value)
                val barValue = e?.y ?: 0.0
                val marker = barChart.marker as CustomMarkerView
                marker.setDataValue(barValue)
                marker.refreshContent(e, h) // Refresh the marker view content
            }

            override fun onNothingSelected() {
                // No action needed when no bar is selected
            }
        })

        // Set format label sumbu X
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.setDrawGridLines(false) // Menghilangkan garis grid horizontal
        barChart.axisLeft.isEnabled = false // Menonaktifkan sumbu Y kiri
        barChart.axisRight.isEnabled = false // Menonaktifkan sumbu Y kanan

        // Menonaktifkan pinch zoom dan double-tap zoom
        barChart.setPinchZoom(false)
        barChart.isDoubleTapToZoomEnabled = false
        barChart.setScaleEnabled(false)

        // Menonaktifkan deskripsi chart
        barChart.description.isEnabled = false

        // Mengatur scrolling horizontal
        barChart.isDragEnabled = true

        // Animasi untuk chart
        barChart.animateY(1500, Easing.EaseInOutQuad)

        // Refresh chart
        barChart.invalidate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
