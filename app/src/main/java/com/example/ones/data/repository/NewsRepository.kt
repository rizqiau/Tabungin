package com.example.ones.data.repository

import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.remote.api.RetrofitInstance
import com.example.ones.data.remote.response.NewsResponse

class NewsRepository(private val apiService: ApiService) {

    suspend fun getTopHeadlines(
        country: String,
        category: String,
        apiKey: String
    ): NewsResponse {
        return apiService.getTopHeadlines(country, category, apiKey)
    }
}