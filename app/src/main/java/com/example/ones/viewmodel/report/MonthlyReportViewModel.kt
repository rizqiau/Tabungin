package com.example.ones.viewmodel.report

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.data.repository.SavingsRepository
import com.example.ones.data.model.MonthlyReportData
import com.example.ones.data.model.Result
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MonthlyReportViewModel(private val savingsRepository: SavingsRepository) : ViewModel() {

    private val _reportData = MutableLiveData<Result<MonthlyReportData>>()
    val reportData: LiveData<Result<MonthlyReportData>> get() = _reportData

    private val _selectedMonthYear = MutableLiveData<LocalDate>()
    val selectedMonthYear: LiveData<LocalDate> get() = _selectedMonthYear

    init {
        // Inisialisasi dengan bulan dan tahun saat ini
        _selectedMonthYear.value = LocalDate.now()
        fetchReportData()
    }

    fun fetchReportData() {
        val currentMonthYear = _selectedMonthYear.value ?: LocalDate.now()
        val month = currentMonthYear.month.value
        val year = currentMonthYear.year

        _reportData.value = Result.Loading

        viewModelScope.launch {
            try {
                val report = savingsRepository.getMonthlyTransactions(month, year)
                _reportData.value = Result.Success(report)
                Log.d("MonthlyReportViewModel", "Report data fetched successfully: $report")
            } catch (e: Exception) {
                _reportData.value = Result.Error(e.message ?: "Error fetching report data")
                Log.e("MonthlyReportViewModel", "Error fetching report: ${e.message}")
            }
        }
    }

    fun nextMonth() {
        _selectedMonthYear.value = _selectedMonthYear.value?.plusMonths(1) ?: LocalDate.now().plusMonths(1)
        fetchReportData()
    }

    fun previousMonth() {
        _selectedMonthYear.value = _selectedMonthYear.value?.minusMonths(1) ?: LocalDate.now().minusMonths(1)
        fetchReportData()
    }

    fun getFormattedMonthYear(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        return _selectedMonthYear.value?.format(formatter) ?: ""
    }
}