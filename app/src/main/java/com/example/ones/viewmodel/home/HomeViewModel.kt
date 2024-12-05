package com.example.ones.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.R
import com.example.ones.data.model.LatestEntry
import com.example.ones.data.remote.response.Article
import com.example.ones.data.repository.NewsRepository
import com.example.ones.data.repository.SavingsRepository
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

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
                    val allTransactions = mutableListOf<LatestEntry>()

                    // Mapping untuk additions
                    response.data.additions.forEach { addition ->
                        allTransactions.add(
                            LatestEntry(
                                iconResId = R.drawable.ic_shopping,  // Ganti sesuai dengan ikon yang sesuai
                                title = addition.description,
                                date = DateFormat.getDateInstance().format(Date(addition.createdAt.seconds * 1000L)),
                                amount = "+$${addition.amount}"  // Penambahan
                            )
                        )
                    }

                    // Mapping untuk reductions
                    response.data.reductions.forEach { reduction ->
                        allTransactions.add(
                            LatestEntry(
                                iconResId = R.drawable.ic_shopping,  // Ganti sesuai dengan ikon yang sesuai
                                title = reduction.description,
                                date = DateFormat.getDateInstance().format(Date(reduction.createdAt.seconds * 1000L)),
                                amount = "-$${reduction.amount}"  // Pengurangan
                            )
                        )
                    }

                    // Set data transaksi yang sudah digabungkan
                    _latestEntries.value = allTransactions
                    _error.value = null
                } else {
                    _error.value = "Failed to fetch savings data."
                }
            } catch (e: Exception) {
                _error.value = e.message // Menampilkan pesan error ke UI
            } finally {
                _isLoading.value = false
            }
        }
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
