package com.example.ones.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.data.model.UserModel
import com.example.ones.data.model.Result
import com.example.ones.data.remote.response.LoginResponse
import com.example.ones.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _signupResult = MutableLiveData<kotlin.Result<String>>()
    val signupResult: LiveData<kotlin.Result<String>> = _signupResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val response = userRepository.login(email, password)
                val token = response.data.user.stsTokenManager.accessToken
                val userId = response.data.user.uid  // Mengambil userId dari response
                val user = UserModel(
                    email = email,
                    token = token,
                    userId = userId,  // Menyimpan userId
                    isLogin = true
                )
                userRepository.saveSession(user)
                _loginResult.value = Result.Success(response)
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.register(name, email, password)

                if (response.message.contains("success", ignoreCase = true)) {
                    _signupResult.value = kotlin.Result.success(response.message)
                } else {
                    _signupResult.value = kotlin.Result.failure(Exception(response.message))
                }
            } catch (e: Exception) {
                _signupResult.value = kotlin.Result.failure(e)
            }
        }
    }
}
