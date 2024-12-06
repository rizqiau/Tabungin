package com.example.ones.ui.home.fragments

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
import com.example.ones.data.model.LatestEntry
import com.example.ones.data.model.SummaryCard
import com.example.ones.databinding.FragmentHomeBinding
import com.example.ones.ui.home.adapters.LatestEntriesAdapter
import com.example.ones.ui.home.adapters.NewsAdapter
import com.example.ones.ui.home.adapters.SummaryCardAdapter
import com.example.ones.utils.ViewModelFactory
import com.example.ones.viewmodel.home.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var summaryAdapter: SummaryCardAdapter
    private lateinit var entriesAdapter: LatestEntriesAdapter
    private lateinit var newsAdapter: NewsAdapter

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory.getInstance(requireContext()) // Gunakan ViewModelFactory yang sudah disesuaikan
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupSummaryCards()
        setupLatestEntries() // This will now use dynamic data from ViewModel
        setupNewsRecyclerView()

        observeNews()
        observeSavingsData() // Observe savings data to update LatestEntries

        return binding.root
    }

    private fun setupSummaryCards() {
        val summaryItems = listOf(
            SummaryCard(
                title = "Total Salary",
                amount = "$1,289.38",
                iconResId = R.drawable.ic_wallet,
                isSelected = false
            ),
            SummaryCard(
                title = "Total Expense",
                amount = "$298.16",
                iconResId = R.drawable.ic_wallet,
                isSelected = true // Highlighted
            ),
            SummaryCard(
                title = "Total Savings",
                amount = "$3,388.00",
                iconResId = R.drawable.ic_wallet,
                isSelected = false
            )
        )

        // Setup adapter with click listener
        summaryAdapter = SummaryCardAdapter(summaryItems).apply {
            onItemClicked = { position ->
                // Update the selected item
                summaryItems.forEachIndexed { index, item ->
                    item.isSelected = index == position
                }
                summaryAdapter.notifyDataSetChanged() // Refresh RecyclerView
            }
        }

        binding.rvSummaryCards.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSummaryCards.adapter = summaryAdapter
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

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Tampilkan error ke user
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
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
