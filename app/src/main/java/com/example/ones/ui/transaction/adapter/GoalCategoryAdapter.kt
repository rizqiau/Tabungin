package com.example.ones.ui.transaction.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.ones.R
import com.example.ones.data.model.GoalCategory

class GoalCategoryAdapter(
    private val context: Context,
    private val categoryList: List<GoalCategory>
) : ArrayAdapter<GoalCategory>(context, 0, categoryList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        val category = categoryList[position]

        val categoryName = view.findViewById<TextView>(R.id.category_name)
        categoryName.text = category.title

        val categoryIcon = view.findViewById<ImageView>(R.id.category_icon)
        categoryIcon.setImageResource(category.icon)

        return view
    }
}