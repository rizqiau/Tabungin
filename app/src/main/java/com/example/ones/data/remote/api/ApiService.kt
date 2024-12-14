package com.example.ones.data.remote.api

import com.example.ones.data.remote.request.AddGoalAmountRequest
import com.example.ones.data.remote.request.AddGoalsRequest
import com.example.ones.data.remote.request.AddSavingsRequest
import com.example.ones.data.remote.request.LoginRequest
import com.example.ones.data.remote.request.PredictRequest
import com.example.ones.data.remote.request.ReduceSavingsRequest
import com.example.ones.data.remote.request.RegisterRequest
import com.example.ones.data.remote.request.UpdateGoalAmountRequest
import com.example.ones.data.remote.request.UpdateGoalRequest
import com.example.ones.data.remote.request.UpdateSavingsRequest
import com.example.ones.data.remote.response.AddGoalsResponse
import com.example.ones.data.remote.response.LoginResponse
import com.example.ones.data.remote.response.NewsResponse
import com.example.ones.data.remote.response.PredictResponse
import com.example.ones.data.remote.response.RegisterResponse
import com.example.ones.data.remote.response.SavingsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @PUT("savings/{userId}/{savingId}/{transactionId}")
    suspend fun updateSavings(
        @Path("userId") userId: String,
        @Path("savingId") savingId: String,
        @Path("transactionId") transactionId: String,
        @Body request: UpdateSavingsRequest,
        @Header("Authorization") token: String
    ): SavingsResponse

    @DELETE("savings/{userId}/{savingId}/{transactionId}")
    suspend fun deleteSavings(
        @Path("userId") userId: String,
        @Path("savingId") savingId: String,
        @Path("transactionId") transactionId: String,
        @Header("Authorization") token: String
    ): SavingsResponse

    @POST("goals/{userId}/{savingId}/{goalId}")
    suspend fun addGoalAmount(
        @Path("userId") userId: String,
        @Path("savingId") savingId: String,
        @Path("goalId") goalId: String,
        @Body request: AddGoalAmountRequest,
        @Header("Authorization") token: String
    ): SavingsResponse

    @POST("goals/{userId}/{savingId}")
    suspend fun addGoals(
        @Path("userId") userId: String,
        @Path("savingId") savingId: String,
        @Body request: AddGoalsRequest,
        @Header("Authorization") token: String
    ): AddGoalsResponse

    @PUT("goals/{userId}/{savingId}/{goalId}")
    suspend fun updateGoalAmount(
        @Path("userId") userId: String,
        @Path("savingId") savingId: String,
        @Path("goalId") goalId: String,
        @Body request: UpdateGoalAmountRequest,
        @Header("Authorization") token: String
    ): SavingsResponse

    @PUT("goals/{userId}/{savingId}/{goalId}")
    suspend fun updateGoal(
        @Path("userId") userId: String,
        @Path("savingId") savingId: String,
        @Path("goalId") goalId: String,
        @Body request: UpdateGoalRequest,
        @Header("Authorization") token: String
    ): SavingsResponse

    @DELETE("goals/{userId}/{savingId}/{goalId}")
    suspend fun deleteGoal(
        @Path("userId") userId: String,
        @Path("savingId") savingId: String,
        @Path("goalId") goalId: String,
        @Header("Authorization") token: String
    )

    @POST("predict")
    suspend fun predict(
        @Body request: PredictRequest,
    ): PredictResponse
}