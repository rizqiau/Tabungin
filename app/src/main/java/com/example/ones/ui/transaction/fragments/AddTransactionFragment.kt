package com.example.ones.ui.transaction.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ones.R
import com.example.ones.databinding.FragmentAddTransactionBinding
import com.example.ones.ui.transaction.adapter.TransactionPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        // Setup ViewPager2 with TabLayout
        val adapter = TransactionPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Pengeluaran"
                1 -> tab.text = "Pendapatan"
                2 -> tab.text = "Tabungan"
            }
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
