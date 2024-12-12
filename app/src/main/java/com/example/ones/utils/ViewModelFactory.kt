package com.example.ones.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ones.data.repository.GoalsRepository
import com.example.ones.data.repository.NewsRepository
import com.example.ones.data.repository.PredictRepository
import com.example.ones.data.repository.SavingsRepository
import com.example.ones.data.repository.UserRepositoryInterface
import com.example.ones.di.Injection.provideViewModelFactory
import com.example.ones.viewmodel.auth.AuthViewModel
import com.example.ones.viewmodel.goals.GoalsViewModel
import com.example.ones.viewmodel.home.HomeViewModel
import com.example.ones.viewmodel.predict.PredictViewModel
import com.example.ones.viewmodel.report.MonthlyReportViewModel
import com.example.ones.viewmodel.transaction.TransactionViewModel

class ViewModelFactory(
    private val userRepository: UserRepositoryInterface,
    private val savingsRepository: SavingsRepository,
    private val newsRepository: NewsRepository,
    private val goalsRepository: GoalsRepository,
    private val predictRepository: PredictRepository // Tambahkan PredictRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository, savingsRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(savingsRepository, newsRepository, predictRepository) as T // Inject NewsRepository ke HomeViewModel
            }
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                TransactionViewModel(savingsRepository) as T
            }
            modelClass.isAssignableFrom(GoalsViewModel::class.java) -> {
                GoalsViewModel(goalsRepository) as T
            }
            modelClass.isAssignableFrom(PredictViewModel::class.java) -> { // Tambahkan PredictViewModel
                PredictViewModel(predictRepository, savingsRepository) as T
            }
            modelClass.isAssignableFrom(MonthlyReportViewModel::class.java) -> { // Tambahkan PredictViewModel
                MonthlyReportViewModel(savingsRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = provideViewModelFactory(context)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
