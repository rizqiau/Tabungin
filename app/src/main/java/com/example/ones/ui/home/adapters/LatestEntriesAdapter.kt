package com.example.ones.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ones.data.model.LatestEntry
import com.example.ones.databinding.ItemLatestEntryBinding

class LatestEntriesAdapter(private val items: List<LatestEntry>) :
    RecyclerView.Adapter<LatestEntriesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLatestEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LatestEntry) {
            binding.ivIcon.setImageResource(item.iconResId)
            binding.tvTitle.text = item.title
            binding.tvDate.text = item.date
            binding.tvAmount.text = item.amount
            binding.tvPaymentInfo.text = item.paymentInfo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLatestEntryBinding.inflate(
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