package com.example.projet.data.model

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("_id") val id: String,
    val rating: Int,
    val comment: String,
    val providerId: String,
    val customerId: ReviewCustomer?,
    val serviceId: ReviewService?
)

data class ReviewCustomer(
    @SerializedName("_id") val id: String,
    val name: String?
)

// Renamed to avoid conflict with com.example.projet.data.model.Service
data class ReviewService(
    @SerializedName("_id") val id: String,
    val title: String?
)