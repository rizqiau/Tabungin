package com.example.ones.data.remote.request

data class ReduceSavingsRequest (
    val category: String,
    val amount: Long,
    val description: String
)