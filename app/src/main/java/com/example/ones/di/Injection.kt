package com.example.ones.di

import android.content.Context
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.preferences.dataStore
import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.remote.api.RetrofitInstance
import com.example.ones.data.repository.UserRepository
import com.example.ones.data.repository.SavingsRepository
import com.example.ones.data.repository.NewsRepository
import com.example.ones.utils.ViewModelFactory

object Injection {

    // Menyediakan ApiService untuk News
    fun provideNewsApiService(): ApiService {
        return RetrofitInstance.newsApiService // Menggunakan RetrofitInstance untuk News API
    }

    // Menyediakan ApiService untuk login, register, savings
    fun provideAuthApiService(): ApiService {
        return RetrofitInstance.savingsApiService // Menggunakan RetrofitInstance untuk Auth dan Savings API
    }

    // Menyediakan UserRepository dengan apiService yang sesuai untuk user-related actions (login, register, dsb)
    fun provideUserRepository(context: Context): UserRepository {
        val apiService = provideAuthApiService() // Gunakan API service yang sesuai untuk login dan register
        val preferences = UserPreference.getInstance(context.dataStore)
        return UserRepository(apiService, preferences)
    }

    // Menyediakan SavingsRepository dengan apiService yang sesuai untuk savings-related actions
    fun provideSavingsRepository(context: Context): SavingsRepository {
        val apiService = provideAuthApiService() // Menggunakan API service yang sama untuk savings
        val preferences = UserPreference.getInstance(context.dataStore)
        return SavingsRepository(apiService, preferences)
    }

    // Menyediakan NewsRepository untuk berita
    fun provideNewsRepository(): NewsRepository {
        val apiService = provideNewsApiService() // Menggunakan API service untuk berita
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

