package com.example.ones.data.model

data class SummaryCard(
    val title: String,
    val amount: String,
    val iconResId: Int,
    val isSelected: Boolean = false // Default tidak terpilih
)
