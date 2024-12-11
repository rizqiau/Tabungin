package com.example.ones.data.repository

import android.util.Log
import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.remote.request.PredictRequest
import com.example.ones.data.remote.response.PredictResponse

class PredictRepository(private val apiService: ApiService) {

    suspend fun predictSavings(request: PredictRequest): PredictResponse {
        try {
            Log.d("PredictRepository", "Request: $request")
            return apiService.predict(request)
        } catch (e: Exception) {
            Log.e("PredictRepository", "Error predicting: ${e.message}")
            throw e
        }
    }
}



