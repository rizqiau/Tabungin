package com.example.ones.data.model

data class MonthlyReportData(
    val totalIncome: Long,
    val totalOutcome: Long,
    val savings: Long,
    val barChartData: List<Pair<String, Float>>,
    val pieChartData: List<Pair<String, Float>>,
    val transactions: List<TransactionItem>
)

data class TransactionItem(
    val id: String,
    val category: String,
    val amount: Long,
    val description: String,
    val date: String
)
