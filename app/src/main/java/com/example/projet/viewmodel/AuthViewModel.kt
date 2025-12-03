package com.example.projet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet.data.model.Category
import com.example.projet.data.model.LoginRequest
import com.example.projet.data.model.RegisterRequest
import com.example.projet.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> = _userRole

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    fun getCategories() {
        viewModelScope.launch {
            try {
                val result = repository.getCategories()
                _categories.value = result
            } catch (e: Exception) {
                // Handle error silently or log it
            }
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Please enter email and password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val loginRequest = LoginRequest(email, pass)
                val response = repository.login(loginRequest)
                
                _userRole.value = response.role
                _loginSuccess.value = true

            } catch (e: Exception) {
                _errorMessage.value = "Login failed: ${e.message}"
                _loginSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun register(name: String, email: String, pass: String, role: String, city: String?, tel: String?, category: String?, skills: String?, bio: String?) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _registerResult.value = Result.failure(Exception("Please fill required fields"))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = RegisterRequest(name, email, pass, role, city, tel, category, skills, bio)
                val response = repository.register(request)
                _registerResult.value = Result.success(response.message)
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onNavigationComplete() {
        _loginSuccess.value = false
        _userRole.value = null
    }
}
