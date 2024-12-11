package com.example.ones.data.repository

import com.example.ones.data.model.UserModel
import com.example.ones.data.remote.response.LoginResponse
import com.example.ones.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

interface UserRepositoryInterface {
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun register(name: String, email: String, password: String): RegisterResponse
    suspend fun saveSession(user: UserModel)
    fun getSession(): Flow<UserModel>
    suspend fun logout()
}
