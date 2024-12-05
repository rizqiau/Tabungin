package com.example.ones.viewmodel.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.R
import com.example.ones.data.model.LatestEntry
import com.example.ones.data.remote.response.Article
import com.example.ones.data.remote.response.TransactionDate
import com.example.ones.data.repository.NewsRepository
import com.example.ones.data.repository.SavingsRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val savingsRepository: SavingsRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {

    // LiveData untuk LatestEntry (Tabungan)
    private val _latestEntries = MutableLiveData<List<LatestEntry>>()
    val latestEntries: LiveData<List<LatestEntry>> get() = _latestEntries

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

    private fun parseDateFromTransactionDate(transactionDate: TransactionDate): String {
        // Mengonversi 'seconds' ke dalam milidetik
        val milliseconds = transactionDate.seconds * 1000L + transactionDate.nanoseconds / 1000000L  // nanoseconds diubah ke milidetik

        // Membuat objek Date menggunakan hasil konversi
        val date = Date(milliseconds)

        // Menggunakan SimpleDateFormat untuk memformat menjadi string yang lebih readable
        val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return outputFormat.format(date)
    }

    fun getLast7Days(): List<String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("d", Locale.getDefault()) // Format hanya tanggal (tanpa bulan dan tahun)

        val dates = mutableListOf<String>()

        for (i in 6 downTo 0) {
            // Mengatur tanggal ke hari ini dikurangi i hari
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            dates.add(dateFormat.format(calendar.time))
        }

        return dates.reversed() // Agar urut dari hari terbaru
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
