package com.example.ones.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // Base URL untuk News API
    private const val NEWS_BASE_URL = "https://newsapi.org/"

    // Base URL untuk Auth dan Savings API
    private const val SAVINGS_BASE_URL = "https://tabungin-66486896293.asia-southeast2.run.app"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Retrofit instance untuk News API
    private val newsRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NEWS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
            .build()
    }

    // Retrofit instance untuk Auth dan Savings API
    private val savingsRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SAVINGS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
            .build()
    }

    // API service untuk News
    val newsApiService: ApiService by lazy {
        newsRetrofit.create(ApiService::class.java)
    }

    // API service untuk Auth dan Savings
    val savingsApiService: ApiService by lazy {
        savingsRetrofit.create(ApiService::class.java)
    }
}
