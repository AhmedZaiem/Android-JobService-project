package com.example.projet.data.repository

import com.example.projet.data.api.AuthApi
import com.example.projet.data.api.RetrofitClient
import com.example.projet.data.model.LoginRequest
import com.example.projet.data.model.RegisterRequest

class AuthRepository {

    private val authApi = RetrofitClient.instance.create(AuthApi::class.java)

    suspend fun login(loginRequest: LoginRequest) = authApi.login(loginRequest)

    suspend fun register(registerRequest: RegisterRequest) = authApi.register(registerRequest)

    suspend fun getCategories() = authApi.getCategories()
}
