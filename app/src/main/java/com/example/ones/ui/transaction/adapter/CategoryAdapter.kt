package com.example.ones.ui.transaction.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.example.ones.R
import com.example.ones.data.model.Category

class CategoryAdapter(
    private val context: Context,
    private val categories: List<Category>
) : BaseAdapter(), Filterable {

    private var filteredCategories: List<Category> = categories

    override fun getCount(): Int = filteredCategories.size

    override fun getItem(position: Int): Any = filteredCategories[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        val category = getItem(position) as Category
        val iconImageView: ImageView = view.findViewById(R.id.category_icon)
        val nameTextView: TextView = view.findViewById(R.id.category_name)

        iconImageView.setImageResource(category.icon)
        nameTextView.text = category.name

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }

    // Implementasi Filterable
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                if (constraint == null || constraint.isEmpty()) {
                    filterResults.values = categories
                    filterResults.count = categories.size
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    val filteredList = categories.filter {
                        it.name.toLowerCase().contains(filterPattern)
                    }
                    filterResults.values = filteredList
                    filterResults.count = filteredList.size
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    filteredCategories = results.values as List<Category>
                    notifyDataSetChanged()
                } else {
                    filteredCategories = categories
                    notifyDataSetChanged()
                }
            }
        }
    }
}
