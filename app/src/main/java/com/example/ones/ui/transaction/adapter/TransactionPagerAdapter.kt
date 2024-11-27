package com.example.ones.ui.transaction.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ones.ui.transaction.fragments.TransactionIncomeFragment
import com.example.ones.ui.transaction.fragments.TransactionOutcomeFragment
import com.example.ones.ui.transaction.fragments.TransactionSavingsFragment

class TransactionPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3 // Tiga tab: Pengeluaran, Pendapatan, Tabungan

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TransactionOutcomeFragment() // Fragment untuk Pengeluaran
            1 -> TransactionIncomeFragment() // Fragment untuk Pendapatan
            2 -> TransactionSavingsFragment() // Fragment untuk Tabungan
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
