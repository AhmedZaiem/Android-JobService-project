package com.example.projet.data.model

data class ReviewRequest(
    val reservationId: String,
    val customerId: String,
    val rating: Int,
    val comment: String
)


