package com.example.ones.viewmodel.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.R
import com.example.ones.data.model.LatestEntry
import com.example.ones.data.remote.response.Article
import com.example.ones.data.remote.response.Transaction
import com.example.ones.data.remote.response.TransactionDate
import com.example.ones.data.repository.NewsRepository
import com.example.ones.data.repository.SavingsRepository
import com.example.ones.data.model.Result
import com.example.ones.data.remote.request.PredictRequest
import com.example.ones.data.remote.response.PredictResponse
import com.example.ones.data.remote.response.SavingsResponse
import com.example.ones.data.repository.PredictRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val savingsRepository: SavingsRepository,
    private val newsRepository: NewsRepository,
    private val predictRepository: PredictRepository
) : ViewModel() {

    // LiveData untuk LatestEntry (Tabungan)
    private val _latestEntries = MutableLiveData<List<LatestEntry>>()
    val latestEntries: LiveData<List<LatestEntry>> get() = _latestEntries

    // LiveData untuk status delete transaction (sukses/gagal)
    private val _deleteStatus = MutableLiveData<Result<SavingsResponse>>()
    val deleteStatus: LiveData<Result<SavingsResponse>> get() = _deleteStatus

    private val _updateSavings = MutableLiveData<Result<String>>()
    val updateSavings: LiveData<Result<String>> get() = _updateSavings

    // LiveData untuk Reductions selama 7 hari terakhir
    private val _reductionsLast7Days = MutableLiveData<List<Pair<String, Long>>>()
    val reductionsLast7Days: LiveData<List<Pair<String, Long>>> get() = _reductionsLast7Days

    // LiveData untuk hasil prediksi
    private val _predictResult = MutableLiveData<Result<PredictResponse>>()
    val predictResult: LiveData<Result<PredictResponse>> get() = _predictResult

    // LiveData untuk Articles (Berita)
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> get() = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        fetchSavingsData() // Fetch data tabungan
        fetchTopHeadlines("us", "business", "e4732fddfae14b91af7072a3566a4c0b") // Fetch data berita
        predictSavings()
    }

    // Fetching savings data from API
    private fun fetchSavingsData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = savingsRepository.getSavingsData()
                if (response != null) {
                    // Gabungkan additions dan reductions
                    val allTransactionData = mutableListOf<LatestEntry>()

                    // Menggabungkan transaksi dari additions
                    response.data.additions.forEach { addition ->
                        allTransactionData.add(
                            LatestEntry(
                                iconResId = R.drawable.ic_shopping,
                                title = addition.description,
                                date = parseDateFromTransactionDate(addition.createdAt),
                                amount = "+Rp${addition.amount}",
                                transactionId = addition.id
                            )
                        )
                    }

                    // Menggabungkan transaksi dari reductions
                    response.data.reductions.forEach { reduction ->
                        allTransactionData.add(
                            LatestEntry(
                                iconResId = R.drawable.ic_shopping,
                                title = reduction.description,
                                date = parseDateFromTransactionDate(reduction.createdAt),
                                amount = "-Rp${reduction.amount}",
                                transactionId = reduction.id
                            )
                        )
                    }

                    response.data.getGoalsAsTransactions().forEach { goalTransaction ->
                        allTransactionData.add(
                            LatestEntry(
                                iconResId = R.drawable.ic_note,
                                title = goalTransaction.description,
                                date = parseDateFromTransactionDate(goalTransaction.createdAt),
                                amount = "Rp${goalTransaction.amount}",
                                transactionId = goalTransaction.id
                            )
                        )
                    }

                    // Urutkan transaksi berdasarkan waktu (terbaru di atas)
                    allTransactionData.sortByDescending {
                        Date(it.date).time // Urutkan berdasarkan waktu transaksi
                    }

                    _latestEntries.value = allTransactionData
                    Log.d("allTransactionData", "$allTransactionData.id")
                    _error.value = null

                    // Filter reductions untuk 7 hari terakhir
                    val reductionsLast7Days = filterReductionsForLast7Days(response.data.reductions)
                    _reductionsLast7Days.value = reductionsLast7Days
                    Log.d("HomeViewModel", "Reductions for the last 7 days: $reductionsLast7Days")

                } else {
                    _error.value = "Failed to fetch savings data."
                }
            } catch (e: Exception) {
                _error.value = e.message // Menampilkan pesan error ke UI
                Log.e("fetchSavingsData", "error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // HomeViewModel
    fun refreshSavingsData() {
        fetchSavingsData()  // Panggil ulang fungsi untuk mengambil data terbaru
    }

    fun deleteTransaction(transactionId: String) {
        // Set status loading ketika mulai proses penghapusan
        _deleteStatus.value = Result.Loading

        // Panggil repository untuk menghapus transaksi
        viewModelScope.launch {
            try {
                // Mengambil status dari repository
                val response = savingsRepository.deleteTransaction(transactionId)

                // Jika berhasil, set status success
                _deleteStatus.value = Result.Success(response)
            } catch (e: Exception) {
                // Jika terjadi error, set status error
                _deleteStatus.value = Result.Error(e.message ?: "Unknown error")
                Log.e("HomeViewModel", "Error deleting transaction: ${e.message}")
            }
        }
    }

    private fun filterReductionsForLast7Days(reductions: List<Transaction>): List<Pair<String, Long>> {
        val result = mutableListOf<Pair<String, Long>>()

        val today = LocalDate.now()

        val amountMap = mutableMapOf<LocalDate, Long>()

        reductions.forEach { reduction ->
            val transactionDate = Instant.ofEpochSecond(reduction.createdAt.seconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            if (transactionDate.isAfter(today.minusDays(7)) || transactionDate.isEqual(today.minusDays(7))) {
                amountMap[transactionDate] = (amountMap[transactionDate] ?: 0L) + reduction.amount
            }
        }

        for (i in 0 until 7) {
            val targetDate = today.minusDays(i.toLong())
            val amount = amountMap.getOrDefault(targetDate, 0L)
            result.add(Pair(targetDate.dayOfMonth.toString(), amount))
        }

        Log.d("HomeViewModel", "Filtered reductions: $result")
        return result
    }

    private fun parseDateFromTransactionDate(transactionDate: TransactionDate): String {
        val milliseconds = transactionDate.seconds * 1000L + transactionDate.nanoseconds / 1000000L  // nanoseconds diubah ke milidetik
        val date = Date(milliseconds)
        val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return outputFormat.format(date)
    }

    private fun fetchTopHeadlines(country: String, category: String, apiKey: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = newsRepository.getTopHeadlines(country, category, apiKey)
                _articles.value = response.articles
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun predictSavings() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Ambil data tabungan
                val savingsData = savingsRepository.getSavingsData()
                val request = PredictRequest(
                    Pendapatan_Bulanan = savingsData.data.totalAdditions,
                    Umur = 0, // Set nilai default
                    Jumlah_Tanggungan = 0, // Set nilai default
                    Sewa_Bulanan = 0, // Set nilai default
                    Pembayaran_Pinjaman_Bulanan = 0, // Set nilai default
                    Biaya_Asuransi_Bulanan = 0, // Set nilai default
                    Biaya_Bahan_Makanan_Bulanan = 0, // Set nilai default
                    Biaya_Transportasi_Bulanan = 0, // Set nilai default
                    Biaya_Makan_Di_Luar_Bulanan = 0, // Set nilai default
                    Biaya_Hiburan_Bulanan = 0, // Set nilai default
                    Biaya_Utilitas_Bulanan = 0, // Set nilai default
                    Biaya_Perawatan_Kesehatan_Bulanan = 0, // Set nilai default
                    Biaya_Pendidikan_Bulanan = 0, // Set nilai default
                    Biaya_Lain_Lain_Bulanan = savingsData.data.totalReductions // Gunakan total pengeluaran
                )

                Log.d("PredictRequest", "Request: $request")
                val response = predictRepository.predictSavings(request)
                _predictResult.value = Result.Success(response)
                Log.d("PredictResponse", "Response: $response")
            } catch (e: Exception) {
                _predictResult.value = Result.Error(e.message ?: "Unknown error")
                Log.e("PredictViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
