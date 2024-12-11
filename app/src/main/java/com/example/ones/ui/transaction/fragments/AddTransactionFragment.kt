package com.example.ones.ui.transaction.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ones.R
import com.example.ones.databinding.FragmentAddTransactionBinding
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.FragmentTransaction

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val transactionOutcomeFragment = TransactionOutcomeFragment()
    private val transactionIncomeFragment = TransactionIncomeFragment()
    private val transactionSavingsFragment = TransactionSavingsFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Pengeluaran"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Pendapatan"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tabungan"))

        // Setup TabLayout with OnTabSelectedListener
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transactionFragment = when (tab?.position) {
                    0 -> transactionOutcomeFragment
                    1 -> transactionIncomeFragment
                    2 -> transactionSavingsFragment
                    else -> null
                }

                // Replace the current fragment with the selected fragment
                transactionFragment?.let {
                    replaceFragment(it)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Set initial fragment (optional)
        if (savedInstanceState == null) {
            replaceFragment(transactionOutcomeFragment)
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed() // Will handle going back to the previous fragment
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
