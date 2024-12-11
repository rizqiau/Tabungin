package com.example.ones.data.model

data class UserModel(
    val email: String,
    val token: String,
    val userId: String,
    val isLogin: Boolean = false
)
