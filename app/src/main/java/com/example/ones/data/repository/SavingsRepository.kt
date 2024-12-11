package com.example.ones.data.repository

import android.util.Log
import com.example.ones.data.preferences.SavingsPreference
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.remote.request.AddGoalAmountRequest
import com.example.ones.data.remote.request.AddSavingsRequest
import com.example.ones.data.remote.request.ReduceSavingsRequest
import com.example.ones.data.remote.request.UpdateSavingsRequest
import com.example.ones.data.remote.response.SavingsResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class SavingsRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val savingsPreference: SavingsPreference
) {

    suspend fun getSavingsData(): SavingsResponse {
        // Ambil sesi pengguna dari UserPreference
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token

        // Validasi userId dan token
        if (userId.isEmpty() || token.isEmpty()) {
            Log.e("SavingsRepository", "Invalid session: userId or token is empty")
            throw IllegalStateException("Session data is incomplete")
        }

        // Log untuk debug
        Log.d("SavingsRepository", "Fetching savings for userId=$userId with token=$token")

        // Panggil API untuk mendapatkan data savings
        val response = apiService.getSavings(userId, "Bearer $token")

        // Ambil savingId dari response dan simpan ke SavingsPreference
        val savingId = response.data.id
        if (savingId.isNotEmpty()) {
            Log.d("SavingsRepository", "SavingId fetched from API: $savingId")
            savingsPreference.saveSavingsData(savingId) // Simpan savingId
        } else {
            Log.e("SavingsRepository", "SavingId is empty in the API response")
        }

        // Kembalikan response
        return response
    }

    suspend fun addSavings(category: String, amount: Long, description: String): SavingsResponse {
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token

        val request = AddSavingsRequest(category, amount, description)
        return apiService.addSavings(userId, request, "Bearer $token")
    }

    suspend fun reduceSavings(
        category: String,
        amount: Long,
        description: String
    ): SavingsResponse {
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token

        val request = ReduceSavingsRequest(category, amount, description)
        return apiService.reduceSavings(userId, request, "Bearer $token")
    }

    suspend fun updateSavings(
        savingId: String? = null, // Tambahkan default null jika tidak diteruskan
        transactionId: String,
        category: String,
        amount: Long,
        description: String,
        date: String
    ): SavingsResponse {
        // Ambil data sesi pengguna
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token

        if (userId.isEmpty() || token.isEmpty()) {
            Log.e("SavingsRepository", "Invalid session: userId or token is empty")
            throw IllegalStateException("Session data is incomplete")
        }

        // Ambil savingId jika tidak diberikan sebagai parameter
        val resolvedSavingId = savingId ?: savingsPreference.getSavingId().firstOrNull()
        ?: throw IllegalStateException("SavingId is missing and cannot be resolved")

        // Siapkan request body
        val request = UpdateSavingsRequest(
            category = category,
            amount = amount,
            description = description,
            date = date
        )

        // Log untuk debugging
        Log.d(
            "SavingsRepository",
            "Updating savings: userId=$userId, savingId=$resolvedSavingId, transactionId=$transactionId, request=$request"
        )

        // Panggil API untuk update savings
        return apiService.updateSavings(userId, resolvedSavingId, transactionId, request, "Bearer $token")
    }

    suspend fun deleteTransaction(transactionId: String): SavingsResponse {
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token
        val savingId = savingsPreference.getSavingId().first()

        if (savingId != null) {
            return apiService.deleteSavings(userId, savingId, transactionId, "Bearer $token")
        } else {
            throw IllegalArgumentException("Saving ID is not valid")
        }
    }

    suspend fun addGoalAmount(goalId: String, amount: Long): SavingsResponse {
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token
        val response = apiService.getSavings(userId, "Bearer $token")
        val savingId = response.data.id

        val request = AddGoalAmountRequest(amount)
        if (savingId.isNullOrEmpty()) {
            Log.e("GoalsRepository", "SavingId is null or empty")
            throw Exception("SavingId is required")
        }

        if (goalId.isNullOrEmpty()) {
            Log.e("GoalsRepository", "GoalId is null or empty")
            throw Exception("GoalId is required")
        }
        Log.d("SavingsRepository", "UserId: $userId, SavingId: $savingId, GoalId $goalId")

        return apiService.addGoalAmount(userId, savingId, goalId, request, "Bearer $token")
    }

    suspend fun getTotalAdditionsAndReductions(): Pair<Long, Long> {
        val savingsData = getSavingsData() // Panggil data dari fungsi utama
        val totalAdditions = savingsData.data.totalAdditions
        val totalReductions = savingsData.data.totalReductions

        Log.d("SavingsRepository", "Total Additions: $totalAdditions, Total Reductions: $totalReductions")
        return Pair(totalAdditions, totalReductions)
    }
}