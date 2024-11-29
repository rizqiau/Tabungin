package com.example.ones.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://newsapi.org/"  // Base URL default

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)  // Base URL default
            .addConverterFactory(GsonConverterFactory.create())  // Gson converter
            .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
            .build()
    }

    // ApiService digunakan untuk berita
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Jika ingin menggunakan API untuk Savings, buat instance Retrofit berbeda dengan base URL yang sesuai
    fun provideSavingsApiService(): ApiService {
        val savingsRetrofit = retrofit.newBuilder()
            .baseUrl("https://tabungin-api-66486896293.asia-southeast2.run.app/")  // Base URL untuk Savings API
            .build()

        return savingsRetrofit.create(ApiService::class.java)
    }
}
