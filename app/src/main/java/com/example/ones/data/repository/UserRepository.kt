package com.example.ones.data.repository

import android.util.Log
import com.example.ones.data.model.UserModel
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.remote.request.LoginRequest
import com.example.ones.data.remote.request.RegisterRequest
import com.example.ones.data.remote.response.LoginResponse
import com.example.ones.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) : UserRepositoryInterface {

    override suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(LoginRequest(email, password))
    }

    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.registerUser(RegisterRequest(name, email, password))
    }

    override suspend fun saveSession(user: UserModel) {
        Log.d("UserRepository", "Saving user session: userId=${user.userId}, token=${user.token}")
        userPreference.saveSession(user)
    }

    override fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    override suspend fun logout() {
        userPreference.logout()
    }
}
