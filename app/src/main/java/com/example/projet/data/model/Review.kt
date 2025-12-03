package com.example.projet.data.model

data class Review(
    val rating: Int,
    val comment: String,
    val customerId: User,
    val serviceId: Service
)

