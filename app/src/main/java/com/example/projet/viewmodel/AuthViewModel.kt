package com.example.projet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet.data.model.Category
import com.example.projet.data.model.LoginRequest
import com.example.projet.data.model.RegisterRequest
import com.example.projet.data.model.ResetPasswordRequest
import com.example.projet.data.model.UpdatePasswordRequest
import com.example.projet.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    // ---------------------------
    // Loading state
    // ---------------------------
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // ---------------------------
    // Operation Status
    // ---------------------------
    private val _operationStatus = MutableLiveData<Result<String>>()
    val operationStatus: LiveData<Result<String>> = _operationStatus

    // ---------------------------
    // Login state
    // ---------------------------
    private val _loginSuccess = MutableLiveData(false)
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // ---------------------------
    // Logged-in user info
    // ---------------------------
    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> = _userRole

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId

    // ---------------------------
    // Registration result
    // ---------------------------
    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    // ---------------------------
    // Categories
    // ---------------------------
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories


    // ==============================
    // LOAD CATEGORIES
    // ==============================
    fun getCategories() {
        viewModelScope.launch {
            try {
                _categories.value = repository.getCategories()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to load categories", e)
            }
        }
    }


    // ==============================
    // LOGIN
    // ==============================
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please enter email and password"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val request = LoginRequest(email, password)
                val response = repository.login(request)

                // Store info
                _userRole.value = response.role
                _userId.value = response._id

                Log.d("AuthViewModel", "User logged in with id=${response._id}, role=${response.role}")

                _loginSuccess.value = true

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e)
                _errorMessage.value = "Login failed: ${e.message}"
                _loginSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }


    // ==============================
    // REGISTER
    // ==============================
    fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        city: String?,
        tel: String?,
        category: String?,
        skills: String?,
        bio: String?
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _registerResult.value = Result.failure(Exception("Please fill required fields"))
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = RegisterRequest(
                    name, email, password, role, city, tel, category, skills, bio
                )
                val response = repository.register(request)
                _registerResult.value = Result.success(response.message)

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e)
                _registerResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==============================
    // RESET PASSWORD
    // ==============================
    fun resetPassword(request: ResetPasswordRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.resetPassword(request)
                _operationStatus.value = Result.success(response.message)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Reset password failed", e)
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==============================
    // UPDATE PASSWORD
    // ==============================
    fun updatePassword(userId: String, request: UpdatePasswordRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.updatePassword(userId, request)
                _operationStatus.value = Result.success(response.message)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Update password failed", e)
                _operationStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    // ==============================
    // HELPERS
    // ==============================
    fun onNavigationComplete() {
        // Reset navigation flag only
        _loginSuccess.value = false
    }

    fun logout() {
        _userRole.value = null
        _userId.value = null
        _loginSuccess.value = false
        _errorMessage.value = null
    }
}
