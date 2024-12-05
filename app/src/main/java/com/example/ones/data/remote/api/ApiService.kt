package com.example.ones.data.remote.api

import com.example.ones.data.remote.request.AddSavingsRequest
import com.example.ones.data.remote.request.LoginRequest
import com.example.ones.data.remote.request.ReduceSavingsRequest
import com.example.ones.data.remote.request.RegisterRequest
import com.example.ones.data.remote.response.LoginResponse
import com.example.ones.data.remote.response.NewsResponse
import com.example.ones.data.remote.response.RegisterResponse
import com.example.ones.data.remote.response.SavingsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): NewsResponse

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @GET("savings/{userId}")
    suspend fun getSavings(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): SavingsResponse

    @PUT("savings/{userId}/add")
    suspend fun addSavings(
        @Path("userId") userId: String,
        @Body request: AddSavingsRequest,
        @Header("Authorization") token: String
    ): SavingsResponse

    @PUT("savings/{userId}/reduce")
    suspend fun reduceSavings(
        @Path("userId") userId: String,
        @Body request: ReduceSavingsRequest,
        @Header("Authorization") token: String
    ): SavingsResponse

}