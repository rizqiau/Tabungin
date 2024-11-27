package com.example.ones.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.R
import com.example.ones.data.model.LatestEntry
import com.example.ones.data.remote.response.Article
import com.example.ones.data.repository.NewsRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _latestEntries = MutableLiveData<List<LatestEntry>>().apply {
        value = listOf(
            LatestEntry(
                iconResId = R.drawable.ic_shopping,
                title = "Food",
                date = "20 Feb 2024",
                amount = "+ $20 + Vat 0.5%",
                paymentInfo = "Google Pay"
            ),
            LatestEntry(
                iconResId = R.drawable.ic_shopping,
                title = "Uber",
                date = "13 Mar 2024",
                amount = "- $18 + Vat 0.8%",
                paymentInfo = "Cash"
            ),
            LatestEntry(
                iconResId = R.drawable.ic_shopping,
                title = "Shopping",
                date = "11 Mar 2024",
                amount = "- $400 + Vat 0.12%",
                paymentInfo = "Paytm"
            )
        )
    }
    val latestEntries: LiveData<List<LatestEntry>> = _latestEntries

    private val repository = NewsRepository()

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> get() = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        fetchTopHeadlines("us", "business", "e4732fddfae14b91af7072a3566a4c0b")
    }

    private fun fetchTopHeadlines(country: String, category: String, apiKey: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getTopHeadlines(country, category, apiKey)
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
