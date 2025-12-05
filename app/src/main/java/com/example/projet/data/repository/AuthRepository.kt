package com.example.projet.data.repository

import com.example.projet.data.api.AuthApi
import com.example.projet.data.api.RetrofitClient
import com.example.projet.data.model.LoginRequest
import com.example.projet.data.model.RegisterRequest
import com.example.projet.data.model.ResetPasswordRequest
import com.example.projet.data.model.UpdatePasswordRequest

class AuthRepository {

    private val authApi = RetrofitClient.instance.create(AuthApi::class.java)

    suspend fun login(loginRequest: LoginRequest) = authApi.login(loginRequest)

    suspend fun register(registerRequest: RegisterRequest) = authApi.register(registerRequest)

    suspend fun resetPassword(request: ResetPasswordRequest) =
        authApi.resetPassword(request)

    suspend fun updatePassword(userId: String, request: UpdatePasswordRequest) =
        authApi.updatePassword(userId, request)


    suspend fun getCategories() = authApi.getCategories()
}
