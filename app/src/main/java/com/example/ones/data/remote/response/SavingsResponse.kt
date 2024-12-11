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
    val reductions: List<Transaction>,
    val goals: List<Goals>
){
    // Konversi goals menjadi List<Transaction>
    fun getGoalsAsTransactions(): List<Transaction> {
        return goals.map { it.asTransaction() }
    }
}

data class Transaction(
    val id: String,
    val amount: Long,
    val category: String,
    val description: String,
    val date: TransactionDate,
    val createdAt: TransactionDate,
    val updatedAt: TransactionDate,
)

data class TransactionDate(
    val seconds: Long,
    val nanoseconds: Int
)

data class Goals(
    val id: String,
    val title: String,
    val targetAmount: Long,
    val deadline: String,
    val amount: Long,
    val status: String,
    val createdAt: TransactionDate,
    val updatedAt: TransactionDate
){
    // Konversi Goals menjadi Transaction
    fun asTransaction(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            category = "Goals", // Atur kategori sesuai kebutuhan
            description = title, // Gunakan title sebagai deskripsi
            date = createdAt, // Gunakan createdAt untuk date
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}