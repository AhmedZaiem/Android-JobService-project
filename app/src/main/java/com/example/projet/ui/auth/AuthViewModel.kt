package com.example.projet.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet.data.model.LoginRequest
import com.example.projet.data.model.LoginResponse
import com.example.projet.data.model.MessageResponse
import com.example.projet.data.model.RegisterRequest
import com.example.projet.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Result<MessageResponse>>()
    val registerResult: LiveData<Result<MessageResponse>> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = authRepository.login(loginRequest)
                _loginResult.postValue(Result.success(response))
            } catch (e: Exception) {
                _loginResult.postValue(Result.failure(e))
            }
        }
    }

    fun register(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            try {
                val registerRequest = RegisterRequest(name, email, password, role)
                val response = authRepository.register(registerRequest)
                _registerResult.postValue(Result.success(response))
            } catch (e: Exception) {
                _registerResult.postValue(Result.failure(e))
            }
        }
    }
}
