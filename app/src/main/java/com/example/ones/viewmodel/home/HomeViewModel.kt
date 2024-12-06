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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val savingsRepository: SavingsRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {

    // LiveData untuk LatestEntry (Tabungan)
    private val _latestEntries = MutableLiveData<List<LatestEntry>>()
    val latestEntries: LiveData<List<LatestEntry>> get() = _latestEntries

    // LiveData untuk Reductions selama 7 hari terakhir
    private val _reductionsLast7Days = MutableLiveData<List<Pair<String, Long>>>()
    val reductionsLast7Days: LiveData<List<Pair<String, Long>>> get() = _reductionsLast7Days

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
                                amount = "+Rp${addition.amount}"
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
                                amount = "-Rp${reduction.amount}"
                            )
                        )
                    }

                    // Urutkan transaksi berdasarkan waktu (terbaru di atas)
                    allTransactionData.sortByDescending {
                        Date(it.date).time // Urutkan berdasarkan waktu transaksi
                    }

                    _latestEntries.value = allTransactionData
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

    // Fungsi untuk memfilter reductions dalam 7 hari terakhir
    private fun filterReductionsForLast7Days(reductions: List<Transaction>): List<Pair<String, Long>> {
        val result = mutableListOf<Pair<String, Long>>()

        // Ambil tanggal hari ini
        val today = LocalDate.now()

        // Buat map untuk menyimpan hasil pengurangan per tanggal
        val amountMap = mutableMapOf<LocalDate, Long>()

        // Iterasi data reductions dan simpan pengurangan berdasarkan tanggal
        reductions.forEach { reduction ->
            // Konversi detik UNIX ke ZonedDateTime dengan zona waktu lokal
            val transactionDate = Instant.ofEpochSecond(reduction.createdAt.seconds)
                .atZone(ZoneId.systemDefault()) // Gunakan zona waktu default (lokal)
                .toLocalDate() // Ambil hanya tanggalnya

            // Jika tanggal transaksi ada dalam 7 hari terakhir, simpan ke amountMap
            if (transactionDate.isAfter(today.minusDays(7)) || transactionDate.isEqual(today.minusDays(7))) {
                amountMap[transactionDate] = (amountMap[transactionDate] ?: 0L) + reduction.amount
            }
        }

        // Isi hasil yang di-filter ke dalam list result
        for (i in 0 until 7) {
            val targetDate = today.minusDays(i.toLong())
            val amount = amountMap.getOrDefault(targetDate, 0L)
            result.add(Pair(targetDate.dayOfMonth.toString(), amount))
        }

        Log.d("HomeViewModel", "Filtered reductions: $result")
        return result
    }

    private fun parseDateFromTransactionDate(transactionDate: TransactionDate): String {
        // Mengonversi 'seconds' ke dalam milidetik
        val milliseconds = transactionDate.seconds * 1000L + transactionDate.nanoseconds / 1000000L  // nanoseconds diubah ke milidetik

        // Membuat objek Date menggunakan hasil konversi
        val date = Date(milliseconds)

        // Menggunakan SimpleDateFormat untuk memformat menjadi string yang lebih readable
        val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return outputFormat.format(date)
    }

    // Fetching top headlines (News) from API
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
}
