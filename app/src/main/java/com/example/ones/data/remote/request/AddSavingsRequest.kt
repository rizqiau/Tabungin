package com.example.ones.data.remote.request

data class AddSavingsRequest(
    val category: String,
    val amount: Long,
    val description: String
)