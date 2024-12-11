package com.example.ones.data.remote.request

data class PredictRequest(
    val Pendapatan_Bulanan: Long,
    val Umur: Int = 0,
    val Jumlah_Tanggungan: Int = 0,
    val Sewa_Bulanan: Long = 0,
    val Pembayaran_Pinjaman_Bulanan: Long = 0,
    val Biaya_Asuransi_Bulanan: Long = 0,
    val Biaya_Bahan_Makanan_Bulanan: Long = 0,
    val Biaya_Transportasi_Bulanan: Long = 0,
    val Biaya_Makan_Di_Luar_Bulanan: Long = 0,
    val Biaya_Hiburan_Bulanan: Long = 0,
    val Biaya_Utilitas_Bulanan: Long = 0,
    val Biaya_Perawatan_Kesehatan_Bulanan: Long = 0,
    val Biaya_Pendidikan_Bulanan: Long = 0,
    val Biaya_Lain_Lain_Bulanan: Long
)
