package com.example.projet.data.model

data class ReviewRequest(
    val customerId: String,
    val rating: Int,
    val comment: String
)

