package com.example.ones.ui.goals.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ones.R
import com.example.ones.data.remote.response.Goals
import com.example.ones.databinding.ItemGoalBinding

class GoalsAdapter(
    private val onEditClicked: (goalId: String) -> Unit,
    private val onCashOutClicked: (goalId: String) -> Unit
) : ListAdapter<Goals, GoalsAdapter.GoalsViewHolder>(GOALS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalsViewHolder(binding, onEditClicked, onCashOutClicked)
    }

    override fun onBindViewHolder(holder: GoalsViewHolder, position: Int) {
        val goal = getItem(position)
        holder.bind(goal)
    }

    class GoalsViewHolder(
        private val binding: ItemGoalBinding,
        private val onEditClicked: (goalId: String) -> Unit,
        private val onCashOutClicked: (goalId: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: Goals) {
            binding.goalTitle.text = goal.title
            binding.goalTargetAmount.text = goal.targetAmount.toString()
            binding.goalAmount.text = goal.amount.toString()
            binding.goalStatus.text = goal.status
            binding.goalDeadline.text = goal.deadline

            binding.progressBar.max = goal.targetAmount.toInt()
            binding.progressBar.progress = goal.amount.toInt()

            // Set click listener untuk menampilkan popup menu
            binding.root.setOnClickListener {
                showPopupMenu(goal.id)
            }
        }

        private fun showPopupMenu(goalId: String) {
            val popupMenu = PopupMenu(binding.root.context, binding.root)
            popupMenu.menuInflater.inflate(R.menu.goal_item_menu, popupMenu.menu)

            // Handle klik menu
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit_goal -> {
                        onEditClicked(goalId)
                        true
                    }
                    R.id.action_cash_out -> {
                        onCashOutClicked(goalId)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    companion object {
        private val GOALS_COMPARATOR = object : DiffUtil.ItemCallback<Goals>() {
            override fun areItemsTheSame(oldItem: Goals, newItem: Goals): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Goals, newItem: Goals): Boolean {
                return oldItem == newItem
            }
        }
    }
}
