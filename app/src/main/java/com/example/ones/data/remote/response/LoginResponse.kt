package com.example.ones.data.remote.response

data class LoginResponse(
    val message: String,
    val data: UserData
)

data class UserData(
    val user: UserDetail
)

data class UserDetail(
    val uid: String,
    val email: String,
    val emailVerified: Boolean,
    val providerData: List<ProviderData>,
    val stsTokenManager: TokenManager,
    val createdAt: String,
    val lastLoginAt: String,
    val apiKey: String,
    val appName: String
)

data class ProviderData(
    val providerId: String,
    val uid: String,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoURL: String?
)

data class TokenManager(
    val refreshToken: String,
    val accessToken: String,
    val expirationTime: Long
)
