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
import com.db.williamchart.view.BarChartView
import com.example.ones.databinding.FragmentHomeBinding
import com.example.ones.ui.home.adapters.LatestEntriesAdapter
import com.example.ones.ui.home.adapters.NewsAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.home.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var entriesAdapter: LatestEntriesAdapter
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var barChart: BarChartView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireContext()) // Gunakan ViewModelFactory yang sudah disesuaikan
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.barChart

        setupLatestEntries() // This will now use dynamic data from ViewModel
        setupNewsRecyclerView()

        observeNews()
        observeSavingsData() // Observe savings data to update LatestEntries

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
