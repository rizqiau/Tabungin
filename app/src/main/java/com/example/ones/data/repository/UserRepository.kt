package com.example.ones.data.repository

import com.example.ones.data.model.UserModel
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.remote.request.LoginRequest
import com.example.ones.data.remote.response.LoginResponse
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun login(email: String, password: String): LoginResponse {
        val request = LoginRequest(email, password)
        return apiService.login(request)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
        }
    }
}
