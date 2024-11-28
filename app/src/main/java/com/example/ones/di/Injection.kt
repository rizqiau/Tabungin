package com.example.ones.di

import android.content.Context
import com.example.ones.data.preferences.UserPreference
import com.example.ones.data.preferences.dataStore
import com.example.ones.data.remote.api.ApiService
import com.example.ones.data.repository.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Injection {
    private var retrofit: Retrofit? = null

    private fun provideRetrofit(): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder()
                .baseUrl("https://tabungin-api-66486896293.asia-southeast2.run.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().also { retrofit = it }
        }
    }

    private fun provideApiService(): ApiService {
        return provideRetrofit().create(ApiService::class.java)
    }

    fun provideRepository(context: Context): UserRepository {
        val apiService = provideApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(apiService, pref)
    }
}