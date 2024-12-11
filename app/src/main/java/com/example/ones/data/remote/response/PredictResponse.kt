package com.example.ones.data.remote.response

data class PredictResponse(
    val Alert: String,
    val Detail_Keuangan_Anda: FinancialDetails,
    val Rekomendasi_Tabungan_Bulanan_Anda: String,
    val Status_Keuangan: String
)

data class FinancialDetails(
    val Sisa_Pendapatan_Setelah_Pengeluaran: String,
    val Total_Pendapatan_Bulanan: String,
    val Total_Pengeluaran_Bulanan: String
)
