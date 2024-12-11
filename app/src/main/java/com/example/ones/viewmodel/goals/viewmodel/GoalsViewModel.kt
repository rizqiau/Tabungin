package com.example.ones.viewmodel.goals

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.data.model.Result
import com.example.ones.R
import com.example.ones.data.model.GoalCategory
import com.example.ones.data.repository.GoalsRepository
import com.example.ones.data.remote.response.AddGoalsResponse
import com.example.ones.data.remote.response.Goals
import com.example.ones.data.remote.response.SavingsResponse
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val goalsRepository: GoalsRepository
) : ViewModel() {

    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData untuk pesan error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // LiveData untuk menyimpan daftar goals
    private val _goalsList = MutableLiveData<List<Goals>>()
    val goalsList: LiveData<List<Goals>> get() = _goalsList

    // LiveData untuk menyimpan daftar kategori
    private val _goalCategoriesList = MutableLiveData<List<GoalCategory>>()
    val goalCategoriesList: LiveData<List<GoalCategory>> get() = _goalCategoriesList

    private val _updateGoalAmountResponse = MutableLiveData<Result<SavingsResponse>>()
    val updateGoalAmountResponse: LiveData<Result<SavingsResponse>> get() = _updateGoalAmountResponse

    private val _updateGoalResponse = MutableLiveData<Result<SavingsResponse>>()
    val updateGoalResponse: LiveData<Result<SavingsResponse>> get() = _updateGoalResponse

    // LiveData untuk menyimpan response setelah goal ditambahkan
    private val _goalResponse = MutableLiveData<AddGoalsResponse?>()
    val goalResponse: LiveData<AddGoalsResponse?> get() = _goalResponse

    // Fungsi untuk menambahkan goal
    fun addGoal(title: String, targetAmount: Long, deadline: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = goalsRepository.addGoal(title, targetAmount, deadline)
                _isLoading.value = false
                _goalResponse.value = response
                Log.d("GoalsViewModel", "Goal added successfully: ${response.message}")
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
                Log.e("GoalsViewModel", "Error adding goal", e)
            }
        }
    }

    fun updateGoalAmount(goalId: String, amount: Long) {
        _updateGoalAmountResponse.value = Result.Loading
        viewModelScope.launch {
            try {
                // Panggil fungsi di GoalsRepository
                val response = goalsRepository.updateGoalAmount(goalId, amount)
                _updateGoalAmountResponse.value = Result.Success(response)
                Log.d("GoalsViewModel", "Goal update successful: $response")
            } catch (e: Exception) {
                _updateGoalAmountResponse.value = Result.Error(e.message ?: "Unknown error")
                Log.e("GoalsViewModel", "Error updating goal amount: ${e.message}")
            }
        }
    }

    fun updateGoal(goalId: String, title: String, targetAmount: Long, deadline: String) {
        _updateGoalResponse.value = Result.Loading
        viewModelScope.launch {
            try {
                // Panggil fungsi di GoalsRepository
                val response = goalsRepository.updateGoal(goalId, title, targetAmount, deadline)
                _updateGoalResponse.value = Result.Success(response)
                Log.d("GoalsViewModel", "Goal update successful: $response")
            } catch (e: Exception) {
                _updateGoalResponse.value = Result.Error(e.message ?: "Unknown error")
                Log.e("GoalsViewModel", "Error updating goal amount: ${e.message}")
            }
        }
    }

    // Fungsi untuk mengambil goals dan memetakan menjadi GoalCategory
    fun fetchGoalsAndMapToCategories() {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val goals = goalsRepository.getGoals() // Mendapatkan data goals
                _goalsList.value = goals

                // Mapping goals menjadi GoalCategory dengan ikon statis
                val categories = goals.map { goal ->
                    GoalCategory(
                        id = goal.id,
                        title = goal.title,
                        icon = R.drawable.ic_note // Gunakan ikon statis di sini
                    )
                }

                _goalCategoriesList.value = categories
                _isLoading.value = false
                Log.d("GoalsViewModel", "Goals mapped to categories successfully")
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message
                Log.e("GoalsViewModel", "Error fetching or mapping goals", e)
            }
        }
    }
}
