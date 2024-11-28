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

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val response = userRepository.login(email, password)
                val token = response.data.user.stsTokenManager.accessToken
                val user = UserModel(
                    email = email,
                    token = token,
                    isLogin = true
                )
                userRepository.saveSession(user)
                _loginResult.value = Result.Success(response)
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}
