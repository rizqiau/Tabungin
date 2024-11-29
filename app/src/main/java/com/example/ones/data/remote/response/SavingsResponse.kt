package com.example.ones.data.remote.response

data class SavingsResponse(
    val message: String,
    val data: SavingsData
)

data class SavingsData(
    val id: String,
    val userId: String,
    val amount: Long,
    val totalAdditions: Long,
    val totalReductions: Long,
    val additions: List<Transaction>,
    val reductions: List<Transaction>
)

data class Transaction(
    val id: String,
    val amount: Long,
    val description: String,
    val createdAt: TransactionDate
)

data class TransactionDate(
    val seconds: Long,
    val nanoseconds: Int
)