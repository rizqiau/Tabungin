package com.example.ones.data.remote.request

data class UpdateSavingsRequest(
    val category: String,
    val amount: Long,
    val description: String,
    val date: String
)
