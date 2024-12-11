package com.example.ones.data.remote.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val NEWS_BASE_URL = "https://newsapi.org/"
    private const val SAVINGS_BASE_URL = "https://tabungin-66486896293.asia-southeast2.run.app"
    private const val PREDICT_BASE_URL = "https://tabunginmodel-66486896293.asia-southeast2.run.app"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val errorInterceptor = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: "Unknown error (${response.code})"
            Log.e("RetrofitInstance", "Error: $errorBody")
        }
        response
    }

    val headerInterceptor = Interceptor { chain ->
        val request = chain.request()
        Log.d("RetrofitInstance", "Headers: ${request.headers}")
        chain.proceed(request)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(errorInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val newsApiService: ApiService by lazy {
        createRetrofit(NEWS_BASE_URL).create(ApiService::class.java)
    }

    val savingsApiService: ApiService by lazy {
        createRetrofit(SAVINGS_BASE_URL).create(ApiService::class.java)
    }

    val predictApiService: ApiService by lazy {
        createRetrofit(PREDICT_BASE_URL).create(ApiService::class.java)
    }
}
