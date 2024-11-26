package com.example.ones.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ones.R
import com.example.ones.data.model.LatestEntry
import com.example.ones.data.model.SummaryCard
import com.example.ones.databinding.FragmentHomeBinding
import com.example.ones.ui.home.adapters.LatestEntriesAdapter
import com.example.ones.ui.home.adapters.SummaryCardAdapter
import com.example.ones.viewmodel.home.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var summaryAdapter: SummaryCardAdapter
    private lateinit var entriesAdapter: LatestEntriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupSummaryCards()
        setupLatestEntries()

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
                title = "Monthly Expense",
                amount = "$3,388.00",
                iconResId = R.drawable.ic_wallet,
                isSelected = false
            )
        )

        summaryAdapter = SummaryCardAdapter(summaryItems)
        binding.rvSummaryCards.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSummaryCards.adapter = summaryAdapter
    }

    private fun setupLatestEntries() {
        val latestEntries = listOf(
            LatestEntry(
                iconResId = R.drawable.ic_shopping,
                title = "Food",
                date = "20 Feb 2024",
                amount = "+ $20 + Vat 0.5%",
                paymentInfo = "Google Pay"
            ),
            LatestEntry(
                iconResId = R.drawable.ic_shopping,
                title = "Uber",
                date = "13 Mar 2024",
                amount = "- $18 + Vat 0.8%",
                paymentInfo = "Cash"
            ),
            LatestEntry(
                iconResId = R.drawable.ic_shopping,
                title = "Shopping",
                date = "11 Mar 2024",
                amount = "- $400 + Vat 0.12%",
                paymentInfo = "Paytm"
            )
        )

        entriesAdapter = LatestEntriesAdapter(latestEntries)
        binding.rvLatestEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLatestEntries.adapter = entriesAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}