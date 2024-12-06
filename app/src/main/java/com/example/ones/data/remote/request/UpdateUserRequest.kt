package com.example.ones.data.remote.request

data class UpdateUserRequest(
    val username: String,
    val email: String,
    val password: String,
    val newPassword: String
)