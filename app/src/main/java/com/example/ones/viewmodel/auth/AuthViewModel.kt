package com.example.ones.viewmodel.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.data.model.UserModel
import com.example.ones.data.model.Result
import com.example.ones.data.remote.response.LoginResponse
import com.example.ones.data.repository.SavingsRepository
import com.example.ones.data.repository.UserRepositoryInterface
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel(
    private val userRepository: UserRepositoryInterface,
    private val savingsRepository: SavingsRepository
    ) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _signupResult = MutableLiveData<Result<String>>()
    val signupResult: LiveData<Result<String>> get() = _signupResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val response = userRepository.login(email, password)
                val token = response.data.user.stsTokenManager.accessToken
                val userId = response.data.user.uid


                val user = UserModel(
                    email = email,
                    token = token,
                    userId = userId,  // Menyimpan userId
                    isLogin = true
                )
                userRepository.saveSession(user)
                Log.d("AuthViewModel", "Saving session: userId=$userId, token=$token")
                _loginResult.value = Result.Success(response)
            } catch (e: Exception) {
                _loginResult.value = Result.Error(handleError(e))
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _signupResult.value = Result.Loading // Emit Loading state
            try {
                val response = userRepository.register(name, email, password)

                if (response.message.contains("success", ignoreCase = true)) {
                    _signupResult.value = Result.Success(response.message) // Emit Success state
                } else {
                    _signupResult.value = Result.Error(response.message) // Emit Error state
                }
            } catch (e: Exception) {
                _signupResult.value = Result.Error(e.message ?: "An unexpected error occurred") // Emit Error state
            }
        }
    }

    private fun handleError(exception: Exception): String {
        return when (exception) {
            is IOException -> "Network error. Please check your internet connection."
            is HttpException -> "Server error: ${exception.response()?.errorBody()?.string()}"
            else -> "An unexpected error occurred."
        }
    }
}
