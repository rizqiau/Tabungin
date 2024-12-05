package com.example.ones.data.repository

import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.remote.api.ApiService
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
}