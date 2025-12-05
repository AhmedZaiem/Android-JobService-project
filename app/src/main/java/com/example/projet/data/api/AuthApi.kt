package com.example.projet.data.api

import com.example.projet.data.model.Category
import com.example.projet.data.model.LoginRequest
import com.example.projet.data.model.LoginResponse
import com.example.projet.data.model.MessageResponse
import com.example.projet.data.model.RegisterRequest
import com.example.projet.data.model.ResetPasswordRequest
import com.example.projet.data.model.ResetPasswordResponse
import com.example.projet.data.model.UpdatePasswordRequest
import com.example.projet.data.model.UpdatePasswordResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): MessageResponse

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): ResetPasswordResponse

    @PUT("auth/update-password/{userId}")
    suspend fun updatePassword(
        @Path("userId") userId: String,
        @Body request: UpdatePasswordRequest
    ): UpdatePasswordResponse



    @GET("admin/categories")
    suspend fun getCategories(): List<Category>




}
