package com.example.ones.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.ones.data.model.LatestEntry
import com.example.ones.databinding.ItemLatestEntryBinding

class LatestEntriesAdapter(
    private var items: List<LatestEntry>,
    private val onEditClick: (LatestEntry) -> Unit,
    private val onDeleteClick: (transactionId: String) -> Unit
    ) : RecyclerView.Adapter<LatestEntriesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemLatestEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LatestEntry) {
            binding.ivIcon.setImageResource(item.iconResId)
            binding.tvTitle.text = item.title
            binding.tvDate.text = item.date
            binding.tvAmount.text = item.amount

            binding.root.setOnClickListener {
                showPopupMenu(it, item)
            }
        }

        private fun showPopupMenu(view: android.view.View, item: LatestEntry) {
            val popupMenu = PopupMenu(view.context, view)
            val inflater = popupMenu.menuInflater
            inflater.inflate(com.example.ones.R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.itemId) {
                    com.example.ones.R.id.option_edit -> {
                        onEditClick(item) // Mengirimkan objek LatestEntry
                        true
                    }
                    com.example.ones.R.id.option_delete -> {
                        onDeleteClick(item.transactionId)
                        true
                    }
                    else -> false
                }
            }
            // Menampilkan PopupMenu
            popupMenu.show()
        }
    }

    // Menambahkan fungsi untuk memperbarui data di adapter
    fun submitList(newItems: List<LatestEntry>) {
        items = newItems
        notifyDataSetChanged()  // Memberitahu adapter untuk memperbarui tampilan
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
