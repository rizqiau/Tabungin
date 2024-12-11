package com.example.ones.data.model

data class LatestEntry(
    val transactionId: String,
    val category: String,
    val iconResId: Int,
    val title: String,
    val date: String,
    val amount: String,
    val color: Int
)

