package com.example.ones.data.remote.response

data class RegisterResponse(
    val message: String,
    val data: UserRegistrationData
)

data class UserRegistrationData(
    val user: User,
    val saving: Saving
)

data class User(
    val username: String,
    val email: String,
    val passwordHash: String,
    val createdAt: String,
    val updatedAt: String
)

data class Saving(
    val amount: Double,
    val createdAt: String
)
