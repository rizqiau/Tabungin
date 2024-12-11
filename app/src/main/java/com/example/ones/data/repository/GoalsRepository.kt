package com.example.ones.data.repository

import android.util.Log
import com.example.ones.data.model.UserModel
import com.example.ones.data.preferences.SavingsPreference
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.remote.request.AddGoalsRequest
import com.example.ones.data.remote.request.UpdateGoalAmountRequest
import com.example.ones.data.remote.request.UpdateGoalRequest
import com.example.ones.data.remote.response.AddGoalsResponse
import com.example.ones.data.remote.response.Goals
import com.example.ones.data.remote.response.SavingsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class GoalsRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val savingsPreference: SavingsPreference
) {
    suspend fun getGoals(): List<Goals> {
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token

        val response = apiService.getSavings(userId, "Bearer $token")
        val savingId = response.data.id
        if (savingId.isNullOrEmpty()) {
            throw Exception("SavingId not found")
        }
        val savingsResponse: SavingsResponse = apiService.getSavings(userId, "Bearer $token")
        return savingsResponse.data.goals
    }

    suspend fun addGoal(title: String, targetAmount: Long, deadline: String): AddGoalsResponse {
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token

        val response = apiService.getSavings(userId, "Bearer $token")
        val savingId = response.data.id

        val request = AddGoalsRequest(title, targetAmount, deadline)
        return apiService.addGoals(userId, savingId, request, "Bearer $token")
    }

    suspend fun updateGoalAmount(goalId: String, amount: Long): SavingsResponse {
        // Ambil userId dan token dari UserPreference
        val userId = userPreference.getSession().first().userId
        val token = userPreference.getSession().first().token

        // Ambil savingId dari SavingsPreference
        val savingId = savingsPreference.getSavingId().firstOrNull()
            ?: throw IllegalStateException("Saving ID not found in preferences")

        // Siapkan request body
        val request = UpdateGoalAmountRequest(amount)

        // Log untuk debugging
        Log.d(
            "GoalsRepository",
            "Updating goal amount: userId=$userId, savingId=$savingId, goalId=$goalId, amount=$amount"
        )

        // Panggil API
        return apiService.updateGoalAmount(userId, savingId, goalId, request, "Bearer $token")
    }

    suspend fun updateGoal(goalId: String, title: String, targetAmount: Long, deadline: String): SavingsResponse {
        val userId = userPreference.getSession().first().userId
        val token = userPreference.getSession().first().token

        val savingId = savingsPreference.getSavingId().firstOrNull()
            ?: throw IllegalStateException("Saving ID not found in preferences")

        val request = UpdateGoalRequest(title, targetAmount, deadline)

        return apiService.updateGoal(userId, savingId, goalId, request, "Bearer $token")
    }
}
