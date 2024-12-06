package com.example.ones.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ones.R
import com.example.ones.data.model.SummaryCard
import com.example.ones.databinding.ItemSummaryCardBinding
import com.google.android.material.card.MaterialCardView

class SummaryCardAdapter(
    private val items: List<SummaryCard>
) : RecyclerView.Adapter<SummaryCardAdapter.SummaryCardViewHolder>() {

    var onItemClicked: ((Int) -> Unit)? = null // Klik listener

    inner class SummaryCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cvSummaryCard)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

        fun bind(item: SummaryCard, position: Int) {
            tvTitle.text = item.title
            tvAmount.text = item.amount
            ivIcon.setImageResource(item.iconResId)

            // Update warna berdasarkan fokus
            if (item.isSelected) {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.primaryColor))
                ivIcon.setColorFilter(itemView.context.getColor(R.color.white))
                tvTitle.setTextColor(itemView.context.getColor(R.color.white))
                tvAmount.setTextColor(itemView.context.getColor(R.color.white))
            } else {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.white))
                ivIcon.setColorFilter(itemView.context.getColor(R.color.black))
                tvTitle.setTextColor(itemView.context.getColor(R.color.black))
                tvAmount.setTextColor(itemView.context.getColor(R.color.black))
            }

            // Set klik listener
            cardView.setOnClickListener {
                onItemClicked?.invoke(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_summary_card, parent, false)
        return SummaryCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: SummaryCardViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}
