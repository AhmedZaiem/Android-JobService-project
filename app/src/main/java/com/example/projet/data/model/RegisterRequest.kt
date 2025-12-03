package com.example.projet.data.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val city: String? = null,
    val tel: String? = null,
    val category: String? = null,
    val skills: String? = null,
    val bio: String? = null
)