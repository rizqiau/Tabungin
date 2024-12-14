package com.example.ones.viewmodel.transaction

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.R
import com.example.ones.data.model.Category
import com.example.ones.data.model.LatestEntry
import com.example.ones.data.model.Result
import com.example.ones.data.remote.response.SavingsResponse
import com.example.ones.data.remote.response.TransactionDate
import com.example.ones.data.repository.SavingsRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionViewModel (
    private val savingsRepository: SavingsRepository,
) : ViewModel() {

    private val _addSavingsResponse = MutableLiveData<SavingsResponse?>()
    val addSavingsResponse: MutableLiveData<SavingsResponse?> get() = _addSavingsResponse

    private val _reduceSavingsResponse = MutableLiveData<SavingsResponse?>()
    val reduceSavingsResponse: MutableLiveData<SavingsResponse?> get() = _reduceSavingsResponse

    private val _addGoalAmountResponse = MutableLiveData<SavingsResponse?>()
    val addGoalAmountResponse: LiveData<SavingsResponse?> get() = _addGoalAmountResponse

    private val _incomeCategories = MutableLiveData<List<Category>>()
    val incomeCategories: LiveData<List<Category>> get() = _incomeCategories

    private val _outcomeCategories = MutableLiveData<List<Category>>()
    val outcomeCategories: LiveData<List<Category>> get() = _outcomeCategories

    private val _updateSavingsResponse = MutableLiveData<Result<SavingsResponse>>()
    val updateSavingsResponse: LiveData<Result<SavingsResponse>> get() = _updateSavingsResponse

    private val _latestEntries = MutableLiveData<List<LatestEntry>>()
    val latestEntries: LiveData<List<LatestEntry>> get() = _latestEntries

    private val _balance = MutableLiveData<String>()
    val balance: LiveData<String> get() = _balance

    private val _totalIncome = MutableLiveData<String>()
    val totalIncome: LiveData<String> get() = _totalIncome

    private val _totalOutcome = MutableLiveData<String>()
    val totalOutcome: LiveData<String> get() = _totalOutcome

    private val _totalSavings = MutableLiveData<String>()
    val totalSavings: LiveData<String> get() = _totalSavings

    // LiveData untuk status delete transaction (sukses/gagal)
    private val _deleteStatus = MutableLiveData<Result<SavingsResponse>>()
    val deleteStatus: LiveData<Result<SavingsResponse>> get() = _deleteStatus

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
                    _balance.value = "${response.data.amount}"

                    val totalIncomeValue = response.data.additions.sumOf { it.amount }
                    val totalOutcomeValue = response.data.reductions.sumOf { it.amount }
                    val totalSavingsValue = response.data.goals.sumOf { it.amount }

                    _totalIncome.value = "$totalIncomeValue"
                    _totalOutcome.value = "$totalOutcomeValue"
                    _totalSavings.value = "$totalSavingsValue"

                    val allTransactionData = mutableListOf<LatestEntry>()

                    // Menggabungkan transaksi dari additions
                    response.data.additions.forEach { addition ->
                        allTransactionData.add(
                            LatestEntry(
                                iconResId = R.drawable.coins,
                                title = addition.description,
                                date = parseDateFromTransactionDate(addition.createdAt),
                                amount = "+Rp${addition.amount}",
                                transactionId = addition.id,
                                category = addition.category,
                            )
                        )
                    }

                    // Menggabungkan transaksi dari reductions
                    response.data.reductions.forEach { reduction ->
                        allTransactionData.add(
                            LatestEntry(
                                iconResId = R.drawable.loss,
                                title = reduction.description,
                                date = parseDateFromTransactionDate(reduction.createdAt),
                                amount = "-Rp${reduction.amount}",
                                transactionId = reduction.id,
                                category = reduction.category,
                            )
                        )
                    }

                    response.data.getGoalsAsTransactions().forEach { goalTransaction ->
                        allTransactionData.add(
                            LatestEntry(
                                iconResId = R.drawable.money,
                                title = goalTransaction.description,
                                date = parseDateFromTransactionDate(goalTransaction.createdAt),
                                amount = "Rp${goalTransaction.amount}",
                                transactionId = goalTransaction.id,
                                category = goalTransaction.description,
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
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun formatCurrency(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(amount).replace("Rp", "Rp ")
    }

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

    fun loadCategories() {
        // Misalnya, kategori untuk penghasilan
        _incomeCategories.value = listOf(
            Category("Salary", R.drawable.ic_card),
            Category("Investments", R.drawable.ic_investments),
            Category("Bonus", R.drawable.ic_bonus)
        )

        // Kategori untuk pengeluaran
        _outcomeCategories.value = listOf(
            Category("Parents", R.drawable.ic_family),
            Category("Shopping", R.drawable.ic_shopping),
            Category("Food", R.drawable.ic_food),
            Category("Phone", R.drawable.ic_gas),
            Category("Entertainment", R.drawable.ic_transportation),
            Category("Education", R.drawable.ic_note),
            Category("Beauty", R.drawable.ic_entertainment),
            Category("Sports", R.drawable.ic_beauty),
            Category("Social", R.drawable.ic_health),
            Category("Transportations", R.drawable.ic_education),
            Category("Clothing", R.drawable.ic_education),
            Category("Car", R.drawable.ic_education),
            Category("Alcohol", R.drawable.ic_education),
            Category("Cigarettes", R.drawable.ic_technology),
            Category("Electronics", R.drawable.ic_investments),
            Category("Travel", R.drawable.ic_education),
            Category("Health", R.drawable.ic_education),
            Category("Pets", R.drawable.ic_education),
            Category("Repairs", R.drawable.ic_education),
            Category("Housing", R.drawable.ic_education),
            Category("Home", R.drawable.ic_education),
            Category("Gifts", R.drawable.ic_education),
            Category("Donations", R.drawable.ic_education),
            Category("Lottery", R.drawable.ic_education),
            Category("Snacks", R.drawable.ic_education),
            Category("Kids", R.drawable.ic_education),
            Category("Vegetables", R.drawable.ic_education),
            Category("Fruits", R.drawable.ic_education),
            )
    }

    // Fungsi untuk memvalidasi kategori sebelum melakukan transaksi
    fun isValidCategory(category: String, isIncome: Boolean): Boolean {
        val categories = if (isIncome) {
            incomeCategories.value
        } else {
            outcomeCategories.value
        }

        return categories?.any { it.name == category } == true
    }

    fun addSavings(category: String, amount: Long, description: String) {
        // Menyimpan data tabungan yang baru ke dalam SavingsRepository
        viewModelScope.launch {
            _isLoading.value = true // Set loading state
            try {
                // Memanggil fungsi addSavings di repository
                val response = savingsRepository.addSavings(category, amount, description)

                // Mengubah response menjadi data yang dibutuhkan
                if (response != null) {
                    _addSavingsResponse.value = response // Menyimpan hasil response
                    // Menampilkan pesan sukses atau update UI lainnya jika perlu
                } else {
                    // Handle failure jika response kosong atau gagal
                    _addSavingsResponse.value = null
                    _error.value = "Gagal menambahkan tabungan."
                }
            } catch (e: Exception) {
                // Menangani error jika ada masalah dengan API atau koneksi
                _addSavingsResponse.value = null
                _error.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false // Menghentikan loading state
            }
        }
    }

    fun reduceSavings(category: String, amount: Long, description: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = savingsRepository.reduceSavings(category, amount, description)
                _reduceSavingsResponse.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error reduce savings: ${e.message}"
                _reduceSavingsResponse.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSavings(
        savingId: String? = null, // Buat savingId opsional
        transactionId: String,
        category: String,
        amount: Long,
        description: String,
        date: String
    ) {
        // Set status loading sebelum memulai update
        _updateSavingsResponse.value = Result.Loading

        // Melakukan operasi update dalam coroutine
        viewModelScope.launch {
            try {
                // Memanggil fungsi updateSavings di SavingsRepository
                val response = savingsRepository.updateSavings(
                    savingId = savingId, // Bisa null jika tidak diteruskan
                    transactionId = transactionId,
                    category = category,
                    amount = amount,
                    description = description,
                    date = date
                )

                // Jika berhasil, set status success dengan response
                _updateSavingsResponse.value = Result.Success(response)
                Log.d("TransactionViewModel", "Update successful: $response")
            } catch (e: Exception) {
                // Jika ada error, set status error dengan pesan error
                _updateSavingsResponse.value = Result.Error(e.message ?: "Unknown error")
                Log.e("TransactionViewModel", "Error updating savings: ${e.message}")
            }
        }
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

    fun addGoalAmount(goalId: String, amount: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = savingsRepository.addGoalAmount(goalId, amount)
                _addGoalAmountResponse.value = response
                _error.value = null
                fetchSavingsData()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}