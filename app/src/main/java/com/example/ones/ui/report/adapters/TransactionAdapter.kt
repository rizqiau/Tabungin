package com.example.ones.ui.report.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ones.R
import com.example.ones.data.model.TransactionItem
import com.example.ones.databinding.ItemTransactionBinding
import java.text.NumberFormat

class TransactionAdapter(
    private var transactions: List<TransactionItem>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: TransactionItem) {
            binding.apply {
                tvCategory.text = transaction.category
                tvAmount.text = formatAmount(transaction.amount)
                tvDate.text = transaction.date
                tvDescription.text = transaction.description

                // Ubah warna teks berdasarkan jenis transaksi (positif/negatif)
                val color = if (transaction.amount >= 0) {
                    ContextCompat.getColor(root.context, R.color.green) // Warna untuk pemasukan
                } else {
                    ContextCompat.getColor(root.context, R.color.red) // Warna untuk pengeluaran
                }
                tvAmount.setTextColor(color)
            }
        }

        private fun formatAmount(amount: Long): String {
            return if (amount >= 0) {
                "+Rp${NumberFormat.getInstance().format(amount)}"
            } else {
                "-Rp${NumberFormat.getInstance().format(-amount)}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    fun submitList(newTransactions: List<TransactionItem>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
