package com.example.ones.viewmodel.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.R
import com.example.ones.data.model.LatestEntry
import com.example.ones.data.repository.SavingsRepository
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

class TransactionViewModel (
    private val savingsRepository: SavingsRepository,
) : ViewModel() {

    private val _latestEntries = MutableLiveData<List<LatestEntry>>()
    val latestEntries: LiveData<List<LatestEntry>> get() = _latestEntries

    private val _balance = MutableLiveData<String>()
    val balance: LiveData<String> get() = _balance

    private val _totalIncome = MutableLiveData<String>()
    val totalIncome: LiveData<String> get() = _totalIncome

    private val _totalOutcome = MutableLiveData<String>()
    val totalOutcome: LiveData<String> get() = _totalOutcome

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        fetchSavingsData()
        }

    private fun fetchSavingsData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = savingsRepository.getSavingsData()
                if (response != null) {
                    _balance.value = "$${response.data.amount}"

                    val totalIncomeValue = response.data.additions.sumOf { it.amount }
                    val totalOutcomeValue = response.data.reductions.sumOf { it.amount }

                    _totalIncome.value = "$$totalIncomeValue"
                    _totalOutcome.value = "$$totalOutcomeValue"

                    val allTransactions = mutableListOf<LatestEntry>()

                    response.data.additions.forEach { addition ->
                        allTransactions.add(
                            LatestEntry(
                                iconResId = R.drawable.ic_shopping,
                                title = addition.description,
                                date = DateFormat.getDateInstance().format(Date(addition.createdAt.seconds * 1000L)),
                                amount = "+$${addition.amount}"
                            )
                        )
                    }

                    response.data.reductions.forEach { reduction ->
                        allTransactions.add(
                            LatestEntry(
                                iconResId = R.drawable.ic_shopping,
                                title = reduction.description,
                                date = DateFormat.getDateInstance().format(Date(reduction.createdAt.seconds * 1000L)),
                                amount = "-$${reduction.amount}"
                            )
                        )
                    }


                    _latestEntries.value = allTransactions
                    _error.value = null
                } else {
                    _error.value = "Failed to fetch savings data."
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}