package com.example.projet.data.api

import com.example.projet.data.model.LoginRequest
import com.example.projet.data.model.LoginResponse
import com.example.projet.data.model.MessageResponse
import com.example.projet.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): MessageResponse
}
