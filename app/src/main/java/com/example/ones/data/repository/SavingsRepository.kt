package com.example.ones.data.repository

import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.remote.request.AddSavingsRequest
import com.example.ones.data.remote.request.ReduceSavingsRequest
import com.example.ones.data.remote.response.SavingsResponse
import kotlinx.coroutines.flow.first

class SavingsRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    // Function untuk mengambil data savings
    suspend fun getSavingsData(): SavingsResponse {
        // Mengambil session user dari DataStore
        val userModel = userPreference.getSession().first()  // Mengambil session pertama

        // Ambil userId dan token
        val userId = userModel.userId
        val token = userModel.token

        return apiService.getSavings(userId, "Bearer $token")
    }

    suspend fun addSavings(category: String, amount: Long, description: String): SavingsResponse {
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token

        val request = AddSavingsRequest(category, amount, description)
        return apiService.addSavings(userId, request, "Bearer $token")
    }

    suspend fun reduceSavings(category: String, amount: Long, description: String): SavingsResponse {
        val userModel = userPreference.getSession().first()
        val userId = userModel.userId
        val token = userModel.token

        val request = ReduceSavingsRequest(category, amount, description)
        return apiService.reduceSavings(userId, request, "Bearer $token")
    }
}