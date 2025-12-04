package com.example.projet.data.model

import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("_id") val id: String,
    val date: String?,
    val status: String?,
    val customerId: Customer?,
    val serviceId: BookingService?,
    val providerId: String?
)

data class Customer(
    @SerializedName("_id") val id: String,
    val name: String?
)

// Renamed to avoid conflict with com.example.projet.data.model.Service
data class BookingService(
    @SerializedName("_id") val id: String,
    val title: String?
)
