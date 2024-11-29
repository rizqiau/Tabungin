package com.example.ones.di

import android.content.Context
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.preferences.dataStore
import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.repository.UserRepository
import com.example.ones.data.repository.SavingsRepository
import com.example.ones.data.repository.NewsRepository
import com.example.ones.utils.ViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {

    // Utility function untuk menyediakan Retrofit dengan base URL yang diberikan
    private fun provideRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
            .build()
    }

    // Menyediakan ApiService untuk berita
    fun provideNewsApiService(): ApiService {
        return provideRetrofit("https://newsapi.org/").create(ApiService::class.java)
    }

    // Menyediakan ApiService untuk savings
    fun provideSavingsApiService(): ApiService {
        return provideRetrofit("https://tabungin-api-66486896293.asia-southeast2.run.app/")
            .create(ApiService::class.java)
    }

    // Menyediakan UserRepository dengan apiService yang sesuai untuk user-related actions (login, register, dsb)
    fun provideUserRepository(context: Context): UserRepository {
        val apiService = provideNewsApiService() // Harus menggunakan API service yang benar untuk UserRepository
        val preferences = UserPreference.getInstance(context.dataStore)
        return UserRepository(apiService, preferences)
    }

    // Menyediakan SavingsRepository dengan apiService yang sesuai untuk savings-related actions
    fun provideSavingsRepository(context: Context): SavingsRepository {
        val apiService = provideSavingsApiService() // Menggunakan API service khusus untuk savings
        val preferences = UserPreference.getInstance(context.dataStore)
        return SavingsRepository(apiService, preferences)
    }

    // Menyediakan NewsRepository untuk berita
    fun provideNewsRepository(): NewsRepository {
        val apiService = provideNewsApiService() // Menggunakan API service khusus untuk berita
        return NewsRepository(apiService)
    }

    // Menyediakan ViewModelFactory dengan dependencies yang tepat
    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val userRepository = provideUserRepository(context)
        val savingsRepository = provideSavingsRepository(context)
        val newsRepository = provideNewsRepository() // Menyuntikkan NewsRepository juga
        return ViewModelFactory(userRepository, savingsRepository, newsRepository)
    }
}
