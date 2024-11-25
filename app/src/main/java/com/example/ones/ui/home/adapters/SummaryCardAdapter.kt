package com.example.ones.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ones.R
import com.example.ones.data.model.SummaryCard
import com.example.ones.databinding.ItemSummaryCardBinding

class SummaryCardAdapter(private val items: List<SummaryCard>) :
    RecyclerView.Adapter<SummaryCardAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemSummaryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SummaryCard) {
            binding.tvTitle.text = item.title
            binding.tvAmount.text = item.amount
            binding.ivIcon.setImageResource(item.iconResId)

            // Highlight jika item dipilih
            val backgroundColor = if (item.isSelected) {
                binding.root.context.getColor(R.color.primaryColor)
            } else {
                binding.root.context.getColor(R.color.white)
            }
            binding.root.setCardBackgroundColor(backgroundColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSummaryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
